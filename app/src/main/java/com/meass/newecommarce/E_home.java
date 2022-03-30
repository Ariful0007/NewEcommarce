package com.meass.newecommarce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class E_home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_home);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(" Categories");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(10.0f);
    }
    public void cardsActivity(View view) {
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","Shoes");
        startActivity(intent);
    }

    public void viewCart(View view) {
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","Women");
        startActivity(intent);
    }

    public void tshirtActivity(View view) {
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","Women");
        startActivity(intent);
        //startActivity(new Intent(E_Commarce_home.this, Women.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
        return true;
    }

    public void bagsActivity(View view) {

        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","Kids");
        startActivity(intent);
        // startActivity(new Intent(E_Commarce_home.this, Kids.class));
    }

    public void stationaryAcitivity(View view) {

        //startActivity(new Intent(MainActivity.this, Stationary.class));
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","Others");
        startActivity(intent);
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Main_Dash.class));
    }

    public void station1aryAcitivity(View view) {
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","Kids99");
        startActivity(intent);
    }

    public void Statiohnarky(View view) {
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","metooo");
        startActivity(intent);
    }

    public void bagfid(View view) {
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","electronices");
        startActivity(intent);
    }

    public void Sftathhhionarky(View view) {
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","electronices_others");
        startActivity(intent);

    }

    public void bagfid_fucking(View view) {
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","main_man");
        startActivity(intent);
    }

    public void Sftathhhionarky_bags(View view) {
        Intent intent=new Intent(getApplicationContext(),My_Products.class);
        intent.putExtra("name","handbags");
        startActivity(intent);
    }

    @Override
    public boolean onNavigateUp() {
        startActivity(new Intent(getApplicationContext(),Main_Dash.class));
        return  true;
    }
}