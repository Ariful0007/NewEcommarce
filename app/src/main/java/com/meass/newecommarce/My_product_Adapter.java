package com.meass.newecommarce;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;

//My_product_Adapter
public class My_product_Adapter extends RecyclerView.Adapter<My_product_Adapter.myView> {
    private final List<GenericProductModel> data;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    KProgressHUD progressHUD;

    public My_product_Adapter(List<GenericProductModel> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public My_product_Adapter.myView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_cardview_layout,parent,false);
        return new My_product_Adapter.myView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  My_product_Adapter.myView holder, final int position) {
        Float ff=data.get(position).getCardprice();
        String a=""+ff;
        String image=data.get(position).getCardimage();
        holder.login_desc.setText(data.get(position).getCardname());
        holder.login1.setText(a);
        try {
            Picasso.get().load(image).into(holder.imageView);
        }catch(Exception e) {
            e.printStackTrace();
        }
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String[] strings={"Update Product Details","Delete Products"};
                AlertDialog.Builder builder=new AlertDialog.Builder(v.getContext());
                builder.setTitle("Options")
                        .setItems(strings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (which==0) {
                                    String options[]={"Product Name","Product Details","Product Price","Vendor Contact number"};
                                    AlertDialog.Builder update=new AlertDialog.Builder(v.getContext());
                                    update.setTitle("Update Data Options")
                                            .setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                  dialog.dismiss();
                                                  if (which==0) {
                                                      dialog.dismiss();
                                                      final EditText input = new EditText(v.getContext());
                                                      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                              LinearLayout.LayoutParams.MATCH_PARENT,
                                                              LinearLayout.LayoutParams.MATCH_PARENT);
                                                      input.setLayoutParams(lp);
                                                      progressHUD = KProgressHUD.create(v.getContext());
                                                      input.setHint("Enter  Product Name");
                                                      new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                                                              .setTitle("Update")
                                                              .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      dialog.dismiss();
                                                                  }
                                                              }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {

                                                              if (TextUtils.isEmpty(input.getText().toString())) {

                                                                  Toasty.error(v.getContext(), "Given a product name", Toast.LENGTH_SHORT, true).show();
                                                                  return;
                                                              }
                                                              else {
                                                                  dialog.dismiss();
                                                                  progress_check();
                                                                  final String key=data.get(position).getKey();
                                                                  final String uuid=data.get(position).getUid();
                                                                  final String key2=data.get(position).getKey2();

                                                                  firebaseFirestore.collection("Products")
                                                                          .document(key2)
                                                                          .collection(key)
                                                                          .document(uuid)
                                                                          .get()
                                                                          .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                              @Override
                                                                              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                  if (task.isSuccessful()) {
                                                                                      if (task.getResult().exists()) {
                                                                                          firebaseFirestore.collection("Products")
                                                                                                  .document(key2)
                                                                                                  .collection(key)
                                                                                                  .document(uuid)
                                                                                                  .update("cardname",input.getText().toString())
                                                                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                      @Override
                                                                                                      public void onComplete(@NonNull Task<Void> task) {
                                                                                                          progressHUD.dismiss();
                                                                                                          Toasty.success(v.getContext(),"Done",Toasty.LENGTH_SHORT,true).show();

                                                                                                      }
                                                                                                  });
                                                                                      }
                                                                                      else {
                                                                                          progressHUD.dismiss();
                                                                                          Toasty.error(v.getContext(),"No Item Found Here",Toasty.LENGTH_SHORT,true).show();
                                                                                      }
                                                                                  }
                                                                                  else {
                                                                                      progressHUD.dismiss();
                                                                                      Toasty.error(v.getContext(),"No Item Found Here",Toasty.LENGTH_SHORT,true).show();
                                                                                  }
                                                                              }
                                                                          });



                                                              }
                                                          }
                                                      })
                                                              .setIcon(R.drawable.icon_int)
                                                              .setView(input)
                                                              .show();

                                                  }
                                                   else  if (which==1) {
                                                        dialog.dismiss();
                                                        final EditText input = new EditText(v.getContext());
                                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                                LinearLayout.LayoutParams.MATCH_PARENT);
                                                        input.setLayoutParams(lp);
                                                        progressHUD = KProgressHUD.create(v.getContext());
                                                        input.setHint("Enter  Product Details");
                                                        new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                                                                .setTitle("Update")
                                                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                if (TextUtils.isEmpty(input.getText().toString())) {

                                                                    Toasty.error(v.getContext(), "Given Product Details", Toast.LENGTH_SHORT, true).show();
                                                                    return;
                                                                }
                                                                else {
                                                                    dialog.dismiss();
                                                                    progress_check();
                                                                    final String key=data.get(position).getKey();
                                                                    final String uuid=data.get(position).getUid();
                                                                    final String key2=data.get(position).getKey2();

                                                                    firebaseFirestore.collection("Products")
                                                                            .document(key2)
                                                                            .collection(key)
                                                                            .document(uuid)
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        if (task.getResult().exists()) {
                                                                                            firebaseFirestore.collection("Products")
                                                                                                    .document(key2)
                                                                                                    .collection(key)
                                                                                                    .document(uuid)
                                                                                                    .update("carddiscription",input.getText().toString())
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            progressHUD.dismiss();
                                                                                                            Toasty.success(v.getContext(),"Done",Toasty.LENGTH_SHORT,true).show();

                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                        else {
                                                                                            progressHUD.dismiss();
                                                                                            Toasty.error(v.getContext(),"No Item Found Here",Toasty.LENGTH_SHORT,true).show();
                                                                                        }
                                                                                    }
                                                                                    else {
                                                                                        progressHUD.dismiss();
                                                                                        Toasty.error(v.getContext(),"No Item Found Here",Toasty.LENGTH_SHORT,true).show();
                                                                                    }
                                                                                }
                                                                            });



                                                                }
                                                            }
                                                        })
                                                                .setIcon(R.drawable.icon_int)
                                                                .setView(input)
                                                                .show();

                                                    }
                                                    if (which==2) {
                                                        dialog.dismiss();
                                                        final EditText input = new EditText(v.getContext());
                                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                                LinearLayout.LayoutParams.MATCH_PARENT);
                                                        input.setLayoutParams(lp);
                                                        progressHUD = KProgressHUD.create(v.getContext());
                                                        input.setHint("Enter  Product Price");
                                                        new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                                                                .setTitle("Update")
                                                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                if (TextUtils.isEmpty(input.getText().toString())) {

                                                                    Toasty.error(v.getContext(), "Given Product Price", Toast.LENGTH_SHORT, true).show();
                                                                    return;
                                                                }
                                                                else {
                                                                    dialog.dismiss();
                                                                    progress_check();
                                                                    final String key=data.get(position).getKey();
                                                                    final String uuid=data.get(position).getUid();
                                                                    final String key2=data.get(position).getKey2();

                                                                    firebaseFirestore.collection("Products")
                                                                            .document(key2)
                                                                            .collection(key)
                                                                            .document(uuid)
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        if (task.getResult().exists()) {
                                                                                            firebaseFirestore.collection("Products")
                                                                                                    .document(key2)
                                                                                                    .collection(key)
                                                                                                    .document(uuid)
                                                                                                    .update("cardprice",Float.parseFloat(input.getText().toString()))
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            progressHUD.dismiss();
                                                                                                            Toasty.success(v.getContext(),"Done",Toasty.LENGTH_SHORT,true).show();

                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                        else {
                                                                                            progressHUD.dismiss();
                                                                                            Toasty.error(v.getContext(),"No Item Found Here",Toasty.LENGTH_SHORT,true).show();
                                                                                        }
                                                                                    }
                                                                                    else {
                                                                                        progressHUD.dismiss();
                                                                                        Toasty.error(v.getContext(),"No Item Found Here",Toasty.LENGTH_SHORT,true).show();
                                                                                    }
                                                                                }
                                                                            });



                                                                }
                                                            }
                                                        })
                                                                .setIcon(R.drawable.icon_int)
                                                                .setView(input)
                                                                .show();

                                                    }
                                                    if (which==3) {
                                                        dialog.dismiss();
                                                        final EditText input = new EditText(v.getContext());
                                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                                LinearLayout.LayoutParams.MATCH_PARENT);
                                                        input.setLayoutParams(lp);
                                                        progressHUD = KProgressHUD.create(v.getContext());
                                                        input.setHint("Enter  Vendor Number");
                                                        new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                                                                .setTitle("Update")
                                                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                if (TextUtils.isEmpty(input.getText().toString())) {

                                                                    Toasty.error(v.getContext(), "Given a Vendor Number ", Toast.LENGTH_SHORT, true).show();
                                                                    return;
                                                                }
                                                                else {
                                                                    dialog.dismiss();
                                                                    progress_check();
                                                                    final String key=data.get(position).getKey();
                                                                    final String uuid=data.get(position).getUid();
                                                                    final String key2=data.get(position).getKey2();

                                                                    firebaseFirestore.collection("Products")
                                                                            .document(key2)
                                                                            .collection(key)
                                                                            .document(uuid)
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        if (task.getResult().exists()) {
                                                                                            firebaseFirestore.collection("Products")
                                                                                                    .document(key2)
                                                                                                    .collection(key)
                                                                                                    .document(uuid)
                                                                                                    .update("phone",input.getText().toString())
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            progressHUD.dismiss();
                                                                                                            Toasty.success(v.getContext(),"Done",Toasty.LENGTH_SHORT,true).show();

                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                        else {
                                                                                            progressHUD.dismiss();
                                                                                            Toasty.error(v.getContext(),"No Item Found Here",Toasty.LENGTH_SHORT,true).show();
                                                                                        }
                                                                                    }
                                                                                    else {
                                                                                        progressHUD.dismiss();
                                                                                        Toasty.error(v.getContext(),"No Item Found Here",Toasty.LENGTH_SHORT,true).show();
                                                                                    }
                                                                                }
                                                                            });



                                                                }
                                                            }
                                                        })
                                                                .setIcon(R.drawable.icon_int)
                                                                .setView(input)
                                                                .show();

                                                    }

                                                }
                                            }).create().show();
                                }
                                else if(which==1) {
                                    progressHUD = KProgressHUD.create(v.getContext());



                                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(v.getContext())
                                            .setBackgroundColor(R.color.whiteCardColor)
                                            .setimageResource(R.drawable.trash)
                                            .setTextTitle("Delete")
                                            .setCancelable(true)
                                            .setTextSubTitle("Are you want to delete ")
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
                                                    final String key=data.get(position).getKey();
                                                    final String uuid=data.get(position).getUid();
                                                    final String key2=data.get(position).getKey2();
                                                    firebaseFirestore.collection("Indivisiul_Products")
                                                            .document(firebaseAuth.getCurrentUser().getEmail())
                                                            .collection("List")
                                                            .document(uuid)
                                                            .delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        firebaseFirestore.collection("Products")
                                                                                .document(key2)
                                                                                .collection(key)
                                                                                .document(uuid)
                                                                                .delete()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            progressHUD.dismiss();
                                                                                            v.getContext().startActivity(new Intent(v.getContext(),Main_Dash.class));
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
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
                                }
                            }
                        }).create().show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myView extends RecyclerView.ViewHolder{
        TextView login_desc,login1;
        ImageView imageView;

        public myView(@NonNull  View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.cardimage);
            login_desc=itemView.findViewById(R.id.cardcategory);
            login1=itemView.findViewById(R.id.cardprice);
        }
    }
    private void progress_check() {
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

}