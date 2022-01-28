package com.kenoDigital.rookiemate.ProjectPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kenoDigital.rookiemate.R;

public class ViewProjectsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_projects_page);

        Button backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}