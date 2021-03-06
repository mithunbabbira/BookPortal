package com.example.bookportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bookportal.adapter.ItemsRecyclerAdapter;

import com.example.bookportal.domain.Items;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

//public class MainActivity extends AppCompatActivity implements ItemsRecyclerAdapter.OnItemClickListener
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private LinearLayout pdfActivity, sellbook, bookstore ,scheme;

    private List<Items> mItemList;
    private RecyclerView itemRecyclerView;
    private ItemsRecyclerAdapter itemsRecyclerAdapter , searchRecyclerAdapter;

    private EditText mSearchText;


    private ProgressBar mProgressCircle;
    private Toolbar mToolBar;
    String collegePath, combinationPath , phone , name;

    private Boolean DataLoading =true;


    //SEARCH
    private List<Items> mItemSearchList;
    //private List<SearchItems> mItemSearchList;
    private RecyclerView mItemSearchRecyclerView;
    //private SearchRecyclerAdapter searchRecyclerAdapter;




    RecyclerView searchedRecycler, mostviewedRecycler;
    RecyclerView.Adapter adapter;
    ImageView menuIcon;
    LinearLayout contentView;
    static final float END_SCALE= 0.7f;

    //Drawer Menu

    DrawerLayout drawerlayout;
    NavigationView navvigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        setSupportActionBar(mToolBar);
        pdfActivity = findViewById(R.id.pdfActivity);
        sellbook = findViewById(R.id.sellbook);
        bookstore = findViewById(R.id.bookstore);
        scheme = findViewById(R.id.scheme);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        //mSearchText = findViewById(R.id.search_text);

        mProgressCircle = findViewById(R.id.main_progress_circle_1);

        final GlobalData globalData = (GlobalData) getApplication();

        globalData.setUserID(mAuth.getCurrentUser().getUid());


        //navigation
        contentView = findViewById(R.id.content);

        //menu
        menuIcon = findViewById(R.id.nav_menu);

        //Recyclers
        //searchedRecycler = findViewById(R.id.searched_recycler);
       // mostviewedRecycler = findViewById(R.id.mostviewed_recycler);

        //Menu
        drawerlayout = findViewById(R.id.drawer_layout);
        navvigationView = findViewById(R.id.navview);

        //Recyclers Method


        //navigation methods(menuicon)
        navigationDrawer();


        mItemList = new ArrayList<>();
        itemRecyclerView = findViewById(R.id.mostviewed_recycler);
        //itemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        itemRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        itemsRecyclerAdapter = new ItemsRecyclerAdapter(this, mItemList,false);
        itemRecyclerView.setAdapter(itemsRecyclerAdapter);


        pdfActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(DataLoading){
                    Toast.makeText(MainActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(MainActivity.this,PdfOperationActivity.class);
                    startActivity(intent);

                }


            }
        });

        scheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(DataLoading){
                    Toast.makeText(MainActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                }else{

                    Intent intent = new Intent(MainActivity.this,SchemeActivity.class);
                    startActivity(intent);

                }


            }
        });


        sellbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(DataLoading){
                    Toast.makeText(MainActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                }else{

                    Intent intent = new Intent(MainActivity.this,SellActivity.class);
                    startActivity(intent);

                }




            }
        });


        bookstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataLoading){
                    Toast.makeText(MainActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                }else{

                    Intent intent = new Intent(MainActivity.this,BookStoreActivity.class);
                    startActivity(intent);

                }




            }
        });


        mStore.collection("User").document(mAuth.getCurrentUser().getUid())
                .collection("Path").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        collegePath = document.getString("college");
                        combinationPath = document.getString("combination");
                        phone = document.getString("phone");
                        name = document.getString("name");
                        globalData.setCollegePath(collegePath);
                        globalData.setCombinationPath(combinationPath);
                        globalData.setPhone(phone);
                        globalData.setName(name);
                        getData();

                    }

                }
            }
        });

    }

    public void getData() {
        mStore.collection("College")
                .document(collegePath)
                .collection("Combination")
                .document(combinationPath)
                .collection("BookData")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        Items items = doc.toObject(Items.class);
                        mItemList.add(items);
                    }
                    DataLoading = false;
                    mProgressCircle.setVisibility(View.INVISIBLE);
                    itemsRecyclerAdapter.notifyDataSetChanged();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DataLoading = false;
                mProgressCircle.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Failed to load " +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }



    //Navigation methods defined


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.nav_shop:
                startActivity(new Intent(MainActivity.this, BookStoreActivity.class));
                break;
            case R.id.nav_pdf:
                startActivity(new Intent(MainActivity.this, PdfOperationActivity.class));
                break;
            case R.id.nav_home:
                break;
            case R.id.nav_myacc:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            case R.id.nav_sell_books:
                startActivity(new Intent(MainActivity.this, SellActivity.class));
                break;
            case R.id.nav_mybooks:
                startActivity(new Intent(MainActivity.this, MyBookActivity.class));
                break;
        }
        return true;


    
    }

    private void navigationDrawer() {
        //Navigation Drawer
        navvigationView.bringToFront();
        navvigationView.setNavigationItemSelectedListener(this);
        navvigationView.setCheckedItem(R.id.nav_home);





            menuIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DataLoading) {
                        Toast.makeText(MainActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                    } else {


                        if (drawerlayout.isDrawerVisible(GravityCompat.START)) {
                            drawerlayout.closeDrawer(GravityCompat.START);
                        } else
                            drawerlayout.openDrawer(GravityCompat.START);
                    }
                }


            });


        animateNavbar();
    }

    private void animateNavbar() {
        drawerlayout.setScrimColor(getResources().getColor(R.color.colorPrimary));
        drawerlayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                //scale the view on slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1-diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                //Translate teh view for scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslate = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslate);
//                super.onDrawerSlide(drawerView, slideOffset);
            }


        });
    }

    @Override
    public void onBackPressed() {
        if(drawerlayout.isDrawerVisible(GravityCompat.START)){
            drawerlayout.closeDrawer(GravityCompat.START);
        }
        else{
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);

        }

    }


// TESTING

}