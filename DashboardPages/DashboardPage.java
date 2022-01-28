package com.kenoDigital.rookiemate.DashboardPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kenoDigital.rookiemate.R;

public class DashboardPage extends AppCompatActivity {

    Button backButton;
    CardView materials_card,opportunities_card,announcements_card,projects_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_page);

        backButton = (Button) findViewById(R.id.BackButton);
        materials_card = (CardView) findViewById(R.id.materials_card);
        opportunities_card = (CardView) findViewById(R.id.opportunities_Card);
        announcements_card = (CardView) findViewById(R.id.announcement_card);
        projects_card = (CardView) findViewById(R.id.projects_card);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        materials_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardPage.this, MaterialsDashboardPage.class));
            }
        });
    }
}