package com.meass.newecommarce;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;

//My_ListAdapter
public class My_ListAdapter extends RecyclerView.Adapter<My_ListAdapter.myView> {
    private final List<Product_vendor> data;
    FirebaseFirestore firebaseFirestore;

    public My_ListAdapter(List<Product_vendor> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public My_ListAdapter.myView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_cardview_layout, parent, false);
        return new My_ListAdapter.myView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull My_ListAdapter.myView holder, final int position) {

        String image = data.get(position).getProimage();
        holder.login_desc.setText(data.get(position).getProname());
        holder.login1.setText("৳ "+data.get(position).getProprice());
        try {
            Picasso.get().load(image).into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(v.getContext())
                        .setBackgroundColor(R.color.whiteCardColor)
                        .setimageResource(R.drawable.show_c)
                        .setTextTitle("Order")
                        .setCancelable(true)
                        .setTextSubTitle("Are you want to show details of this order?")
                        .setPositiveButtonText("Cancel")
                        .setPositiveColor(R.color.colorPrimaryDark)
                        .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                dialog.dismiss();



                            }
                        }).setNegativeColor(R.color.toolbar)
                        .setNegativeButtonText("Show")
                        .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                dialog.dismiss();
                                Intent intent=new Intent(v.getContext(),Order_Details.class);
                                intent.putExtra("pro_image",data.get(position).getProimage());
                                intent.putExtra("pro_name",data.get(position).getProname());
                                intent.putExtra("pro_price",data.get(position).getProprice());
                                intent.putExtra("deli_name",data.get(position).getName());
                                intent.putExtra("deli_mobi",data.get(position).getPhone());
                                intent.putExtra("deli_address",data.get(position).getAddress());
                                intent.putExtra("deli_date",data.get(position).getDelivary_date());
                                intent.putExtra("uuid",data.get(position).getUuid());
                                v.getContext().startActivity(intent);




                            }
                        })
                        .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setCancelable(false)
                        .build();
                alert.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myView extends RecyclerView.ViewHolder {
        TextView login_desc, login1;
        ImageView imageView;

        public myView(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cardimage);
            login_desc = itemView.findViewById(R.id.cardcategory);
            login1 = itemView.findViewById(R.id.cardprice);
        }
    }

}