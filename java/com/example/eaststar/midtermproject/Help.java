package com.example.eaststar.midtermproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Help extends AppCompatActivity implements View.OnClickListener{

    int page;
    ImageButton nextpage,prevpage;
    LinearLayout linearlayout[]=new LinearLayout[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page=0;

        setContentView(R.layout.activity_help);

        nextpage=(ImageButton)findViewById(R.id.nextpage);
        nextpage.setOnClickListener(this);

        prevpage=(ImageButton)findViewById(R.id.prevpage);
        prevpage.setOnClickListener(this);


        linearlayout[0]=(LinearLayout)findViewById(R.id.linearlayout_help1);
        linearlayout[1]=(LinearLayout)findViewById(R.id.linearlayout_help2);
        linearlayout[2]=(LinearLayout)findViewById(R.id.linearlayout_help3);

    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.nextpage){
            if(page<2){
                linearlayout[page].setVisibility(View.INVISIBLE);
                ++page;
                linearlayout[page].setVisibility(View.VISIBLE);
            }
        }

        if(v.getId()==R.id.prevpage){
            if(page>0){
                linearlayout[page].setVisibility(View.INVISIBLE);
                --page;
                linearlayout[page].setVisibility(View.VISIBLE);
            }
        }

        TextView pagenum=(TextView)findViewById(R.id.page);
        pagenum.setText("("+Integer.toString(page+1)+"/3)");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.x){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
