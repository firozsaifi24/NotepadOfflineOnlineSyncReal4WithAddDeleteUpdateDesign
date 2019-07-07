package example.firoz.notepadofflineonlinesyncreal2.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import example.firoz.notepadofflineonlinesyncreal2.activity.MainActivity;
import example.firoz.notepadofflineonlinesyncreal2.data.ServiceRequest;
import example.firoz.notepadofflineonlinesyncreal2.utilities.PublicValues;
import example.firoz.notepadofflineonlinesyncreal2.utilities.SessionManager;

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;
    private SessionManager session;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);
        session= new SessionManager(context);

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
