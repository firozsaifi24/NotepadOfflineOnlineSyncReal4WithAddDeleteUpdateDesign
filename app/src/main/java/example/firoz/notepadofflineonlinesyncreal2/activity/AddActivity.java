package example.firoz.notepadofflineonlinesyncreal2.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.EditTextPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import example.firoz.notepadofflineonlinesyncreal2.R;
import example.firoz.notepadofflineonlinesyncreal2.adapter.ListPopupAdapter;
import example.firoz.notepadofflineonlinesyncreal2.data.ServiceRequest;
import example.firoz.notepadofflineonlinesyncreal2.helper.CustomTypefaceSpan;
import example.firoz.notepadofflineonlinesyncreal2.helper.DatabaseHelper;
import example.firoz.notepadofflineonlinesyncreal2.helper.EditTextCursorWatcher;
import example.firoz.notepadofflineonlinesyncreal2.model.Colors;
import example.firoz.notepadofflineonlinesyncreal2.utilities.PublicValues;
import example.firoz.notepadofflineonlinesyncreal2.utilities.SessionManager;
import yuku.ambilwarna.AmbilWarnaDialog;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    //View objects
    private TextView buttonSave;
    private EditTextCursorWatcher editTextNote;
    private ActionMode mActionMode;

    public static final String SELECTION_CHANGE_BROADCAST = "in.webhelpers.selectionchange";
    private BroadcastReceiver broadcastReceiverSelectionChange;

    //list pop window and data
    ListPopupWindow listPopupWindow;
    //ListPopupWindow listPopupWindow2;
    ArrayList<Colors> colorsList;
    ArrayList<Colors> highlighterList;
    String[] brandItems={"Acura", "Alfa romeo", "Aston martin","Audi",
            "BAIC"};
    String[] modelItems={"A1", "A2", "A3","A4",
            "A5"};
    String[] partsItems={"Body Parts", "Engine Parts", "Interior Parts","Mechanical Parts"};
    String[] yearItems={"2018", "2017", "2016","2015","2014","2013"};

    int current_textColor= Color.BLACK;
    int current_textHighlighter= Color.BLACK;

    ImageView btn_bold, btn_italic, btn_underline;
    ImageView btn_highlight, btn_txt_color;
    ImageView btn_superscript, btn_subscript, btn_strikethrough;
    ImageView btn_bullet;
    Button btn_fontfamily, btn_size;
    LinearLayout bold_layout, italic_layout, underline_layout, txt_color_layout, highlight_layout;
    LinearLayout superscript_layout, subscript_layout, strikethrough_layout, bullet_layout;
    String myHtmlDataTo;

    private SessionManager session;
    private int userid;

    //database helper object
    private DatabaseHelper db;

    //1 means data is synced and 0 means data is not synced
    public static final int NOTES_SYNCED_WITH_SERVER = 1;
    public static final int NOTES_NOT_SYNCED_WITH_SERVER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        buttonSave = (TextView) findViewById(R.id.buttonSave);
        editTextNote =  findViewById(R.id.editTextName);
        btn_bold= findViewById(R.id.btn_bold);
        btn_italic= findViewById(R.id.btn_italic);
        btn_underline= findViewById(R.id.btn_underline);
        btn_fontfamily= findViewById(R.id.btn_fontfamily);
        btn_highlight= findViewById(R.id.btn_highlight);
        btn_size= findViewById(R.id.btn_size);
        btn_txt_color= findViewById(R.id.btn_txt_color);
        btn_superscript= findViewById(R.id.btn_superscript);
        btn_subscript= findViewById(R.id.btn_subscript);
        btn_strikethrough= findViewById(R.id.btn_strikethrough);
        btn_bullet= findViewById(R.id.btn_bullet);

        bold_layout= findViewById(R.id.bold_layout);
        italic_layout= findViewById(R.id.italic_layout);
        underline_layout= findViewById(R.id.underline_layout);
        txt_color_layout= findViewById(R.id.txt_color_layout);
        highlight_layout= findViewById(R.id.highlight_layout);

        superscript_layout= findViewById(R.id.superscript_layout);
        subscript_layout= findViewById(R.id.subscript_layout);
        strikethrough_layout= findViewById(R.id.strikethrough_layout);
        bullet_layout= findViewById(R.id.bullet_layout);

        db = new DatabaseHelper(this);

        colorsList = new ArrayList<>();
        highlighterList= new ArrayList<>();

        for(int i=0; i<=2; i++)
        {

            if(i==0)
            {
                Colors color= new Colors();
                color.setTitle("Current Color");
                color.setIconColor(Color.BLACK);

                colorsList.add(color);
            }

            if(i==1)
            {
                Colors color= new Colors();
                color.setTitle("More Colors...");
                color.setIconColor(10);

                colorsList.add(color);
            }

            if(i==2)
            {
                Colors color= new Colors();
                color.setTitle("Remove Color");
                color.setIconColor(Color.TRANSPARENT);

                colorsList.add(color);
            }
        }

        for(int i=0; i<=2; i++)
        {

            if(i==0)
            {
                Colors color= new Colors();
                color.setTitle("Current Color");
                color.setIconColor(Color.BLACK);

                highlighterList.add(color);
            }

            if(i==1)
            {
                Colors color= new Colors();
                color.setTitle("More Colors...");
                color.setIconColor(10);

                highlighterList.add(color);
            }

            if(i==2)
            {
                Colors color= new Colors();
                color.setTitle("Remove Color");
                color.setIconColor(Color.TRANSPARENT);

                highlighterList.add(color);
            }
        }

        session = new SessionManager(getApplicationContext());

        if(session.isLoggedIn())
        {

            String welcomeName  = session.getName();
            Log.d("Name value ",welcomeName);
            welcomeName = welcomeName.substring(0,1).toUpperCase() + welcomeName.substring(1).toLowerCase();

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

        //editTextNote.setText(Html.fromHtml("<p dir=\"ltr\">sgsgs<span style=\"font-size:35px\";><b><big>hsh</big>dh<small>hd</small></b></span></p>"));
        editTextNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(AddActivity.this, "Seleced", Toast.LENGTH_LONG).show();
                if(mActionMode!=null)
                {
                    return false;
                }

                /*mActionMode= startActionMode(mActionModeCallback);
                //startActionMode(new ActionBarCallBack());
                return true;*/
                return false;
            }
        });

        editTextNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mActionMode!=null)
                {
                    mActionMode.finish();
                    mActionMode=null;
                }

                //checkSpans();


            }
        });

        editTextNote.setCustomSelectionActionModeCallback(new CallbackCustom(editTextNote));

      /*  editTextNote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    *//*case MotionEvent.ACTION_DOWN:
                        Layout layout = ((EditText) v).getLayout();
                        float x = event.getX() + mText.getScrollX();
                        int offset = layout.getOffsetForHorizontal(0, x);
                        if(offset>0)
                            if(x>layout.getLineMax(0))
                                mText.setSelection(offset);     // touch was at end of text
                            else
                                mText.setSelection(offset - 1);
                        break;*//*

                    case MotionEvent.ACTION_DOWN:
                        Toast.makeText(AddActivity.this, "Dragging done", Toast.LENGTH_LONG).show();

                }
                return true;

            }
        });*/

        //adding click listener to button
        buttonSave.setOnClickListener(this);
        btn_bold.setOnClickListener(this);
        btn_italic.setOnClickListener(this);
        btn_underline.setOnClickListener(this);
        btn_fontfamily.setOnClickListener(this);
        btn_highlight.setOnClickListener(this);
        btn_size.setOnClickListener(this);
        btn_txt_color.setOnClickListener(this);
        btn_superscript.setOnClickListener(this);
        btn_subscript.setOnClickListener(this);
        btn_strikethrough.setOnClickListener(this);
        btn_bullet.setOnClickListener(this);

        broadcastReceiverSelectionChange = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                //Toast.makeText(getApplicationContext(), "Coooool", Toast.LENGTH_LONG).show();
                Log.d("Cool", "Cool");
                checkSpans();
            }
        };

        registerReceiver(broadcastReceiverSelectionChange, new IntentFilter(SELECTION_CHANGE_BROADCAST));

    }

    private void checkSpans() {

        //testing
        int startSelection=editTextNote.getSelectionStart();
        int endSelection=editTextNote.getSelectionEnd();

        Spannable sb = editTextNote.getText();
        //StyleSpan[] ss = sb.getSpans(startSelection, endSelection, StyleSpan.class);
        StyleSpan[] ss = sb.getSpans(startSelection, endSelection, StyleSpan.class);

        Log.d("Style Length", String.valueOf(ss.length));

        boolean isBold=false;
        boolean isItalic=false;

        if(ss.length !=0)
        {

            for (int i = 0; i < ss.length; i++) {
                Log.d("SS[I] Style", String.valueOf(ss[i].getStyle()));

                if (ss[i].getStyle() == android.graphics.Typeface.BOLD){
                    //str.removeSpan(ss[i]);
                    btn_bold.setImageResource(R.drawable.ic_format_bold_white_24dp);
                    bold_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
                    isBold=true;
                }
                else
                if (ss[i].getStyle() == Typeface.ITALIC){
                    //str.removeSpan(ss[i]);
                    btn_italic.setImageResource(R.drawable.ic_format_italic_white_24dp);
                    italic_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
                    isItalic=true;
                }
            }

            if(isBold==false)
            {
                btn_bold.setImageResource(R.drawable.ic_format_bold_black_24dp);
                bold_layout.setBackgroundResource(R.drawable.border_action_buttons);
            }
            if(isItalic==false)
            {
                btn_italic.setImageResource(R.drawable.ic_format_italic_black_24dp);
                italic_layout.setBackgroundResource(R.drawable.border_action_buttons);
            }
        }
        else
        {
            btn_bold.setImageResource(R.drawable.ic_format_bold_black_24dp);
            bold_layout.setBackgroundResource(R.drawable.border_action_buttons);

            btn_italic.setImageResource(R.drawable.ic_format_italic_black_24dp);
            italic_layout.setBackgroundResource(R.drawable.border_action_buttons);
        }
        //testing

        //underline
        UnderlineSpan[] us = sb.getSpans(startSelection, endSelection, UnderlineSpan.class);

        Log.d("Underline Length", String.valueOf(us.length));

        if(us.length !=0)
        {
            for (int i = 0; i < us.length; i++) {
                //testing
                btn_underline.setImageResource(R.drawable.ic_format_underlined_white_24dp);
                underline_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }
        }
        else
        {
            //testing
            btn_underline.setImageResource(R.drawable.ic_format_underlined_black_24dp);
            underline_layout.setBackgroundResource(R.drawable.border_action_buttons);
        }

        //superscript

        SuperscriptSpan[] sup = sb.getSpans(startSelection, endSelection, SuperscriptSpan.class);

        Log.d("Superscript Length", String.valueOf(sup.length));

        if(sup.length !=0)
        {
            for (int i = 0; i < sup.length; i++) {
                //testing
                btn_superscript.setImageResource(R.drawable.ic_format_superscript_white);
                superscript_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }
        }
        else
        {
            //testing
            btn_superscript.setImageResource(R.drawable.ic_format_superscript_black);
            superscript_layout.setBackgroundResource(R.drawable.border_action_buttons);
        }

        //subscript

        SubscriptSpan[] sub = sb.getSpans(startSelection, endSelection, SubscriptSpan.class);

        Log.d("Subscript Length", String.valueOf(sub.length));

        if(sub.length !=0)
        {
            for (int i = 0; i < sub.length; i++) {
                //testing
                btn_subscript.setImageResource(R.drawable.ic_format_subscript_white);
                subscript_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }
        }
        else
        {
            //testing
            btn_subscript.setImageResource(R.drawable.ic_format_subscript_black);
            subscript_layout.setBackgroundResource(R.drawable.border_action_buttons);
        }

        //strikethrough

        StrikethroughSpan[] strike = sb.getSpans(startSelection, endSelection, StrikethroughSpan.class);

        Log.d("Strike Length", String.valueOf(strike.length));

        if(strike.length !=0)
        {
            for (int i = 0; i < strike.length; i++) {
                //testing
                btn_strikethrough.setImageResource(R.drawable.ic_format_strikethrough_white_24dp);
                strikethrough_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }
        }
        else
        {
            //testing
            btn_strikethrough.setImageResource(R.drawable.ic_format_strikethrough_black_24dp);
            strikethrough_layout.setBackgroundResource(R.drawable.border_action_buttons);
        }

        //bullet
        BulletSpan[] bs = sb.getSpans(startSelection, endSelection, BulletSpan.class);

        Log.d("Bullet Length", String.valueOf(bs.length));

        if(bs.length !=0)
        {
            for (int i = 0; i < bs.length; i++) {
                //testing
                btn_bullet.setImageResource(R.drawable.ic_format_list_bulleted_white_24dp);
                bullet_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }
        }
        else
        {
            //testing
            btn_bullet.setImageResource(R.drawable.ic_format_list_bulleted_black_24dp);
            bullet_layout.setBackgroundResource(R.drawable.border_action_buttons);
        }

    }

    public class CallbackCustom implements ActionMode.Callback {

        private EditText mEditText;

        public CallbackCustom(EditText text) {
            this.mEditText = text;

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            /*int start = mEditText.getSelectionStart();
            int end = mEditText.getSelectionEnd();
            Spannable wordtoSpan = (Spannable) mEditText.getText();

            switch (item.getItemId()) {
                case R.id.ContextualActionModeTestOneActivity_select_all:
                    Toast.makeText(getBaseContext(), "S", Toast.LENGTH_LONG).show();
                    mode.finish();    // Automatically exists the action mode, when the user selects this action
                    return true;
                case R.id.ContextualActionModeTestOneActivity_copy:
                    Toast.makeText(getBaseContext(), "C", Toast.LENGTH_LONG).show();
                    mode.finish();
                    return true;
                case R.id.ContextualActionModeTestOneActivity_paste:
                    Toast.makeText(getBaseContext(), "P", Toast.LENGTH_LONG).show();
                    mode.finish();
                    return true;
                default:
                    mode.finish();
                    return false;
            }*/
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            /*Toast.makeText(getApplicationContext(),"OnCreateAction", Toast.LENGTH_LONG).show();
            mActionMode= startActionMode(mActionModeCallback);
            mode.setTitle("Formatting");
            mode.getMenuInflater().inflate(R.menu.menu_selected_text, menu);*/
            if(mActionMode!=null)
            {
                return false;
            }
            mActionMode= startActionMode(mActionModeCallback);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
/*            menu.removeItem(android.R.id.selectAll);
            // Remove the "cut" option
            menu.removeItem(android.R.id.cut);
            // Remove the "copy all" option
            menu.removeItem(android.R.id.copy);*/
            //Toast.makeText(getApplicationContext(),"Preparing", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private ActionMode.Callback mActionModeCallback= new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Formatting");
            mode.getMenuInflater().inflate(R.menu.menu_selected_text, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.ContextualActionModeTestOneActivity_select_all:
                    Toast.makeText(getBaseContext(), "Select All", Toast.LENGTH_LONG).show();

                    editTextNote.selectAll();

                    //mode.finish();    // Automatically exists the action mode, when the user selects this action
                    return true;

                    case R.id.ContextualActionModeTestOneActivity_copy:
                    Toast.makeText(getBaseContext(), "Copy", Toast.LENGTH_LONG).show();

                    int startSelection=editTextNote.getSelectionStart();
                    int endSelection=editTextNote.getSelectionEnd();
                    String selectedText = editTextNote.getText().toString().substring(startSelection, endSelection);

                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", selectedText);
                    clipboard.setPrimaryClip(clip);

                    //mode.finish();
                    return true;

                    case R.id.ContextualActionModeTestOneActivity_paste:

                    ClipboardManager clipboard2 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    String pasteData = "";

                    // If it does contain data, decide if you can handle the data.
                    if (!(clipboard2.hasPrimaryClip())) {

                    } else if (!(clipboard2.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))) {

                        // since the clipboard has data but it is not plain text
                        Toast.makeText(getApplicationContext(), "Has Html", Toast.LENGTH_LONG).show();
                    } else {

                        //since the clipboard contains plain text.
                        ClipData.Item item2 = clipboard2.getPrimaryClip().getItemAt(0);

                        Toast.makeText(getApplicationContext(), "Has Plain", Toast.LENGTH_LONG).show();
                        // Gets the clipboard as text.
                        pasteData = item2.getText().toString();
                    }

                    Toast.makeText(getBaseContext(), "Paste Data: "+pasteData, Toast.LENGTH_LONG).show();

                    int start = Math.max(editTextNote.getSelectionStart(), 0);
                    int end = Math.max(editTextNote.getSelectionEnd(), 0);
                    editTextNote.getText().replace(Math.min(start, end), Math.max(start, end),
                            pasteData, 0, pasteData.length());

                    Log.d("Paste Date", pasteData);

                    //mode.finish();
                    return true;
                default:
                    mode.finish();
                    return false;
            }
            //return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode=null;
        }
    };

/*    class ActionBarCallBack implements android.view.ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            mode.setTitle("Do it");
            getMenuInflater().inflate(R.menu.menu_selected_text, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.ContextualActionModeTestOneActivity_add:
                    Toast.makeText(getBaseContext(), "add this text somewhere ", Toast.LENGTH_LONG).show();
                    mode.finish();    // Automatically exists the action mode, when the user selects this action
                    break;
                case R.id.ContextualActionModeTestOneActivity_search:
                    Toast.makeText(getBaseContext(), "search this text ", Toast.LENGTH_LONG).show();
                    break;
                case R.id.ContextualActionModeTestOneActivity_sort:
                    Toast.makeText(getBaseContext(), "sort", Toast.LENGTH_LONG).show();
                    break;
                case R.id.ContextualActionModeTestOneActivity_help:
                    Toast.makeText(getBaseContext(), "help with this", Toast.LENGTH_LONG).show();
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            mode.finish();
            Toast.makeText(AddActivity.this, "Destroyed", Toast.LENGTH_LONG).show();
        }
    }*/


    @Override
    public void onClick(View view) {
        if(view==buttonSave)
        {
            saveNameToServer();
        }

        if(view==btn_bold)
        {

            int startSelection=editTextNote.getSelectionStart();
            int endSelection=editTextNote.getSelectionEnd();

            Spannable sb = editTextNote.getText();
            //StyleSpan[] ss = sb.getSpans(startSelection, endSelection, StyleSpan.class);
            StyleSpan[] ss = sb.getSpans(startSelection, endSelection, StyleSpan.class);

            Log.d("Style Length", String.valueOf(ss.length));

            boolean isBold=false;

            if(ss.length !=0)
            {

                for (int i = 0; i < ss.length; i++) {
                    Log.d("SS[I] Style", String.valueOf(ss[i].getStyle()));

                    if (ss[i].getStyle() == android.graphics.Typeface.BOLD){
                        //str.removeSpan(ss[i]);
                        sb.removeSpan(ss[i]);
                        Log.d("SS[I]", ss[i].toString());
                        isBold=true;
                        btn_bold.setImageResource(R.drawable.ic_format_bold_black_24dp);
                        bold_layout.setBackgroundResource(R.drawable.border_action_buttons);
                        break;
                    }
                }

                if(isBold==false)
                {
                    sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                    //testing
                    btn_bold.setImageResource(R.drawable.ic_format_bold_white_24dp);
                    bold_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);

                }
            }
            else
            {
                sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                //testing
                btn_bold.setImageResource(R.drawable.ic_format_bold_white_24dp);
                bold_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }
            //sb.subSequence(startSelection, endSelection);


           /* if(!isBold)
            {
                Spannable stringBuilder = editTextNote.getText();
                stringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);



                Log.d("StringBuilder", stringBuilder.toString());
                Log.d("ToHtml", Html.toHtml(stringBuilder));

                Toast.makeText(AddActivity.this, "Bolded", Toast.LENGTH_LONG).show();
            }
            else
            {

                //int startSelection=editTextNote.getSelectionStart();
                //int endSelection=editTextNote.getSelectionEnd();

                Spannable str = editTextNote.getText();
                StyleSpan[] ss = str.getSpans(startSelection, endSelection, StyleSpan.class);

                for (int i = 0; i < ss.length; i++) {
                    if (ss[i].getStyle() == android.graphics.Typeface.BOLD){
                        //str.removeSpan(ss[i]);
                        str.removeSpan(ss[i]);
                        Log.d("SS[I]", ss[i].toString());
                    }
                }

                Toast.makeText(AddActivity.this, "Bold Removed", Toast.LENGTH_LONG).show();

            }*/


                //remove bold.


            //stringBuilder2.setSpan(new StyleSpan(Typeface.BOLD), startSelection, endSelection, 0);
            //editTextNote.setText(stringBuilder2);

            //Toast.makeText(AddActivity.this,)
;
            //CharacterStyle styleBold = new StyleSpan(Typeface.BOLD);
            //CharacterStyle styleNormal = new StyleSpan(Typeface.NORMAL);

            //insertConstantStrAtEnd("</br>");
            //insertConstantStrAtStart("<b>", "</b>");

            //SpannableStringBuilder sb = new SpannableStringBuilder(editTextNote.getText().toString());

               //String selectedText = editTextNote.getText().toString().substring(startSelection, endSelection);
            //sb.setSpan(styleBold, startSelection, endSelection, 0);
            //editTextNote.setText(sb);
        }

        if(view==btn_italic)
        {
            int startSelection=editTextNote.getSelectionStart();
            int endSelection=editTextNote.getSelectionEnd();

            Spannable sb = editTextNote.getText();
            //StyleSpan[] ss = sb.getSpans(startSelection, endSelection, StyleSpan.class);
            StyleSpan[] ss = sb.getSpans(startSelection, endSelection, StyleSpan.class);

            Log.d("Style Length", String.valueOf(ss.length));

            boolean isItalic=false;

            if(ss.length !=0)
            {

                for (int i = 0; i < ss.length; i++) {
                    Log.d("SS[I] Style", String.valueOf(ss[i].getStyle()));

                    if (ss[i].getStyle() == Typeface.ITALIC){
                        //str.removeSpan(ss[i]);
                        sb.removeSpan(ss[i]);
                        Log.d("SS[I]", ss[i].toString());
                        isItalic=true;

                        //testing
                        btn_italic.setImageResource(R.drawable.ic_format_italic_black_24dp);
                        italic_layout.setBackgroundResource(R.drawable.border_action_buttons);
                        break;
                    }
                }

                if(isItalic==false)
                {
                    sb.setSpan(new StyleSpan(Typeface.ITALIC), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                    //testing
                    btn_italic.setImageResource(R.drawable.ic_format_italic_white_24dp);
                    italic_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
                }
            }
            else
            {
                sb.setSpan(new StyleSpan(Typeface.ITALIC), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);


                //testing
                btn_italic.setImageResource(R.drawable.ic_format_italic_white_24dp);
                italic_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }

        }

        if(view==btn_underline)
        {
            int startSelection=editTextNote.getSelectionStart();
            int endSelection=editTextNote.getSelectionEnd();

            Spannable sb = editTextNote.getText();
            UnderlineSpan[] ss = sb.getSpans(startSelection, endSelection, UnderlineSpan.class);

            Log.d("Underline Length", String.valueOf(ss.length));

            if(ss.length !=0)
            {
                for (int i = 0; i < ss.length; i++) {
                    sb.removeSpan(ss[i]);
                    //testing
                    btn_underline.setImageResource(R.drawable.ic_format_underlined_black_24dp);
                    underline_layout.setBackgroundResource(R.drawable.border_action_buttons);
                }
            }
            else
            {
                sb.setSpan(new UnderlineSpan(), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                //testing
                btn_underline.setImageResource(R.drawable.ic_format_underlined_white_24dp);
                underline_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }

        }

        if(view==btn_superscript)
        {
            int startSelection=editTextNote.getSelectionStart();
            int endSelection=editTextNote.getSelectionEnd();

            Spannable sb = editTextNote.getText();
            SuperscriptSpan[] ss = sb.getSpans(startSelection, endSelection, SuperscriptSpan.class);

            Log.d("Superscript Length", String.valueOf(ss.length));

            if(ss.length !=0)
            {
                for (int i = 0; i < ss.length; i++) {
                    sb.removeSpan(ss[i]);
                    //testing
                    btn_superscript.setImageResource(R.drawable.ic_format_superscript_black);
                    superscript_layout.setBackgroundResource(R.drawable.border_action_buttons);
                }
            }
            else
            {
                sb.setSpan(new SuperscriptSpan(), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                //testing
                btn_superscript.setImageResource(R.drawable.ic_format_superscript_white);
                superscript_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }
        }

        if(view==btn_subscript)
        {
            int startSelection=editTextNote.getSelectionStart();
            int endSelection=editTextNote.getSelectionEnd();

            Spannable sb = editTextNote.getText();
            SubscriptSpan[] ss = sb.getSpans(startSelection, endSelection, SubscriptSpan.class);

            Log.d("Subscript Length", String.valueOf(ss.length));

            if(ss.length !=0)
            {
                for (int i = 0; i < ss.length; i++) {
                    sb.removeSpan(ss[i]);
                    //testing
                    btn_subscript.setImageResource(R.drawable.ic_format_subscript_black);
                    subscript_layout.setBackgroundResource(R.drawable.border_action_buttons);
                }
            }
            else
            {
                sb.setSpan(new SubscriptSpan(), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                //testing
                btn_subscript.setImageResource(R.drawable.ic_format_subscript_white);
                subscript_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }
        }

        if(view==btn_strikethrough)
        {
            int startSelection=editTextNote.getSelectionStart();
            int endSelection=editTextNote.getSelectionEnd();

            Spannable sb = editTextNote.getText();
            StrikethroughSpan[] ss = sb.getSpans(startSelection, endSelection, StrikethroughSpan.class);

            Log.d("Superscript Length", String.valueOf(ss.length));

            if(ss.length !=0)
            {
                for (int i = 0; i < ss.length; i++) {
                    sb.removeSpan(ss[i]);
                    //testing
                    btn_strikethrough.setImageResource(R.drawable.ic_format_strikethrough_black_24dp);
                    strikethrough_layout.setBackgroundResource(R.drawable.border_action_buttons);
                }
            }
            else
            {
                sb.setSpan(new StrikethroughSpan(), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                //testing
                btn_strikethrough.setImageResource(R.drawable.ic_format_strikethrough_white_24dp);
                strikethrough_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }
        }

        if(view==btn_bullet)
        {
            int startSelection=editTextNote.getSelectionStart();
            int endSelection=editTextNote.getSelectionEnd();

            Spannable sb = editTextNote.getText();
            BulletSpan[] ss = sb.getSpans(startSelection, endSelection, BulletSpan.class);

            Log.d("Underline Length", String.valueOf(ss.length));

            if(ss.length !=0)
            {
                for (int i = 0; i < ss.length; i++) {
                    sb.removeSpan(ss[i]);
                    //testing
                    btn_bullet.setImageResource(R.drawable.ic_format_list_bulleted_black_24dp);
                    bullet_layout.setBackgroundResource(R.drawable.border_action_buttons);
                }
            }
            else
            {
                //gap width from bullet to text, color, radius
                //40, color, 20
                sb.setSpan(new BulletSpan(), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                //testing
                btn_bullet.setImageResource(R.drawable.ic_format_list_bulleted_white_24dp);
                bullet_layout.setBackgroundResource(R.drawable.border_action_buttons_selected);
            }

        }

        if(view==btn_fontfamily)
        {
            PopupMenu popup = new PopupMenu(AddActivity.this, btn_fontfamily);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.menu_fontfamily, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    //Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                    if(item.getItemId()==R.id.font1)
                    {
                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();

                        Typeface font = Typeface.createFromAsset(getAssets(), "eiffela.ttf");

                        sb.setSpan(new CustomTypefaceSpan(font), startSelection, endSelection, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    else
                    if(item.getItemId()==R.id.font2)
                    {

                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();

                        Typeface font = Typeface.createFromAsset(getAssets(), "wrong_hunt.ttf");

                        sb.setSpan(new CustomTypefaceSpan(font), startSelection, endSelection, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                    }
                    else
                    if(item.getItemId()==R.id.remove_font)
                    {

                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();

                        Typeface font = Typeface.createFromAsset(getAssets(), "normal_roboto.ttf");

                        sb.setSpan(new CustomTypefaceSpan(font), startSelection, endSelection, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        /*int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();
                        StyleSpan[] ss = sb.getSpans(startSelection, endSelection, StyleSpan.class);
                        Log.d("Font family Length", String.valueOf(ss.length));

                        if(ss.length !=0)
                        {

                            Toast.makeText(AddActivity.this, "Font family default", Toast.LENGTH_LONG).show();
                        }*/
                    }

                    return true;
                }
            });

            popup.show();//showing popup menu
        }

        if(view==btn_highlight)
        {

            listPopupWindow = new ListPopupWindow(getApplicationContext());
            //listPopupWindow.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.list_item_popupwindow,brandLi));
            ListPopupAdapter listPopupAdapter = new ListPopupAdapter(highlighterList);
            listPopupWindow.setAdapter(listPopupAdapter);
            listPopupWindow.setAnchorView(btn_highlight);
            listPopupWindow.setWidth(300);
            //listPopupWindow.setHeight(250);
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                    if (highlighterList.size() > position) {

                        if(position==0) {
                            int startSelection = editTextNote.getSelectionStart();
                            int endSelection = editTextNote.getSelectionEnd();

                            Spannable sb = editTextNote.getText();
                            BackgroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                            Log.d("Highlight Length", String.valueOf(ss.length));

                            if (ss.length != 0) {
                                for (int i = 0; i < ss.length; i++) {
                                    sb.removeSpan(ss[i]);
                                }
                                Toast.makeText(AddActivity.this, "Underline removed", Toast.LENGTH_LONG).show();
                            } else {
                                sb.setSpan(new BackgroundColorSpan(current_textHighlighter), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }
                        }
                        else
                        if(position==1)
                        {

                            final int startSelection=editTextNote.getSelectionStart();
                            final int endSelection=editTextNote.getSelectionEnd();

                            final Spannable sb = editTextNote.getText();
                            BackgroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                            Log.d("Highlight Length", String.valueOf(ss.length));

                            //sb.setSpan(new ForegroundColorSpan(Colors.BLUE), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                            //colorPicker();

                            final AmbilWarnaDialog dialog = new AmbilWarnaDialog(AddActivity.this, current_textHighlighter, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                @Override
                                public void onOk(AmbilWarnaDialog dialog, int color) {
                                    current_textHighlighter = color;
                                    sb.setSpan(new BackgroundColorSpan(color), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    Log.d("Selected Colors: ", String.valueOf(color));

                                    //TextView tv= view.findViewById(R.id.txt_title_color);
                                    //ImageView iv= view.findViewById(R.id.img_icon_color);

                                    //iv.setBackgroundColor(color);
                                    //highlighterList.get(position).setIconColor(color);
                                    highlighterList.get(position-1).setIconColor(color);
                                    current_textHighlighter=color;
                                    //Colors selection= colorsList.get(position).setIconColor(color);
                                    //txt_brand.setText(selection.getTitle());
                                }

                                @Override
                                public void onCancel(AmbilWarnaDialog dialog) {
                                    Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.show();

                        }
                        else
                        if(position==2)
                        {
                            int startSelection=editTextNote.getSelectionStart();
                            int endSelection=editTextNote.getSelectionEnd();

                            Spannable sb = editTextNote.getText();
                            BackgroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                            Log.d("Highlight Length", String.valueOf(ss.length));

                            if(ss.length !=0)
                            {
                                for (int i = 0; i < ss.length; i++) {
                                    sb.removeSpan(ss[i]);
                                }
                                Toast.makeText(AddActivity.this, "Highlight removed", Toast.LENGTH_LONG).show();
                            }
                        }

                        listPopupWindow.dismiss();
                    }
                }
            });

            listPopupWindow.show();

            /*PopupMenu popup = new PopupMenu(AddActivity.this, btn_highlight);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.menu_highlight_color, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    //Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                    if(item.getItemId()==R.id.red)
                    {
                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();
                        BackgroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        Log.d("Highlight Length", String.valueOf(ss.length));

                        *//*if(ss.length !=0)
                        {
                            for (int i = 0; i < ss.length; i++) {
                                sb.removeSpan(ss[i]);
                            }
                            Toast.makeText(AddActivity.this, "Underline removed", Toast.LENGTH_LONG).show();
                        }
                        else
                        {*//*
                            sb.setSpan(new BackgroundColorSpan(Color.RED), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        //}
                    }
                    else
                    if(item.getItemId()==R.id.blue)
                    {
                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();
                        BackgroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        Log.d("Highlight Length", String.valueOf(ss.length));

                        sb.setSpan(new BackgroundColorSpan(Color.BLUE), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                    }
                    else
                    if(item.getItemId()==R.id.remove_color)
                    {
                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();
                        BackgroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        Log.d("Highlight Length", String.valueOf(ss.length));

                        if(ss.length !=0)
                        {
                            for (int i = 0; i < ss.length; i++) {
                                sb.removeSpan(ss[i]);
                            }
                            Toast.makeText(AddActivity.this, "Highlight removed", Toast.LENGTH_LONG).show();
                        }
                    }

                    return true;
                }
            });

            popup.show();//showing popup menu*/
        }

        if(view==btn_size)
        {
            PopupMenu popup = new PopupMenu(AddActivity.this, btn_size);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.menu_size, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    //Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                    if(item.getItemId()==R.id.s55)
                    {
                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();
                        //we can use RelativeSize span for increasing font by %
                        //we can use AbsoluteSize span for increasing font by dp.
                        AbsoluteSizeSpan[] ss = sb.getSpans(startSelection, endSelection, AbsoluteSizeSpan.class);
                        Log.d("Text Colors Length", String.valueOf(ss.length));

                        /*if(ss.length !=0)
                        {
                            for (int i = 0; i < ss.length; i++) {
                                sb.removeSpan(ss[i]);
                            }
                            Toast.makeText(AddActivity.this, "Underline removed", Toast.LENGTH_LONG).show();
                        }
                        else
                        {*/
                        sb.setSpan(new AbsoluteSizeSpan(55), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        //}
                    }
                    else
                    if(item.getItemId()==R.id.s25)
                    {
                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();
                        AbsoluteSizeSpan[] ss = sb.getSpans(startSelection, endSelection, AbsoluteSizeSpan.class);
                        Log.d("Highlight Length", String.valueOf(ss.length));

                        sb.setSpan(new AbsoluteSizeSpan(25), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                    }
                    else
                    if(item.getItemId()==R.id.remove_size)
                    {
                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();
                        AbsoluteSizeSpan[] ss = sb.getSpans(startSelection, endSelection, AbsoluteSizeSpan.class);
                        Log.d("Highlight Length", String.valueOf(ss.length));

                        if(ss.length !=0)
                        {
                            for (int i = 0; i < ss.length; i++) {
                                sb.removeSpan(ss[i]);
                            }
                            Toast.makeText(AddActivity.this, "Text size default", Toast.LENGTH_LONG).show();
                        }
                    }

                    return true;
                }
            });

            popup.show();//showing popup menu
        }

        if(view==btn_txt_color)
        {
                listPopupWindow = new ListPopupWindow(getApplicationContext());
                //listPopupWindow.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.list_item_popupwindow,brandLi));
                ListPopupAdapter listPopupAdapter = new ListPopupAdapter(colorsList);
                listPopupWindow.setAdapter(listPopupAdapter);
                listPopupWindow.setAnchorView(btn_txt_color);
                //listPopupWindow.setWidth(measureContentWidth(listPopupAdapter));
                listPopupWindow.setWidth(300);
                //listPopupWindow.setHeight(300);
                listPopupWindow.setModal(true);
                listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                        if (colorsList.size() > position) {

                            if(position==0)
                            {
                                int startSelection=editTextNote.getSelectionStart();
                                int endSelection=editTextNote.getSelectionEnd();

                                Spannable sb = editTextNote.getText();
                                ForegroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, ForegroundColorSpan.class);
                                Log.d("Text Colors Length", String.valueOf(ss.length));

                                if(ss.length !=0)
                                {
                                    for (int i = 0; i < ss.length; i++) {
                                        sb.removeSpan(ss[i]);
                                    }
                                    Toast.makeText(AddActivity.this, "Underline removed", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    sb.setSpan(new ForegroundColorSpan(current_textColor), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                }
                                sb.setSpan(new ForegroundColorSpan(current_textColor), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }
                            else
                            if(position==1)
                            {
                                final int startSelection=editTextNote.getSelectionStart();
                                final int endSelection=editTextNote.getSelectionEnd();

                                final Spannable sb = editTextNote.getText();
                                ForegroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, ForegroundColorSpan.class);
                                Log.d("Highlight Length", String.valueOf(ss.length));

                                //sb.setSpan(new ForegroundColorSpan(Colors.BLUE), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                                //colorPicker();

                                final AmbilWarnaDialog dialog = new AmbilWarnaDialog(AddActivity.this, current_textColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                    @Override
                                    public void onOk(AmbilWarnaDialog dialog, int color) {
                                        current_textColor = color;
                                        sb.setSpan(new ForegroundColorSpan(color), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                        Log.d("Selected Colors: ", String.valueOf(color));

                                        //TextView tv= view.findViewById(R.id.txt_title_color);
                                        //ImageView iv= view.findViewById(R.id.img_icon_color);

                                        //iv.setBackgroundColor(color);
                                        //colorsList.get(position).setIconColor(color);
                                        colorsList.get(position-1).setIconColor(color);
                                        current_textColor=color;
                                        //Colors selection= colorsList.get(position).setIconColor(color);
                                        //txt_brand.setText(selection.getTitle());
                                    }

                                    @Override
                                    public void onCancel(AmbilWarnaDialog dialog) {
                                        Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.show();
                            }
                            else
                            if(position==2)
                            {
                                int startSelection=editTextNote.getSelectionStart();
                                int endSelection=editTextNote.getSelectionEnd();

                                Spannable sb = editTextNote.getText();
                                ForegroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, ForegroundColorSpan.class);
                                Log.d("Color Length", String.valueOf(ss.length));

                                if(ss.length !=0)
                                {
                                    for (int i = 0; i < ss.length; i++) {
                                        sb.removeSpan(ss[i]);
                                    }
                                    Toast.makeText(AddActivity.this, "Text Colors removed", Toast.LENGTH_LONG).show();
                                }
                            }

                            listPopupWindow.dismiss();
                        }
                    }
                });

                listPopupWindow.show();
                //Toast.makeText(getActivity(),"Success",Toast.LENGTH_LONG).show();

/*            PopupMenu popup = new PopupMenu(AddActivity.this, btn_txt_color);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.menu_text_color, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    //Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                    if(item.getItemId()==R.id.default_color)
                    {
                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();
                        ForegroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, ForegroundColorSpan.class);
                        Log.d("Text Colors Length", String.valueOf(ss.length));

                        if(ss.length !=0)
                        {
                            for (int i = 0; i < ss.length; i++) {
                                sb.removeSpan(ss[i]);
                            }
                            Toast.makeText(AddActivity.this, "Underline removed", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                        sb.setSpan(new ForegroundColorSpan(Color.BLACK), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                    }
                    else
                    if(item.getItemId()==R.id.custom_color)
                    {
                        final int startSelection=editTextNote.getSelectionStart();
                        final int endSelection=editTextNote.getSelectionEnd();

                        final Spannable sb = editTextNote.getText();
                        ForegroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, ForegroundColorSpan.class);
                        Log.d("Highlight Length", String.valueOf(ss.length));

                        //sb.setSpan(new ForegroundColorSpan(Colors.BLUE), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                        //colorPicker();

                        final AmbilWarnaDialog dialog = new AmbilWarnaDialog(AddActivity.this, current_textColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                current_textColor = color;
                                sb.setSpan(new ForegroundColorSpan(color), startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                Log.d("Selected Colors: ", String.valueOf(color));
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.show();

                    }
                    else
                    if(item.getItemId()==R.id.remove_color)
                    {
                        int startSelection=editTextNote.getSelectionStart();
                        int endSelection=editTextNote.getSelectionEnd();

                        Spannable sb = editTextNote.getText();
                        ForegroundColorSpan[] ss = sb.getSpans(startSelection, endSelection, ForegroundColorSpan.class);
                        Log.d("Color Length", String.valueOf(ss.length));

                        if(ss.length !=0)
                        {
                            for (int i = 0; i < ss.length; i++) {
                                sb.removeSpan(ss[i]);
                            }
                            Toast.makeText(AddActivity.this, "Text Colors removed", Toast.LENGTH_LONG).show();
                        }
                    }

                    return true;
                }
            });*/

            /*try {
                Method method = popup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                method.setAccessible(true);
                method.invoke(popup.getMenu(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            //popup.show();//showing popup menu
        }
    }


    /*
     * this method is saving the name to ther server
     * */
    private void saveNameToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Name...");
        progressDialog.show();

        UUID uuid = UUID.randomUUID();
        //final String unique_id= uuid.toString()+"-"+userid;
        final String unique_id= uuid.toString()+"-"+System.currentTimeMillis()+"-"+userid;
        Log.d("UID: ", String.valueOf(uuid));
        Log.d("Unique ID: ", String.valueOf(unique_id));

        // set the format to sql date time
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date date = new Date();

        //spannable content starts here
        //convert spannable to html tags to save

        Spannable stringBuilder = editTextNote.getText();

        Log.d("StringBuilder", stringBuilder.toString());
        Log.d("ToHtml", Html.toHtml(stringBuilder));

        myHtmlDataTo= Html.toHtml(stringBuilder);

        //spannable content ends here

        final String note = myHtmlDataTo;
        //final String note = editTextNote.getText().toString().trim();
        final int userid = session.getUserId();
        //final String created_at = "2019-03-23 11:00:40";
        final String created_at = String.valueOf(dateFormat.format(date));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PublicValues.URL_NOTEPAD_ADD,
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
                                saveNameToLocalStorage(unique_id, note, userid, created_at, NOTES_SYNCED_WITH_SERVER);
                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                saveNameToLocalStorage(unique_id, note, userid, created_at, NOTES_NOT_SYNCED_WITH_SERVER);
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
                        saveNameToLocalStorage(unique_id, note, userid, created_at, NOTES_NOT_SYNCED_WITH_SERVER);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("unique_id", unique_id);
                params.put("notes", note);
                params.put("userid", String.valueOf(userid));
                params.put("created_at", created_at);
                return params;
            }
        };

        ServiceRequest.getInstance(this).addToRequestQueue(stringRequest);
    }

    //saving the name to local storage
    private void saveNameToLocalStorage(String unique_id, String note, int userid, String created_at, int status) {
        editTextNote.setText("");
        db.addNotes(unique_id, note, userid, created_at, status);
        /*Notes n = new Notes(unique_id, note, userid, created_at, status);
        notesList.add(n);
        refreshList();*/
        //Toast.makeText(AddActivity.this, "Note saved successfully", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //String myText = "make this <b>bold</b> and <b>this</b> too and this too <b>WOW</b>";
    //editTextNote.setText(makeSpannable(myText, "<b>(.+?)</b>", "<b>", "</b>"));
    //String selectedText = et.getText().substring(startSelection, endSelection);

    public SpannableStringBuilder makeSpannable(String text, String regex, String startTag, String endTag) {

        StringBuffer sb = new StringBuffer();
        SpannableStringBuilder spannable = new SpannableStringBuilder();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            sb.setLength(0);
            String group = matcher.group();
            String spanText = group.substring(startTag.length(), group.length() - endTag.length());
            matcher.appendReplacement(sb, spanText);

            spannable.append(sb.toString());
            int start = spannable.length() - spanText.length();

            spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        sb.setLength(0);
        matcher.appendTail(sb);
        spannable.append(sb.toString());
        return spannable;
    }

    public void insertConstantStrAtEnd(String insertStr) {
        String oriContent = editTextNote.getText().toString();
        int index = editTextNote.getSelectionEnd() >= 0 ? editTextNote.getSelectionEnd() : 0;
        StringBuilder sBuilder = new StringBuilder(oriContent);
        sBuilder.insert(index, insertStr);
        editTextNote.setText(sBuilder.toString());
        editTextNote.setSelection(index + insertStr.length());
    }

    public void insertConstantStrAtStart(String insertAtStart, String insertAtEnd) {

            String oriContent = editTextNote.getText().toString();
            int startIndex = editTextNote.getSelectionStart() >= 0 ? editTextNote.getSelectionStart() : 0;
            int endIndex = editTextNote.getSelectionEnd() >= 0 ? editTextNote.getSelectionEnd() : 0;

            StringBuilder sBuilder = new StringBuilder(oriContent);
            sBuilder.insert(startIndex, insertAtStart);
            sBuilder.insert(endIndex+3, insertAtEnd);
            editTextNote.setText(sBuilder.toString());
            //editTextNote.setText(makeSpannable(sBuilder.toString(), "<b>(.+?)</b>", "<b>", "</b>"));
            //editTextNote.setSelection(startIndex, endIndex+7);
    }

    public void colorPicker()
    {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, current_textColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                //current_textColor= Color.BLACK;
                //current_textColor = color;
                Log.d("Selected Colors: ", String.valueOf(color));
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    //You could measure the width of the adapter content
    //so it will not take the width of anchor view.
    //use it with listpopup window
    //listPopupWindow.setWidth(measureContentWidth(listPopupAdapter));
    private int measureContentWidth(ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(getApplicationContext());
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }

}