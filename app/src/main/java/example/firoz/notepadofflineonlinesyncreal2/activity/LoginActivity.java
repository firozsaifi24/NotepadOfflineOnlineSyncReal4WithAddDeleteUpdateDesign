package example.firoz.notepadofflineonlinesyncreal2.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import example.firoz.notepadofflineonlinesyncreal2.R;
import example.firoz.notepadofflineonlinesyncreal2.data.CustomRequest;
import example.firoz.notepadofflineonlinesyncreal2.data.ServiceRequest;
import example.firoz.notepadofflineonlinesyncreal2.interfaces.OnLoginCallback;
import example.firoz.notepadofflineonlinesyncreal2.utilities.PublicValues;
import example.firoz.notepadofflineonlinesyncreal2.utilities.SessionManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnLoginCallback {


    ImageView menu_btn;

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private CustomRequest customRequest;
    private SessionManager session;

    TextView txt_register_req;

    EditText et_email, et_password;
    Button btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*menu_btn= findViewById(R.id.menu_btn);
        menu_btn.setOnClickListener(this);
*/
        et_email= findViewById(R.id.et_email);
        et_password= findViewById(R.id.et_password);
        btn_login= findViewById(R.id.btn_login);
        txt_register_req= findViewById(R.id.txt_register_page_req);
        btn_login.setOnClickListener(this);
        txt_register_req.setOnClickListener(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        customRequest=new CustomRequest();
        // Session manager
        session = new SessionManager(getApplicationContext());

    }

    @Override
    public void onClick(View view) {

        if(view==txt_register_req)
        {
            Intent i= new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(i);
        }


        if(view==btn_login)
        {
            String email= et_email.getText().toString().trim();
            String pass= et_password.getText().toString().trim();

            if(!email.isEmpty())
            {
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    if(!pass.isEmpty())
                    {
                        pDialog.setMessage("Logging in ...");
                        showDialog();
                        //login user
                        ServiceRequest.getInstance(getApplicationContext()).addToRequestQueue(customRequest.login(PublicValues.URL_LOGIN,this,email,pass));

                    }
                    else
                    {
                        Toast.makeText(this,"Pls enter your password", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(this,"Pls enter a valid email address!", Toast.LENGTH_LONG).show();
                }

            }
            else
            {
                Toast.makeText(this,"Pls enter your email", Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    public void onLoginSuccess(JSONObject credentials) {
        // user successfully logged in
        // Create login session
        hideDialog();
        session.setLogin(true);

        JSONObject user = null;
        try {

            user = credentials.getJSONObject("user");
            String name = user.getString("name");
            int userid = user.getInt("id");
            String email = user.getString("email");

            session.setName(name);
            session.setUserId(userid);
            session.setEmail(email);

            Log.d("user id and role: ", userid+" and "+name+ " and"+email);

            Toast.makeText(this, "Login Successfull", Toast.LENGTH_LONG).show();

            Intent i= new Intent(this,MainActivity.class);
            startActivity(i);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoginError(String error) {
        hideDialog();
        if (getApplicationContext() != null){
            if (error != null && !error.isEmpty()){
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Some error has been occurred!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onNetworkError() {
        hideDialog();
        if(getApplicationContext() !=null)
        {
            Toast.makeText(this,"Nn Internet Connection!",Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
