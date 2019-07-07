package example.firoz.notepadofflineonlinesyncreal2.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import example.firoz.notepadofflineonlinesyncreal2.WelcomeActivity;
import example.firoz.notepadofflineonlinesyncreal2.adapter.NotepadAdapter;
import example.firoz.notepadofflineonlinesyncreal2.helper.NetworkStateChecker;
import example.firoz.notepadofflineonlinesyncreal2.R;
import example.firoz.notepadofflineonlinesyncreal2.data.ServiceRequest;
import example.firoz.notepadofflineonlinesyncreal2.helper.DatabaseHelper;
import example.firoz.notepadofflineonlinesyncreal2.interfaces.OnLongClickCallback;
import example.firoz.notepadofflineonlinesyncreal2.model.Notes;
import example.firoz.notepadofflineonlinesyncreal2.utilities.PublicValues;
import example.firoz.notepadofflineonlinesyncreal2.utilities.SessionManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnLongClickCallback {

    private SessionManager session;
    private TextView txt_name, txt_no_notes;
    private int userid;
    ImageView btn_more;
    private Context context;

    FloatingActionButton fab;

    /*
     * this is the url to our webservice
     * make sure you are using the ip instead of localhost
     * it will not work if you are using localhost
     * */
    public static final String URL_SAVE_NAME = "https://webhelpers.in/NotepadOfflineOnlineSync/saveName.php";

    //database helper object
    private DatabaseHelper db;

    //recyclerview
    //related products
    //recyclerview
    RecyclerView recyclerView_notepad;
    //arraylist
    private List<Notes> notesList;
    //adapters
    private NotepadAdapter notepadAdapter;

    //1 means data is synced and 0 means data is not synced
    public static final int NOTES_SYNCED_WITH_SERVER = 1;
    public static final int NOTES_NOT_SYNCED_WITH_SERVER = 0;

    public static final int NOTES_DELETED_FROM_SERVER = 2;
    public static final int NOTES_NOT_DELETED_FROM_SERVER = 1;

    public static final int NOTES_UPDATED_TO_SERVER = 2;
    public static final int NOTES_NOT_UPDATED_TO_SERVER = 1;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "in.webhelpers.datasaved";
    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver broadcastReceiverSelectionChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context= this;

        //initializing views and objects
        db = new DatabaseHelper(this);
        notesList= new ArrayList<>();

        fab= findViewById(R.id.fab);
        txt_no_notes= findViewById(R.id.txt_no_notes);
        btn_more= findViewById(R.id.btn_more);
        recyclerView_notepad= (RecyclerView) findViewById(R.id.recycler_notepad);

        //horizontal layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        //grid layout manager
        //GridLayoutManager adsGridLayoutManager= new GridLayoutManager(getActivity(),2);
        recyclerView_notepad.setLayoutManager(mLayoutManager);
        recyclerView_notepad.setScrollContainer(false);
        recyclerView_notepad.setNestedScrollingEnabled(false);

        session = new SessionManager(getApplicationContext());

        if(session.isLoggedIn())
        {

            String welcomeName  = session.getName();
            Log.d("Name value ",welcomeName);
            welcomeName = welcomeName.substring(0,1).toUpperCase() + welcomeName.substring(1).toLowerCase();

            //getSupportActionBar().setSubtitle(welcomeName);
            //actionBar.setSubtitle(welcomeName);

            userid= session.getUserId();

            //txt_name_nav.setText("Hi, " + welcomeName);
            //txt_name_nav.setVisibility(View.VISIBLE);
            //userLoginSignupLayout.setVisibility(View.GONE);
            //logout_layout.setVisibility(View.VISIBLE);

/*            if(role.equals("ADMIN"))
            {
                home("ADMIN");

            }
            else
            {
                home("USER");
            }*/
        }

        //adding click listener to button
        fab.setOnClickListener(this);
        btn_more.setOnClickListener(this);

        //calling the method to load all the stored names
        loadNames();

        //the broadcast receiver to update sync status
        //receiving the sended broadcast from NetworkStateChecker broadcast receiver to refresh the list after sync
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                loadNames();
            }
        };


        //registering the user made broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
        //registering the connectivity broadcast receiver
        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /*
     * this method will
     * load the names from the database
     * with updated sync status
     * */
    private void loadNames() {
        notesList.clear();
        Cursor cursor = db.getNames();
        if (cursor.moveToFirst()) {
            do {
                Notes note = new Notes(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_UNIQUE_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTES)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_UPDATED_AT)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DELETE_STATUS))
                );
                notesList.add(note);
            } while (cursor.moveToNext());
        }

        notepadAdapter= new NotepadAdapter(this, notesList, this);
        recyclerView_notepad.setAdapter(notepadAdapter);

        checkIfNotesEmpty();
    }

    /*
     * this method will simply refresh the list
     * */
    private void refreshList() {
        notepadAdapter.notifyDataSetChanged();
        checkIfNotesEmpty();
    }

    @Override
    public void onClick(View view) {

        if(view== fab)
        {
            Intent i= new Intent(MainActivity.this, AddActivity.class);
            startActivity(i);
        }

        if (view==btn_more)
        {
            /*
                //popup menu position setting two methods are
    1.            To overlap only, use this approach:
    PopupMenu popup= new PopupMenu(MainActivity.this, view, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);

    2. To get a PopupMenu with a bright background and a detailed control over the offsets use this approach:
styles.xml

<style name="PopupMenuOverlapAnchor" parent="@style/Theme.AppCompat.Light">
   <item name="android:overlapAnchor">true</item>
   <item name="android:dropDownVerticalOffset">0dp</item>
   <item name="android:dropDownHorizontalOffset">0dp</item>
</style>
Code:

ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuOverlapAnchor);
PopupMenu popupMenu = new PopupMenu(contextThemeWrapper, this);
             */

            if(session.isLoggedIn())
            {
                String welcomeName  = session.getName();
                welcomeName = welcomeName.substring(0,1).toUpperCase() + welcomeName.substring(1).toLowerCase();

                final PopupMenu popup= new PopupMenu(MainActivity.this, view, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);
                //final PopupMenu popup = new PopupMenu(MainActivity.this, view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_toolbar_logged_in, popup.getMenu());
                popup.getMenu().findItem(R.id.welcome_name_toolbar_menu).setTitle("Hi "+welcomeName);
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        if(item.getItemId()==R.id.about_toolbar_menu)
                        {
                            Intent i= new Intent(MainActivity.this, AboutActivity.class);
                            startActivity(i);
                        }
                        else
                        if(item.getItemId()==R.id.logout_toolbar_menu)
                        {
                            logout();
                        }
                        else
                        if(item.getItemId()==R.id.sync_toolbar_menu)
                        {
                            syncDataManually();
                        }

                        return true;
                    }
                });

                //force popp menu to show icon
                try {
                    Method method = popup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                    method.setAccessible(true);
                    method.invoke(popup.getMenu(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popup.show();//showing popup menu
            }
            else
            {
                final PopupMenu popup= new PopupMenu(MainActivity.this, view, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_toolbar_logged_out, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        if(item.getItemId()==R.id.about_toolbar_menu)
                        {
                            Intent i= new Intent(MainActivity.this, AboutActivity.class);
                            startActivity(i);
                        }
                        else
                        if(item.getItemId()==R.id.login_toolbar_menu)
                        {
                            Intent i= new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                        }

                        return true;
                    }
                });

                //force popup menu to show icon
                try {
                    Method method = popup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                    method.setAccessible(true);
                    method.invoke(popup.getMenu(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popup.show();//showing popup menu
            }
        }
    }

    private void logout() {
        session.setLogin(false);
        session.clear();

        Toast.makeText(getApplicationContext(),"Logout Successfull!", Toast.LENGTH_LONG).show();

        Intent i= new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void syncDataManually() {

        if(session.isLoggedIn())
        {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            //if there is a network
            if (activeNetwork != null) {
                //if connected to wifi or mobile data plan
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                    //getting all the unsynced names
                    Cursor cursor = db.getUnsyncedNotes();
                    if (cursor.moveToFirst()) {
                        do {
                            //calling the method to save the unsynced name to MySQL
                            saveName(
                                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_UNIQUE_ID)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTES)),
                                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT))
                            );
                        } while (cursor.moveToNext());
                    }

                    //getting all undeleted notes
                    Cursor cursorUndeleted = db.getUnDeletedNotes();
                    if (cursorUndeleted.moveToFirst()) {
                        do {
                            //calling the method to save the unsynced name to MySQL
                            deleteNotes(
                                    cursorUndeleted.getString(cursorUndeleted.getColumnIndex(DatabaseHelper.COLUMN_UNIQUE_ID)),
                                    cursorUndeleted.getInt(cursorUndeleted.getColumnIndex(DatabaseHelper.COLUMN_USER_ID))
                            );
                        } while (cursorUndeleted.moveToNext());
                    }

                    //getting all unupdated notes
                    Cursor cursorUnupdated = db.getUnUpdatedNotes();
                    if (cursorUnupdated.moveToFirst()) {
                        do {
                            //calling the method to save the unsynced name to MySQL
                            updateNotes(
                                    cursorUnupdated.getString(cursorUnupdated.getColumnIndex(DatabaseHelper.COLUMN_UNIQUE_ID)),
                                    cursorUnupdated.getString(cursorUnupdated.getColumnIndex(DatabaseHelper.COLUMN_NOTES)),
                                    cursorUnupdated.getString(cursorUnupdated.getColumnIndex(DatabaseHelper.COLUMN_UPDATED_AT)),
                                    cursorUnupdated.getInt(cursorUnupdated.getColumnIndex(DatabaseHelper.COLUMN_USER_ID))
                            );
                        } while (cursorUnupdated.moveToNext());
                    }
                }
            }

        }

    }

    @Override
    public void onLongClickContextMenu(final int position, View v) {
        //Notes note= notesList.get(position);
        //Toast.makeText(MainActivity.this, note.getNotes().toString(), Toast.LENGTH_LONG).show();
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(MainActivity.this, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                if(item.getItemId()==R.id.edit)
                {
                    Toast.makeText(MainActivity.this, notesList.get(position).getDelete_status() + " Edited", Toast.LENGTH_LONG).show();
                    Intent i= new Intent(MainActivity.this, EditActivity.class);
                    i.putExtra("unique_id", notesList.get(position).getUnique_id());
                    i.putExtra("notes", notesList.get(position).getNotes());
                    startActivity(i);
                }
                else
                if(item.getItemId()==R.id.delete)
                {
                    Toast.makeText(MainActivity.this, notesList.get(position).getNotes() + " Deleted", Toast.LENGTH_LONG).show();
                    deleteNoteFromServer(notesList.get(position).getUnique_id(), position);
                }

                return true;
            }
        });

        popup.show();//showing popup menu
    }

    private void deleteNoteFromServer(final String unique_id, final int position)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting Note...");
        progressDialog.show();

        final int userid = session.getUserId();
        //final String created_at = "2019-03-23 11:00:40";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PublicValues.URL_NOTEPAD_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response.toString());
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the name to sqlite with status synced
                                deleteNoteFromLocalStorage(unique_id, NOTES_DELETED_FROM_SERVER, position);
                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                deleteNoteFromLocalStorage(unique_id, NOTES_NOT_DELETED_FROM_SERVER, position);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //on error storing the name to sqlite with status unsynced
                        deleteNoteFromLocalStorage(unique_id, NOTES_NOT_DELETED_FROM_SERVER, position);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("unique_id", unique_id);
                params.put("userid", String.valueOf(userid));
                return params;
            }
        };

        ServiceRequest.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void deleteNoteFromLocalStorage(String unique_id, int deleteStatus, int position)
    {
        if(deleteStatus==NOTES_NOT_DELETED_FROM_SERVER)
        {
            db.updateNotesDeleteStatus(unique_id, deleteStatus);
        }
        else if(deleteStatus==NOTES_DELETED_FROM_SERVER)
        {
            db.deleteNotes(unique_id, deleteStatus);
        }
        notesList.remove(position);
        refreshList();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        loadNames();
    }

    public void checkIfNotesEmpty()
    {
        if(notesList.size() ==0 || notesList.isEmpty() || notesList==null)
        {
            txt_no_notes.setVisibility(View.VISIBLE);
            recyclerView_notepad.setVisibility(View.GONE);
        }
        else
        {
            txt_no_notes.setVisibility(View.GONE);
            recyclerView_notepad.setVisibility(View.VISIBLE);
        }
    }

    //syncing functions
    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */
    private void saveName(final int id, final String unique_id, final String note, final int userid, final String created_at) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PublicValues.URL_NOTEPAD_ADD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateNotesStatus(unique_id, MainActivity.NOTES_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("unique_id", unique_id);
                params.put("notes", note);
                params.put("userid", String.valueOf(userid));
                params.put("created_at", created_at);
                //params.put("updated_at", updated_at);
                return params;
            }
        };

        ServiceRequest.getInstance(context).addToRequestQueue(stringRequest);
    }


    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */
    private void deleteNotes(final String unique_id, final int userid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PublicValues.URL_NOTEPAD_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.deleteNotes(unique_id, 2);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("unique_id", unique_id);
                params.put("userid", String.valueOf(userid));
                return params;
            }
        };

        ServiceRequest.getInstance(context).addToRequestQueue(stringRequest);
    }

    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */
    private void updateNotes(final String unique_id, final String note, final String updated_at, final int user_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PublicValues.URL_NOTEPAD_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateNotesUpdateStatus(unique_id, 2);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("notes", note);
                params.put("unique_id", unique_id);
                params.put("updated_at", updated_at);
                params.put("userid", String.valueOf(user_id));
                return params;
            }
        };

        ServiceRequest.getInstance(context).addToRequestQueue(stringRequest);
    }
}
