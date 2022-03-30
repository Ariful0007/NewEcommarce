package com.meass.newecommarce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

public class Order_Details extends AppCompatActivity {
    String vendor_email;
    String proimage;
    String proname;
    String proprice;
    String desc ;
    String name,address,phone,delivary_date;
    String uuid;
    ImageView cardimage;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    TextView product_name,product_des,porduct_price,phone1,cardcategory,cardprice;
    Button add_product;
    KProgressHUD progressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__details);
        Toolbar toolbar = findViewById(R.id.profile_toolbar);

        toolbar.setTitle("Order Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(10.0f);
        cardimage=findViewById(R.id.cardimage);
        firebaseAuth=FirebaseAuth.getInstance();
        progressHUD = KProgressHUD.create(Order_Details.this);
        firebaseFirestore=FirebaseFirestore.getInstance();
        proimage=getIntent().getStringExtra("pro_image");
        proname=getIntent().getStringExtra("pro_name");
        proprice=getIntent().getStringExtra("pro_price");
        name=getIntent().getStringExtra("deli_name");
        phone=getIntent().getStringExtra("deli_mobi");
        address=getIntent().getStringExtra("deli_address");
        delivary_date=getIntent().getStringExtra("deli_date");
        uuid=getIntent().getStringExtra("uuid");
        product_name=findViewById(R.id.product_name);
        product_name.setText("Customer Name : "+name);
        product_des=findViewById(R.id.product_des);
        product_des.setText("Address  : "+address);
        porduct_price=findViewById(R.id.porduct_price);
        porduct_price.setText("Delivary Date :  "+delivary_date);
        phone1=findViewById(R.id.phone);
        phone1.setText("Customer Number : "+phone);
        add_product=findViewById(R.id.add_product);
        try {
            Picasso.get().load(proimage).into(cardimage);
        }catch (Exception e) {
        }
        cardcategory=findViewById(R.id.cardcategory);
        cardcategory.setText(proname);
        cardprice=findViewById(R.id.cardprice);
        cardprice.setText("৳ "+proprice);
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_check();
                Product_vendor product_vendor=new Product_vendor(firebaseAuth.getCurrentUser().getEmail(),proimage,proname,
                        proprice,desc,name,address,phone,delivary_date,uuid);
                firebaseFirestore.collection("History")
                        .document(firebaseAuth.getCurrentUser().getEmail())
                        .collection("List")
                        .add(product_vendor)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    firebaseFirestore.collection("Indi_orders")
                                            .document(firebaseAuth.getCurrentUser().getEmail())
                                            .collection("Oders").document(uuid)
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressHUD.dismiss();
                                                        startActivity(new Intent(getApplicationContext(),
                                                                Main_Dash.class));
                                                        finish();

                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        });



    }
    private void progress_check() {
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(getApplicationContext(),Main_Dash.class));

        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
    }
}