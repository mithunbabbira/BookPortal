package com.example.bookportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private EditText mUserName;
    private EditText mUserEmail;
    private EditText mUserPhone;
    private EditText mUserUSN;
    private TextView personName , personEmail;
    private RelativeLayout goToMyBook;


    boolean btnClicked = false;

    private ProgressBar mProgressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        mUserName = findViewById(R.id.profile_username);
        mUserEmail = findViewById(R.id.profile_useremail);
        mUserPhone = findViewById(R.id.profile_phone);
        mUserUSN = findViewById(R.id.profile_usn);
        personName =findViewById(R.id.person_name);
        personEmail =findViewById(R.id.person_email);
        goToMyBook =findViewById(R.id.relativeMybook);

        mProgressCircle = findViewById(R.id.progress_circle);
        mProgressCircle.setVisibility(View.INVISIBLE);


        goToMyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MyBookActivity.class));
                finish();

            }
        });




        mStore.collection("User").document(mAuth.getCurrentUser().getUid())
                .collection("Path").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mUserName.setText(document.getString("name"));
                        mUserEmail.setText(document.getString("email"));
                        mUserUSN.setText(document.getString("usn"));
                        mUserPhone.setText(document.getString("phone"));
                        personName.setText(document.getString("name"));
                        personEmail.setText(document.getString("email"));


//                        getData();

                    }

                }
            }
        });




    }

    public void updateData(View view) {
        final String userName, userEmail , userPhone ,userId , usn;

        userName = mUserName.getText().toString();
        userEmail = mUserEmail.getText().toString();
        userPhone = mUserPhone.getText().toString();
        usn = mUserUSN.getText().toString();
        userId = mAuth.getCurrentUser().getUid();

        if(!btnClicked){
            if(!userName.isEmpty() && !userEmail.isEmpty() && !userPhone.isEmpty() && !usn.isEmpty() ){
                btnClicked = true;
                mProgressCircle.setVisibility(View.VISIBLE);



                mStore.collection("User")
                        .document(userId)
                        .collection("Path")
                        .document(userId)
                        .update("name", userName
                                ,"email", userEmail
                                ,"phone", userPhone
                                ,"usn",usn)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                personName.setText(userName);
                                personEmail.setText(userEmail);

                                btnClicked = false;
                                mProgressCircle.setVisibility(View.INVISIBLE);
                                Toast.makeText(ProfileActivity.this, "Data updated", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btnClicked = false;

                        mProgressCircle.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
            else {
                Toast.makeText(this, "Please enter all the detail", Toast.LENGTH_SHORT).show();


            }



        }else{
            Toast.makeText(this, "loading please wait", Toast.LENGTH_SHORT).show();
        }





    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();
    }

    public void homescreen(View view) {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();

    }
}