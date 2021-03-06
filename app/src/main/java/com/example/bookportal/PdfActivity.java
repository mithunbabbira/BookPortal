package com.example.bookportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bookportal.adapter.ItemsRecyclerAdapter;
import com.example.bookportal.adapter.PdfRecyclerAdapter;
import com.example.bookportal.domain.Items;
import com.example.bookportal.domain.PdfItems;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PdfActivity extends AppCompatActivity {

    private Button goToSell;


    private Spinner semSelectSpinner;
    private Spinner subSelectSpinner;

    private List<PdfItems> mPDFList;
    private RecyclerView pdfRecycler;
    private PdfRecyclerAdapter pdfRecyclerAdapter ;


    ArrayAdapter<String> semSelectAdapter;
    ArrayList<String> semSelectList;

    ArrayAdapter<String> subSelectAdapter;
    ArrayList<String> subSelectList;

    ProgressBar progressBar;

    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    CollectionReference subjectsRef;
    private StorageReference mStorageRef;
    private  String collegePath, combinationPath, phone ,  sem , sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pdf);
//        mPDFList.clear();


        final GlobalData globalData = (GlobalData) getApplication();
        collegePath = globalData.getCollegePath();
        combinationPath = globalData.getCombinationPath();
        phone = globalData.getPhone();


        mStorageRef = FirebaseStorage.getInstance().getReference("PDFuploads");
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        semSelectSpinner = findViewById(R.id.semSelectSpinner);
        subSelectSpinner = findViewById(R.id.subSelectSpinner);
        progressBar = findViewById(R.id.main_progress_circle_1);


        mPDFList= new ArrayList<>();
        pdfRecycler = findViewById(R.id.pdfRecycler);
        pdfRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        pdfRecyclerAdapter = new PdfRecyclerAdapter(this, mPDFList);
        pdfRecycler.setAdapter(pdfRecyclerAdapter);



        semSelectList = new ArrayList<>();
        semSelectAdapter =new ArrayAdapter<String>(PdfActivity.this, android.R.layout.simple_spinner_dropdown_item, semSelectList);
        semSelectSpinner.setAdapter(semSelectAdapter);

        subSelectList = new ArrayList<>();
        subSelectAdapter =new ArrayAdapter<String>(PdfActivity.this, android.R.layout.simple_spinner_dropdown_item, subSelectList);
        subSelectSpinner.setAdapter(subSelectAdapter);


        // subjectsRef=mStore.collection("College");
        subjectsRef = mStore.collection("College")
                .document(collegePath)
                .collection("Combination")
                .document(combinationPath)
                .collection("PDFData");

          getSpinnerData();

        semSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sem = parent.getItemAtPosition(position).toString();
                globalData.setSem(sem);
                Toast.makeText(parent.getContext(), sem, Toast.LENGTH_SHORT).show();
                getSpinnerSubData(sem);

//                mPDFList.clear();
//
//                pdfRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        subSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sub = parent.getItemAtPosition(position).toString();
                globalData.setSubject(sub);
                mPDFList.clear();
                Toast.makeText(parent.getContext(), sub, Toast.LENGTH_SHORT).show();
                getRecyclerData(sub,sem);
//                pdfRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//
    }

    private void getRecyclerData(String sub, String sem) {
        progressBar.setVisibility(View.VISIBLE);


        mStore.collection("College")
                .document(collegePath)
                .collection("Combination")
                .document(combinationPath)
                .collection("PDFData")
                .document(sem)
                .collection("subjects")
                .document(sub)
                .collection("pdfData")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    mPDFList.clear();
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        PdfItems items = doc.toObject(PdfItems.class);
                        mPDFList.add(items);

                    }
                    //mProgressCircle.setVisibility(View.INVISIBLE);
                    pdfRecyclerAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);

                }


            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PdfActivity.this, MainActivity.class));
        finish();
    }

    private void getSpinnerSubData(final String sem) {
        subjectsRef.document(sem).collection("subjects")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    subSelectList.clear();
                    subSelectList.add(0,"Non Selected");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("name");
                        subSelectList.add(subject);
                    }
                    subSelectAdapter.notifyDataSetChanged();
                    subSelectSpinner.setSelection(0);
                }
            }
        });
    }

    private void getSpinnerData() {
        Log.i("TAG", "getSpinnerData: " + collegePath +" "+ combinationPath);

        subjectsRef
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    semSelectList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("sem");
                        Log.i("TAG", "onComplete: "+subject);
                        semSelectList.add(subject);
                    }
                    semSelectAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    public void goBAK(View view) {
        startActivity(new Intent(PdfActivity.this, MainActivity.class));
        finish();
    }
}