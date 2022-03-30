package com.meass.newecommarce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main_Dash extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar mainToolbar;
    private String current_user_id;
    private BottomNavigationView mainBottomNav;
    private DrawerLayout mainDrawer;
    private ActionBarDrawerToggle mainToggle;
    private NavigationView mainNav;

    FrameLayout frameLayout;
    private TextView drawerName;
    private CircleImageView drawerImage;
    FirebaseAuth firebaseAuth;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseFirestoreSettings settings;
    private DatabaseReference mUserRef;
    String category;
    DocumentReference documentReference;
    RecyclerView recyclerView;
    My_ListAdapter getDataAdapter1;
    List<Product_vendor> getList;
    String url;

    FirebaseUser firebaseUser;

    private StaggeredGridLayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__dash);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        toolbar.setTitle("E commarce Admin");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(10.0f);
        mainDrawer=findViewById(R.id.main_activity);
        mainNav = findViewById(R.id.main_nav);
        mainNav.setNavigationItemSelectedListener(this);
        mainToggle = new ActionBarDrawerToggle(this,mainDrawer,toolbar,R.string.open,R.string.close);
        mainDrawer.addDrawerListener(mainToggle);
        mainToggle.setDrawerIndicatorEnabled(true);
        mainToggle.syncState();
        //BottomNavigationViewHelper.disableShiftMode(mainBottomNav);
        if (mAuth.getCurrentUser() != null) {
            mainNav = findViewById(R.id.main_nav);
            View headerView = mainNav.getHeaderView(0);
            drawerName = headerView.findViewById(R.id.nav_name);
            drawerImage = headerView.findViewById(R.id.nav_image);



            settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();

            firebaseFirestore.setFirestoreSettings(settings);

            firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                    .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("MAIN_NAV", "listen:error", e);
                                return;
                            }
                            if (documentSnapshot.exists()) {

                                String image = documentSnapshot.getString("image");

                                drawerName.setText(documentSnapshot.getString("name"));
                                try {
                                    Picasso.get()
                                            .load(R.drawable.profile)
                                            .into(drawerImage);
                                }catch(Exception e1) {
                                    Picasso.get()
                                            .load(R.drawable.profile)
                                            .into(drawerImage);
                                }
                            }
                        }
                    });
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            getList = new ArrayList<>();
            getDataAdapter1 = new My_ListAdapter(getList);
            documentReference = firebaseFirestore.collection("Indi_orders")
                    .document(firebaseAuth.getCurrentUser().getEmail())
                    .collection("Oders").document();
            recyclerView = findViewById(R.id.blog_list_view);
            recyclerView.setHasFixedSize(true);
            mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(getDataAdapter1);
            reciveData();




            mainNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.addNote:
                            startActivity(new Intent(getApplicationContext(),Second_Splash.class));

                            return true;
                        case R.id.add:
                            startActivity(new Intent(getApplicationContext(),Add_Image.class));

                            return true;
                        case R.id.Order_plasce:
                            startActivity(new Intent(getApplicationContext(),Order_places.class));

                            return true;
                        case R.id.settings:
                            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));

                            return true;
                        case R.id.my_product:
                            startActivity(new Intent(getApplicationContext(),E_home.class));

                            return true;
                        case R.id.add_a:
                            startActivity(new Intent(getApplicationContext(),Add_bannar.class));

                            return true;
                        case R.id.show:
                            startActivity(new Intent(getApplicationContext(),Show_Bannar.class));

                            return true;
                        case R.id.history:
                            startActivity(new Intent(getApplicationContext(),My_Orders.class));

                            return true;

                        case R.id.logout:


                            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(Main_Dash.this)
                                    .setBackgroundColor(R.color.colorAccent)
                                    .setimageResource(R.drawable.logouttt)
                                    .setTextTitle("Logout")
                                    .setTextSubTitle("Are you want to logout?")
                                    .setPositiveButtonText("Cancel")
                                    .setPositiveColor(R.color.colorPrimaryDark)
                                    .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                                        @Override
                                        public void OnClick(View view, Dialog dialog) {
                                            dialog.dismiss();



                                        }
                                    }).setNegativeColor(R.color.toolbar)
                                    .setNegativeButtonText("Logout")
                                    .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                                        @Override
                                        public void OnClick(View view, Dialog dialog) {
                                            dialog.dismiss();
                                            mAuth.signOut();
                                        }
                                    })
                                    .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                    .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                    .setCancelable(false)
                                    .build();
                            alert.show();


                    }


                    return false;
                }
            });



        }
    }

    private void reciveData() {
        firebaseFirestore.collection("Indi_orders")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("Oders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for (DocumentChange ds : queryDocumentSnapshots.getDocumentChanges()) {
                    if (ds.getType() == DocumentChange.Type.ADDED) {


                        Product_vendor get = ds.getDocument().toObject(Product_vendor.class);
                        getList.add(get);
                        getDataAdapter1.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {


            /*AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Exit")
                    .setMessage("Are you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finishAffinity();

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.create().show();*/
        final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(Main_Dash.this)
                .setBackgroundColor(R.color.white)
                .setimageResource(R.drawable.exit)
                .setTextTitle("Exit")
                .setCancelable(false)
                .setTextSubTitle("Are you want to exit")
                .setBody("User is not stay at app when user click exit button.")
                .setPositiveButtonText("No")
                .setPositiveColor(R.color.toolbar)
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();

                    }
                })
                .setNegativeButtonText("Exit")
                .setNegativeColor(R.color.colorPrimaryDark)
                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                        finishAffinity();

                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setCancelable(false)
                .build();
        alert.show();

    }
}