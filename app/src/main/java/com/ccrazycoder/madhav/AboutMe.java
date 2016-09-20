package com.ccrazycoder.madhav;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AboutMe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Press Send Button to Open Mail App", Snackbar.LENGTH_LONG)
                        .setAction("Send", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", "vdkhakhkhar@gmail.com", null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Madhav App");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "App Version : " + BuildConfig.VERSION_NAME);
                                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                            }
                        }).show();
            }
        });
    }

}
