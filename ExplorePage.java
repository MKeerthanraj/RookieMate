package com.kenoDigital.rookiemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.kenoDigital.rookiemate.AnnouncementsPages.ViewAnnouncementsPage;
import com.kenoDigital.rookiemate.MaterialsPages.ViewMaterialsPage;
import com.kenoDigital.rookiemate.OppertunitiesPages.ViewOppertunitiesPage;
import com.kenoDigital.rookiemate.ProjectPages.ViewProjectsPage;
import com.kenoDigital.rookiemate.SignInPages.SignInPage;
import com.kenoDigital.rookiemate.profilePages.ProfilePage;

import javax.annotation.Nullable;

public class ExplorePage extends AppCompatActivity {

    CardView projectButton,announcementsButton,materialsButton,oppertunitiesButton;
    TextView usernameTextView;
    Button logoutButton,profileButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        projectButton = (CardView)  findViewById(R.id.projectCard);
        announcementsButton = (CardView)  findViewById(R.id.announcementsCard);
        materialsButton = (CardView)  findViewById(R.id.materialCard);
        oppertunitiesButton = (CardView)  findViewById(R.id.oppertunitiesCard);
        logoutButton = (Button) findViewById(R.id.BackButton);
        profileButton = (Button) findViewById(R.id.profileButton);
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        setUsername();

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExplorePage.this, ProfilePage.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
                finish();
            }
        });

        projectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExplorePage.this, ViewProjectsPage.class));
            }
        });

        announcementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExplorePage.this, ViewAnnouncementsPage.class));
            }
        });

        materialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExplorePage.this, ViewMaterialsPage.class));
            }
        });

        oppertunitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExplorePage.this, ViewOppertunitiesPage.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                startActivity(new Intent(ExplorePage.this, SignInPage.class));
                finish();
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