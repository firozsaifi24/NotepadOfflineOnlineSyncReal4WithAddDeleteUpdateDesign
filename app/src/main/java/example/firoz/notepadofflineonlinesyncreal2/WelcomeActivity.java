package example.firoz.notepadofflineonlinesyncreal2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import example.firoz.notepadofflineonlinesyncreal2.activity.LoginActivity;
import example.firoz.notepadofflineonlinesyncreal2.activity.MainActivity;
import example.firoz.notepadofflineonlinesyncreal2.activity.RegisterActivity;
import example.firoz.notepadofflineonlinesyncreal2.utilities.SessionManager;

public class WelcomeActivity extends AppCompatActivity {

    Button btn_login;
    Button btn_register;
    TextView btn_skip;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        session = new SessionManager(getApplicationContext());

        if(session.isLoggedIn())
        {

            String welcomeName  = session.getName();
            Log.d("Name value ",welcomeName);
            welcomeName = welcomeName.substring(0,1).toUpperCase() + welcomeName.substring(1).toLowerCase();

            Intent intent= new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }

        btn_login= findViewById(R.id.btn_login);
        btn_register= findViewById(R.id.btn_register);
        btn_skip= findViewById(R.id.btn_skip);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
