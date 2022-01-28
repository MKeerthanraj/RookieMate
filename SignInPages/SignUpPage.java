package com.kenoDigital.rookiemate.SignInPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kenoDigital.rookiemate.ExplorePage;
import com.kenoDigital.rookiemate.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpPage extends AppCompatActivity {

    Button loginSelectButton,SignUpButton;
    EditText emailEditText,passwordEditText,ConfirmPasswordEditText,nameEditText,branchEditText;
    TextView dateOfBirthEditText;
    RadioButton studentRadioButton,facultyRadioButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar spinner;
    Calendar calendar;
    int year,month,day;
    String dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        loginSelectButton = (Button) findViewById(R.id.loginSelectButton);
        SignUpButton = (Button)findViewById(R.id.signupButton);
        emailEditText = (EditText)findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        ConfirmPasswordEditText = (EditText) findViewById(R.id.ConfirmPasswordEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        dateOfBirthEditText = (TextView) findViewById(R.id.dateOfBirthEditText);
        branchEditText = (EditText) findViewById(R.id.branchEditText);
        studentRadioButton = (RadioButton) findViewById(R.id.studentRadioButton);
        facultyRadioButton = (RadioButton) findViewById(R.id.facultyRadioButton);
        spinner = (ProgressBar) findViewById(R.id.spinner);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        calendar=Calendar.getInstance();

        loginSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpPage.this,SignInPage.class));
                finish();
            }
        });

        dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(SignUpPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        dateOfBirthEditText.setText(SimpleDateFormat.getDateInstance().format(newDate.getTime()));
                        dob=SimpleDateFormat.getDateInstance().format(newDate.getTime());
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = emailEditText.getText().toString();
                String userPass = passwordEditText.getText().toString();
                String confUserPass = ConfirmPasswordEditText.getText().toString();
                String userName = nameEditText.getText().toString();
                String userBranch = branchEditText.getText().toString();
                String userProff = getProff();

                if(userEmail.isEmpty()){
                    emailEditText.setError("Required!");
                    return;
                }else if(!(userEmail.contains("@srmist.edu.in"))){
                    emailEditText.setError("Use only SRM email ID!");
                    return;
                }
                if(userPass.isEmpty()){
                    passwordEditText.setError("Required!");
                    return;
                }
                if(confUserPass.isEmpty()){
                    ConfirmPasswordEditText.setError("Required!");
                    return;
                }

                if(userName.isEmpty()){
                    nameEditText.setError("Required!");
                    return;
                }
                if(dob.isEmpty()){
                    dateOfBirthEditText.setError("Required!");
                    return;
                }
                if(userBranch.isEmpty()){
                    branchEditText.setError("Required!");
                    return;
                }
                if(userProff.matches("NA")){
                    Toast.makeText(SignUpPage.this, "Select Student/Faculty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!(userPass.length() >= 6)){
                    passwordEditText.setError("Password must be atleast of 6 characters!");
                    return;
                }


                if(userPass.matches(confUserPass) ){
                    spinner.setVisibility(View.VISIBLE);
                    fAuth.createUserWithEmailAndPassword(userEmail,userPass)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("fullName",userName);
                                    user.put("email",userEmail);
                                    user.put("dob",dob);
                                    user.put("branch",userBranch);
                                    user.put("type",userProff);

                                    fStore.collection("Users").document(fAuth.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            spinner.setVisibility(View.GONE);
                                            Toast.makeText(SignUpPage.this, "Account Created", Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(SignUpPage.this, ExplorePage.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            spinner.setVisibility(View.GONE);
                                            Toast.makeText(SignUpPage.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                            Log.d("tag error",e.toString());
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            spinner.setVisibility(View.GONE);
                            Toast.makeText(SignUpPage.this, "An Error has occurred!", Toast.LENGTH_SHORT).show();
                            Log.d("tag",e.toString());
                        }
                    });
                }else{
                    Toast.makeText(SignUpPage.this, "Passwords not matched!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private String getProff() {
        if(studentRadioButton.isChecked())
            return "Student";
        else if(facultyRadioButton.isChecked())
            return "Faculty";
        else
            return "NA";
    }
}