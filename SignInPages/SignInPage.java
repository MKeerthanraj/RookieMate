package com.kenoDigital.rookiemate.SignInPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kenoDigital.rookiemate.ExplorePage;
import com.kenoDigital.rookiemate.R;

public class SignInPage extends AppCompatActivity {

    Button signupSelectButton,loginButton;
    EditText emailEditText,passEditText;
    FirebaseAuth fAuth;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(SignInPage.this,ExplorePage.class));
            finish();
        }

        loginButton = (Button) findViewById(R.id.loginButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passEditText = (EditText) findViewById(R.id.passwordEditText);
        signupSelectButton = findViewById(R.id.signupSelectButton);
        spinner = (ProgressBar) findViewById(R.id.spinner);

        signupSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInPage.this,SignUpPage.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = emailEditText.getText().toString();
                String userPass = passEditText.getText().toString();

                if(userEmail.isEmpty()){
                    emailEditText.setError("Required!");
                    return;
                }else if(!(userEmail.contains("@srmist.edu.in"))){
                    emailEditText.setError("Use only SRM email ID!");
                    return;
                }
                if(userPass.isEmpty()){
                    passEditText.setError("Required!");
                    return;
                }
                spinner.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(userEmail,userPass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                spinner.setVisibility(View.GONE);
                                Toast.makeText(SignInPage.this, "Logged in!", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(SignInPage.this, ExplorePage.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        spinner.setVisibility(View.GONE);
                        Toast.makeText(SignInPage.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                        Log.d("tag error",e.toString());
                    }
                });
            }
        });
    }
}