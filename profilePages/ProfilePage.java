package com.kenoDigital.rookiemate.profilePages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kenoDigital.rookiemate.DashboardPages.DashboardPage;
import com.kenoDigital.rookiemate.ExplorePage;
import com.kenoDigital.rookiemate.R;
import com.kenoDigital.rookiemate.SignInPages.SignInPage;

import javax.annotation.Nullable;

public class ProfilePage extends AppCompatActivity {

    Button ExploreButton,AboutMeButton,DashboardButton,logoutButton;
    TextView usernameTextView;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        ExploreButton = (Button) findViewById(R.id.exploreButton);
        AboutMeButton = (Button) findViewById(R.id.aboutMeButton);
        DashboardButton = (Button) findViewById(R.id.dashboeardButton);
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        logoutButton = (Button) findViewById(R.id.BackButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        setUsername();

        ExploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilePage.this, ExplorePage.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                startActivity(new Intent(ProfilePage.this, SignInPage.class));
                finish();
            }
        });

        DashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilePage.this, DashboardPage.class));
            }
        });


    }
    private void setUsername() {
        String uid = fAuth.getCurrentUser().getUid();
        DocumentReference userInfoDocumentReference = fStore.collection("Users").document(uid);
        userInfoDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null)
                    e.printStackTrace();
                else{
                    usernameTextView.setText(documentSnapshot.getString("fullName"));
                }
            }
        });
    }
}