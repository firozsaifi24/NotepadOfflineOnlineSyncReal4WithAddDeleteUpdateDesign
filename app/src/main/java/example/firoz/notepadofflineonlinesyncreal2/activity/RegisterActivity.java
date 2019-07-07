package example.firoz.notepadofflineonlinesyncreal2.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import example.firoz.notepadofflineonlinesyncreal2.R;
import example.firoz.notepadofflineonlinesyncreal2.data.CustomRequest;
import example.firoz.notepadofflineonlinesyncreal2.data.ServiceRequest;
import example.firoz.notepadofflineonlinesyncreal2.interfaces.OnRegisterCallback;
import example.firoz.notepadofflineonlinesyncreal2.utilities.PublicValues;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, OnRegisterCallback {

    ImageView menu_btn;

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private CustomRequest customRequest;

    TextView txt_login_req;
    EditText et_name, et_email, et_password, et_confirm_password;
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

      /*  menu_btn= findViewById(R.id.menu_btn);
        menu_btn.setOnClickListener(this);*/

        et_name= findViewById(R.id.et_name);
        et_email= findViewById(R.id.et_email);
        et_password= findViewById(R.id.et_password);
        et_confirm_password= findViewById(R.id.et_confirm_password);
        btn_register= findViewById(R.id.btn_register);
        txt_login_req= findViewById(R.id.txt_login_page_req);
        btn_register.setOnClickListener(this);
        txt_login_req.setOnClickListener(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        customRequest=new CustomRequest();
    }

    @Override
    public void onClick(View view) {
        if(view==txt_login_req)
        {
            Intent i= new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(i);
        }


        if(view==btn_register) {

            String name = et_name.getText().toString().trim();
            String email = et_email.getText().toString().trim();
            String pass = et_password.getText().toString().trim();
            String confirmpass = et_confirm_password.getText().toString().trim();

            if (!name.isEmpty())
            {

                if(!email.isEmpty())
                {
                    if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                            if(!pass.isEmpty())
                            {
                                if(!confirmpass.isEmpty())
                                {

                                    if(pass.equals(confirmpass))
                                    {

                                        //registerUser(name, email, phone, address, password, view);
                                        pDialog.setMessage("Registering ...");
                                        showDialog();

                                        ServiceRequest.getInstance(getApplicationContext()).addToRequestQueue(customRequest.register(PublicValues.URL_REGISTER,this, name, email, pass));

                                    }
                                    else
                                    {
                                        Toast.makeText(this,"Password do not match!", Toast.LENGTH_LONG).show();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(this,"Pls confirm your password", Toast.LENGTH_LONG).show();
                                }
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
            else
            {
                Toast.makeText(this,"Pls enter your name", Toast.LENGTH_LONG).show();
            }



        }
    }

    @Override
    public void onRegisterSuccess(JSONObject response) {
        //user successfully registered
        hideDialog();
        Toast.makeText(this,"Registeration success, Try Login Now!", Toast.LENGTH_LONG).show();
        Intent i= new Intent(this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onRegisterError(String error) {
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
