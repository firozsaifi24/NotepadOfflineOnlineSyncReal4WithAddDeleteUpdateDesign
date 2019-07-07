package example.firoz.notepadofflineonlinesyncreal2.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import example.firoz.notepadofflineonlinesyncreal2.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView menu_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        menu_btn= findViewById(R.id.menu_btn);

        menu_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==menu_btn)
        {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
