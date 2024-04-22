package com.example.quickcash.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.util.MenuAdapter;

/**
 * Activity to display information about the application, including version, description, and authors.
 */
public class AboutAppActivity extends AppCompatActivity {
    final static String authors = "O. Al Sadi\n"
            + "A. Al-Naamani\n"
            + "J. Bouchard\n"
            + "A. Fernando\n"
            + "I. Keigan\n"
            + "N. Waduge\n"
            + "M. Young";
    final static String appDescription = "Quick Cash was developed as a term project for\n"
            + "CSCI3130: Software Engineering (Winter 2024).";
    final static String appVersion = "Version: 2.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        setUpUI();
    }

    protected void setUpUI() {
        setUpMenu();
        setUpVersionText();
        setUpAboutText();
        setUpAuthorText();
    }

    protected void setUpMenu (){
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    protected void setUpVersionText() {
        TextView versionText = findViewById(R.id.versionText);
        versionText.setText(appVersion);
    }

    protected void setUpAboutText() {
        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText(appDescription);
    }

    protected void setUpAuthorText() {
        TextView authorText = findViewById(R.id.authorText);
        authorText.setText(authors);
    }
}
