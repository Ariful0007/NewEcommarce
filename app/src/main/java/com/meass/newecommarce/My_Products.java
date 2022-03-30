package com.meass.newecommarce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class My_Products extends AppCompatActivity {
    String category;
    DocumentReference documentReference;
    RecyclerView recyclerView;
    My_product_Adapter getDataAdapter1;
    List<GenericProductModel> getList;
    String url;

    FirebaseUser firebaseUser;

    FirebaseAuth firebaseAuth, mAuth;
    FirebaseFirestore firebaseFirestore;
    private StaggeredGridLayoutManager mLayoutManager;
    String docuname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__products);
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        try {
            docuname=getIntent().getStringExtra("name");
        }catch (Exception e) {
            docuname=getIntent().getStringExtra("name");
        }

        toolbar.setTitle(" My Products");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(10.0f);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        getList = new ArrayList<>();
        getDataAdapter1 = new My_product_Adapter(getList);
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference =      firebaseFirestore.collection("Products").document("101").collection(docuname).document();
        recyclerView = findViewById(R.id.blog_list_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(getDataAdapter1);
        reciveData();

    }

    private void reciveData() {


        firebaseFirestore.collection("Products").document("101").collection(docuname).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange ds : queryDocumentSnapshots.getDocumentChanges()) {
                    if (ds.getType() == DocumentChange.Type.ADDED) {


                        GenericProductModel get = ds.getDocument().toObject(GenericProductModel.class);
                        getList.add(get);
                        getDataAdapter1.notifyDataSetChanged();
                    }

                }
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(getApplicationContext(),E_home.class));

        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),E_home.class));
    }
}
