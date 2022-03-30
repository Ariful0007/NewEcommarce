package com.meass.newecommarce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class Order_places extends AppCompatActivity {


    private final String TAG = this.getClass().getSimpleName()+"_xlr8";
    private FirebaseFirestore firebaseFirestore;

    private FirestoreRecyclerAdapter adapter;
    private LottieAnimationView tv_no_item;


    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private LottieAnimationView emptycart;
    CollectionReference collectionReference;
    KProgressHUD progressHUD;


    HashMap<String,String> user;
    public static ArrayList<PlacedOrderModel> placedOrderModelArrayList;
    public static ArrayList<SingleProductModel> singleProductModelArrayList;
    public static HashMap<String,ArrayList<SingleProductModel>> placedOrderModelArrayListHashMap;
    private ArrayList<String> snapshotIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_places);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Order Placed ");
        progressHUD = KProgressHUD.create(Order_places.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        FirebaseApp.initializeApp(this);
        firebaseFirestore = FirebaseFirestore.getInstance();

        snapshotIdList = new ArrayList<>();

        placedOrderModelArrayList = new ArrayList<>();
        singleProductModelArrayList = new ArrayList<>();
        emptycart = findViewById(R.id.empty_cart);
        placedOrderModelArrayListHashMap = new HashMap<>();
        mRecyclerView = findViewById(R.id.recyclerview);
        tv_no_item = findViewById(R.id.tv_no_cards);



        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();


        //if (mRecyclerView != null) {
        //to enable optimization of recyclerview
        //   mRecyclerView.setHasFixedSize(true);
        // }

        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);


        collectionReference = firebaseFirestore.collection("Orders_Check").document("01593838386@gmail.com")
                .collection("Admin_Check");


        //saveOrderData();

        mRecyclerView.setVisibility(View.INVISIBLE);
        populateRecyclerView();


    }
    private void progress_check() {
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    private void saveOrderData(){



        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){

                    for(final QueryDocumentSnapshot snapshot : task.getResult()){
                        Log.d(TAG,snapshot.getId()+" => "+snapshot.getData());

                        final PlacedOrderModel placedOrderModel = snapshot.toObject(PlacedOrderModel.class);

                        placedOrderModelArrayList.add(placedOrderModel);
                        Log.d(TAG+"_1", String.valueOf(placedOrderModelArrayList));

                        collectionReference.document(snapshot.getId()).collection("Items").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if(task.isSuccessful()){
                                            singleProductModelArrayList = new ArrayList<>();

                                            for(QueryDocumentSnapshot snapshot1: task.getResult()){
                                                Log.d(TAG,snapshot.getId()+" => "+snapshot1.getId()+" : "+snapshot1.getData());
                                                singleProductModelArrayList.add(snapshot1.toObject(SingleProductModel.class));
                                                Log.d(TAG+"_2", String.valueOf(singleProductModelArrayList));
                                            }
                                            placedOrderModelArrayListHashMap.put(placedOrderModel.getOrderid(),singleProductModelArrayList);
                                            Log.d(TAG+"_3", String.valueOf(placedOrderModelArrayListHashMap));
                                        }
                                    }
                                });
                    }
                }
            }
        });



    }

    private void populateRecyclerView() {

        final Query topInvoiceQuery = firebaseFirestore.collection("Orders_Check").document("01593838386@gmail.com")
                .collection("Admin_Check");

        topInvoiceQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    Log.d(TAG,"Task Success");
                    if(task.getResult().size()==0){
                        emptycart.setVisibility(View.VISIBLE);
                        tv_no_item.setVisibility(View.GONE);
                        return;
                    }

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Log.d(TAG, document.getId() + " => " + document.getData());
                        snapshotIdList.add(document.getId());
                    }

                    if(tv_no_item.getVisibility()== View.VISIBLE){
                        tv_no_item.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }


                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }

            }
        });


        final FirestoreRecyclerOptions<PlacedOrderModel> response = new FirestoreRecyclerOptions.Builder<PlacedOrderModel>()
                .setQuery(topInvoiceQuery, PlacedOrderModel.class)
                .build();



        //Say Hello to our new FirebaseUI android Element, i.e., FirebaseRecyclerAdapter
        adapter =new FirestoreRecyclerAdapter<PlacedOrderModel, Order_places.MovieViewHolder>(response) {
            @Override
            protected void onBindViewHolder(@NonNull final Order_places.MovieViewHolder holder, final int position, @NonNull final PlacedOrderModel model) {
                Log.d(TAG,"Inside onBindViewHolder for pos : "+position);


                holder.orderAmt.setText("৳ "+model.getTotal_amount().substring(0,model.getTotal_amount().length()-2));
                Log.d(TAG,"Model: "+position+" Amt: "+model.getTotal_amount());
                String date_time = model.getOrder_date_time();
                String date = date_time.split("-")[0];
                String time = date_time.split("-")[1].replace(".",":");

                holder.orderDate.setText(date+", "+time.substring(0,time.length()-3));
                holder.orderCount.setText(model.getNo_of_items()+" Items");
                holder.orderStatus.setText(model.getOrder_status());

                ArrayList<String> images = model.getPlaced_order_images();


                if(images.size()>0) {

                    Log.d(TAG,"IMGS : "+images.size());
                    switch (images.size()) {
                        case 1:
                            holder.cv1.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(0)).into(holder.prod_img_1);
                            holder.cv2.setVisibility(View.INVISIBLE);
                            holder.cv3.setVisibility(View.INVISIBLE);
                            holder.cv4.setVisibility(View.INVISIBLE);
                            holder.extraImg.setVisibility(View.INVISIBLE);
                            break;

                        case 2:
                            holder.cv1.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(0)).into(holder.prod_img_1);
                            holder.cv2.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(1)).into(holder.prod_img_2);
                            holder.cv3.setVisibility(View.INVISIBLE);
                            holder.cv4.setVisibility(View.INVISIBLE);
                            holder.extraImg.setVisibility(View.INVISIBLE);
                            break;

                        case 3:
                            holder.cv1.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(0)).into(holder.prod_img_1);
                            holder.cv2.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(1)).into(holder.prod_img_2);
                            holder.cv3.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(2)).into(holder.prod_img_3);
                            holder.cv4.setVisibility(View.INVISIBLE);
                            holder.extraImg.setVisibility(View.INVISIBLE);
                            break;

                        case 4:
                            holder.cv1.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(0)).into(holder.prod_img_1);
                            holder.cv2.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(1)).into(holder.prod_img_2);
                            holder.cv3.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(2)).into(holder.prod_img_3);
                            holder.cv4.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(3)).into(holder.prod_img_4);
                            holder.extraImg.setVisibility(View.INVISIBLE);
                            break;

                        default:
                            holder.cv1.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(0)).into(holder.prod_img_1);
                            holder.cv2.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(1)).into(holder.prod_img_2);
                            holder.cv3.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(2)).into(holder.prod_img_3);
                            holder.cv4.setVisibility(View.VISIBLE);
                            Picasso.get().load(images.get(3)).into(holder.prod_img_4);
                            holder.extraImg.setVisibility(View.VISIBLE);
                            break;


                    }
                }


                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      holder.orderDate.setOnLongClickListener(new View.OnLongClickListener() {
                          @Override
                          public boolean onLongClick(View v) {
                              final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(Order_places.this)
                                      .setBackgroundColor(R.color.colorAccent)
                                      .setimageResource(R.drawable.trash)
                                      .setTextTitle("Delete")
                                      .setCancelable(true)
                                      .setTextSubTitle("Are you want to delete this order?")
                                      .setPositiveButtonText("Cancel")
                                      .setPositiveColor(R.color.colorPrimaryDark)
                                      .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                                          @Override
                                          public void OnClick(View view, Dialog dialog) {
                                              dialog.dismiss();



                                          }
                                      }).setNegativeColor(R.color.toolbar)
                                      .setNegativeButtonText("Delete")
                                      .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                                          @Override
                                          public void OnClick(View view, Dialog dialog) {
                                              dialog.dismiss();
                                              progress_check();
                                              String order_id=model.getOrderid();
                                              firebaseFirestore.collection("Orders_Check").document("01880921069@gmail.com")
                                                      .collection("Admin_Check").document(snapshotIdList.get(position))
                                                      .delete()
                                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<Void> task) {
                                                              if (task.isSuccessful()) {
                                                                  progressHUD.dismiss();
                                                                  startActivity(new Intent(getApplicationContext(),Main_Dash.class));

                                                              }
                                                          }
                                                      }).addOnFailureListener(new OnFailureListener() {
                                                  @Override
                                                  public void onFailure(@NonNull Exception e) {
                                                      Toasty.error(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT,true).show();
                                                  }
                                              });




                                          }
                                      })
                                      .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                      .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                      .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                      .setCancelable(false)
                                      .build();
                              alert.show();
                              return false;
                          }
                      });
                        Intent intent = new Intent(Order_places.this,OrderTrackActivity.class);
                        intent.putExtra("placed_order_model",model);
                        intent.putExtra("snapshot_id",snapshotIdList.get(position));
                        intent.putExtra("name",model.getDeleiveryname());
                        intent.putExtra("address",model.getDeliverymobile_no());
                        intent.putExtra("phone",model.getDeliveryemail());
                        intent.putExtra("delivary_date",model.getDelivery_date());

                        startActivity(intent);


/*
 Intent intent = new Intent(Order_places.this,OrderTrackActivity.class);
                        intent.putExtra("placed_order_model",model);
                        intent.putExtra("snapshot_id",snapshotIdList.get(position));
                        intent.putExtra("name",model.getDeleiveryname());
                        intent.putExtra("address",model.getDeliverymobile_no());
                        intent.putExtra("phone",model.getDeliveryemail());
                        intent.putExtra("delivary_date",model.getDelivery_date());
                        Toast.makeText(Order_places.this, ""+model.getDeliveryemail(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);

 */

                    }
                });





            }


            @NonNull
            @Override
            public Order_places.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
                Log.d(TAG,"Inflating Layout");
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.custom_order_history_layout, group, false);

                return new MovieViewHolder(view);

            }


        };


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Top Inv Resp: "+response.getSnapshots());
            }
        },10000);



        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);




    }

    //viewHolder for our Firebase UI
    public static class MovieViewHolder extends RecyclerView.ViewHolder{

        TextView orderAmt;
        ImageView prod_img_1;
        ImageView prod_img_2;
        ImageView prod_img_3;
        ImageView prod_img_4;
        TextView orderDate;
        TextView orderCount;
        TextView orderStatus;
        ImageView track_hist;
        ImageView extraImg;
        CardView cv1,cv2,cv3,cv4;
        ConstraintLayout layout;

        View mView;
        public MovieViewHolder(View v) {
            super(v);
            mView = v;
            orderAmt = v.findViewById(R.id.order_total_amt);
            prod_img_1 = v.findViewById(R.id.img_1);
            prod_img_2 = v.findViewById(R.id.img_2);
            prod_img_3 = v.findViewById(R.id.img_3);
            prod_img_4 = v.findViewById(R.id.img_4);
            orderDate = v.findViewById(R.id.order_date);
            orderCount = v.findViewById(R.id.no_of_items);
            orderStatus = v.findViewById(R.id.order_status);
            track_hist = v.findViewById(R.id.track_history);
            cv1 = v.findViewById(R.id.cv_1);
            cv2 = v.findViewById(R.id.cv_2);
            cv3 = v.findViewById(R.id.cv_3);
            cv4 = v.findViewById(R.id.cv_4);
            extraImg = v.findViewById(R.id.extra_img_iv);
            layout = v.findViewById(R.id.card_layout);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d("xlr8","Adapter Listening");
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("xlr8","Adapter Not  Listening");
        adapter.stopListening();
    }

    public void Notifications(View view) {
    }

    public void viewProfile(View view) {

    }
    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}