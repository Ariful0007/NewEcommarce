package com.meass.newecommarce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Step_2 extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ImageView imageView;
    private EditText edtname, edtemail, edtpass, edtcnfpass, edtnumber,phone11;

    CircleImageView image;
    ImageView upload;
    private Uri mainImageURI = null;
    boolean IMAGE_STATUS = false;
    Bitmap profilePicture;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final String TAG = this.getClass().getSimpleName();
    private String userId;

    private StorageReference storageReference;


    private Bitmap compressedImageFile;
    private KProgressHUD progressDialog;
    ImageButton image_button;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;//Firebase

    DocumentReference documentReference;
    Button floatingActionButton;
    FirebaseStorage storage;
    private static final int CAMERA_REQUEST = 1888;
    Button generate_btn;
    //doctor
    private static final int READCODE = 1;
    private static final int WRITECODE = 2;

    private Uri mainImageUri = null;
    EditText product_name,product_des,porduct_price;
    Button add_product;
    String url;
    KProgressHUD progressHUD;
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_2);
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
       try {
           url=getIntent().getStringExtra("url");
       }catch (Exception e) {
           url=getIntent().getStringExtra("url");
       }
        progressHUD = KProgressHUD.create(Step_2.this);
        phone=findViewById(R.id.phone);



        toolbar.setTitle("Add Product");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(10.0f);
        imageView=findViewById(R.id.productimage);
        product_name=findViewById(R.id.product_name);
        product_des=findViewById(R.id.product_des);
        porduct_price=findViewById(R.id.porduct_price);
        add_product=findViewById(R.id.add_product);
        phone11=findViewById(R.id.phone11);
        try {
            Picasso.get().load(url).into(imageView);
        }catch (Exception e) {
        }
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random myRandom = new Random();
                final String randomkey=String.valueOf(myRandom.nextInt(100000));
                int main_id=Integer.parseInt(randomkey);
                String name=product_name.getText().toString();
                String por_de=product_des.getText().toString();
                String product_price1=porduct_price.getText().toString();
                String deliver=phone11.getText().toString();
                if (TextUtils.isEmpty(name)|| TextUtils.isEmpty(por_de)|| TextUtils.isEmpty(deliver)||TextUtils.isEmpty(product_price1)||TextUtils.isEmpty(phone.getText().toString())) {
                    Toast.makeText(Step_2.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    upload();
                }

            }
        });
    }

    private void upload() {
        String option[]={"Add Product"};
        AlertDialog.Builder builder=new AlertDialog.Builder(Step_2.this);
        builder.setTitle("Select a option")
                .setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //1
                        if (which==0) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("1")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"Shoes","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("Shoes")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        progressHUD.dismiss();
                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                        Toast.makeText(Step_2.this, "Product Add successfully Done", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                       else  if (which==1) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("2")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"Women","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("Women")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                   // progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        progressHUD.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                intent.putExtra("id",""+id);
                                                                                                startActivity(intent);

                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    //startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
//3
                        if (which==2) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("3")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"Kids","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("Kids")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    //progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        progressHUD.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                intent.putExtra("id",""+id);
                                                                                                startActivity(intent);

                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    // startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else  if (which==3) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("4")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"Others","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("Others")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        progressHUD.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                intent.putExtra("id",""+id);
                                                                                                startActivity(intent);

                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        //5
                        if (which==4) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("5")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"Kids99","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("Kids99")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    //progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                                                        dialog.dismiss();
                                                                                                        String[] element={"Three Pices","Two Pices","Shoes","Pant","Winter Jacket"};
                                                                                                        AlertDialog.Builder eele=new AlertDialog.Builder(Step_2.this);
                                                                                                        eele.setTitle("Size/Style")
                                                                                                                .setItems(element, new DialogInterface.OnClickListener() {
                                                                                                                    @Override
                                                                                                                    public void onClick(final DialogInterface dialog, int which) {
                                                                                                                        if (which==0) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","1");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("6")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                        else  if (which==1) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","2");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("6")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                        else  if (which==2) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","3");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("6")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                        else  if (which==3) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","4");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("6")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                        else  if (which==4) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","5");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("6")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                    }
                                                                                                                }).create().show();
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                String[] element={"Three Pices","Two Pices","Shoes","Pant","Winter Jacket"};
                                                                                                AlertDialog.Builder eele=new AlertDialog.Builder(Step_2.this);
                                                                                                eele.setTitle("Size/Style")
                                                                                                        .setItems(element, new DialogInterface.OnClickListener() {
                                                                                                            @Override
                                                                                                            public void onClick(final DialogInterface dialog, int which) {
                                                                                                                if (which==0) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","1");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("6")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                                else  if (which==1) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","2");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("6")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                                else  if (which==2) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","3");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("6")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                                else  if (which==3) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","4");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("6")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                                else  if (which==4) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","5");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("6")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                            }
                                                                                                        }).create().show();


                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    //startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else  if (which==5) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("6")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"metooo","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("metooo")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    //progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        progressHUD.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                intent.putExtra("id",""+id);
                                                                                                startActivity(intent);

                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    // startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
//3
                        if (which==6) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("7")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"electronices","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("electronices")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                   // progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        progressHUD.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                intent.putExtra("id",""+id);
                                                                                                startActivity(intent);

                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    //startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else  if (which==7) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("8")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"electronices_others","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("electronices_others")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                   // progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        progressHUD.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                intent.putExtra("id",""+id);
                                                                                                startActivity(intent);

                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    // startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        //9
                        if (which==8) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("9")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"main_man","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("main_man")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                   // progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                                                        dialog.dismiss();
                                                                                                        String[] element={"T-Shirt","Shirt","Shoes","Pant","Winter Jacket"};
                                                                                                        AlertDialog.Builder eele=new AlertDialog.Builder(Step_2.this);
                                                                                                        eele.setTitle("Size/Style")
                                                                                                                .setItems(element, new DialogInterface.OnClickListener() {
                                                                                                                    @Override
                                                                                                                    public void onClick(final DialogInterface dialog, int which) {
                                                                                                                        if (which==0) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","1");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("9")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                        else  if (which==1) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","2");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("9")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                      else  if (which==2) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","3");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("9")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                       else  if (which==3) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","4");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("9")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                       else  if (which==4) {
                                                                                                                            HashMap<String,Object> yean=new HashMap<>();

                                                                                                                            yean.put("finding","5");
                                                                                                                            firebaseFirestore.collection("Products")
                                                                                                                                    .document("9")
                                                                                                                                    .collection("List")
                                                                                                                                    .document(""+id)
                                                                                                                                    .set(yean)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                progressHUD.dismiss();
                                                                                                                                                dialog.dismiss();
                                                                                                                                                startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                    }
                                                                                                                }).create().show();

                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                String[] element={"T-Shirt","Shirt","Shoes","Pant","Winter Jacket"};
                                                                                                AlertDialog.Builder eele=new AlertDialog.Builder(Step_2.this);
                                                                                                eele.setTitle("Size/Style")
                                                                                                        .setItems(element, new DialogInterface.OnClickListener() {
                                                                                                            @Override
                                                                                                            public void onClick(final DialogInterface dialog, int which) {
                                                                                                                if (which==0) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","1");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("9")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                                else  if (which==1) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","2");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("9")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                                else  if (which==2) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","3");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("9")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                                else  if (which==3) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","4");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("9")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                                else  if (which==4) {
                                                                                                                    HashMap<String,Object> yean=new HashMap<>();

                                                                                                                    yean.put("finding","5");
                                                                                                                    firebaseFirestore.collection("Products")
                                                                                                                            .document("9")
                                                                                                                            .collection("List")
                                                                                                                            .document(""+id)
                                                                                                                            .set(yean)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        progressHUD.dismiss();
                                                                                                                                        dialog.dismiss();
                                                                                                                                        Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                                                        intent.putExtra("id",""+id);
                                                                                                                                        startActivity(intent);
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                            }
                                                                                                        }).create().show();


                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    //startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else  if (which==9) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("10")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"handbags","101");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("handbags")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    //progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        progressHUD.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                intent.putExtra("id",""+id);
                                                                                                startActivity(intent);

                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    // startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
//3
                        if (which==10) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("11")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"Beauty And Health","110");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("Shoes")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    //progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        progressHUD.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                intent.putExtra("id",""+id);
                                                                                                startActivity(intent);

                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    // startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else  if (which==11) {
                            progress_check();
                            Random myRandom = new Random();
                            final String randomkey=String.valueOf(myRandom.nextInt(100000));
                            final int id=Integer.parseInt(randomkey);
                            HashMap<String,Object> hashMapde=new HashMap<>();

                            hashMapde.put("deliver",phone11.getText().toString());
                            firebaseFirestore.collection("Delivercharge")
                                    .document("12")
                                    .collection("Charge")
                                    .document(""+id)
                                    .set(hashMapde)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            final GenericProductModel genericProductModel=new GenericProductModel(id,product_name.getText().toString(),url,product_des.getText().toString(),
                                    Float.parseFloat(porduct_price.getText().toString()),firebaseAuth.getCurrentUser().getEmail(),phone.getText().toString(),randomkey,"Toys And Baby Product","110");
                            firebaseFirestore.collection("Indivisiul_Products")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("List")
                                    .document(randomkey)
                                    .set(genericProductModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseFirestore.collection("Products").document("101").collection("Women")
                                                        .document(randomkey)
                                                        .set(genericProductModel)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                  //  progressHUD.dismiss();
                                                                    HashMap<String,Object> hashMap=new HashMap<>();

                                                                    hashMap.put("image",url);

                                                                    firebaseFirestore.collection("ImageSlider")
                                                                            .document("1")
                                                                            .collection(""+id)
                                                                            .add(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        AlertDialog.Builder myWarniing=new AlertDialog.Builder(Step_2.this);
                                                                                        myWarniing.setTitle("Conformation")
                                                                                                .setMessage("Are You Want To Add More  Image Of This Product")
                                                                                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        progressHUD.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                                                    }
                                                                                                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                                Intent intent=new Intent(getApplicationContext(),Image1.class);
                                                                                                intent.putExtra("id",""+id);
                                                                                                startActivity(intent);

                                                                                            }
                                                                                        }).create().show();
                                                                                    }
                                                                                }
                                                                            });

                                                                    //startActivity(new Intent(getApplicationContext(),Main_Dash.class));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }

                    }
                }).create().show();


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
        startActivity(new Intent(getApplicationContext(),Add_Image.class));

        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Add_Image.class));
    }
}