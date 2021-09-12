package com.example.eaststar.midtermproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//메인화면을 구성하는 클래스
//Game, Help 클래스로 갈 수 있는 부분
//OptionsMenu를 이용해 Help로 가거나 배경화면을 바꿀 수 있도록 함
//DB를 이용해 점수와 시간 기록을 관리하고 메인화면에서 보여줌
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    RelativeLayout layout;
    TextView maxtime;
    TextView maxscore;

    int mxtime,mxscore;
    int skin;
    int background;

    String[] Skinname={"Classic","Celestial","Mist","Orchid","Verdant","Amber","Cobalt","Eidgenossin","Sigrun","Valkyrie","Devil","Imp"};
    Bitmap Skin[]=new Bitmap[12];

    TextView title,skinnameview;
    ImageView skinview;
    ImageButton prev,next,start,exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        setTheme(android.R.style.Theme_Holo_NoActionBar_Fullscreen);

        setContentView(R.layout.activity_main);



        //font
        Typeface font= Typeface.createFromAsset(this.getAssets(),"fonts/koverwatch.ttf");

        title=(TextView)findViewById(R.id.title);
        title.setTypeface(font);

        skinnameview=(TextView)findViewById(R.id.skinnameview);
        skinnameview.setTypeface(font);


        //game
        skin=0;
        background=0;

        layout=(RelativeLayout)findViewById(R.id.relativelayout);


        //bitmap
        Skin[0]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.classic);
        Skin[1]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.celestial);
        Skin[2]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.mist);
        Skin[3]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.orchid);
        Skin[4]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.verdant);
        Skin[5]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.amber);
        Skin[6]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.cobalt);
        Skin[7]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.eidgenossin);
        Skin[8]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.sigrun);
        Skin[9]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.valkyrie);
        Skin[10]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.devil);
        Skin[11]= BitmapFactory.decodeResource(this.getResources(),R.mipmap.imp);


        //view&button
        skinview=(ImageView)findViewById(R.id.skinview);

        prev=(ImageButton)findViewById(R.id.prevbtn);
        prev.setOnClickListener(this);

        next=(ImageButton)findViewById(R.id.nextbtn);
        next.setOnClickListener(this);

        start=(ImageButton)findViewById(R.id.startbtn);
        start.setOnClickListener(this);

        exit=(ImageButton)findViewById(R.id.exitbtn);
        exit.setOnClickListener(this);

        try{
            DBManager dbmgr=new DBManager(this);

            SQLiteDatabase sdb=dbmgr.getReadableDatabase();

            Cursor cursor=sdb.rawQuery("select id, score, time from record",null);

            boolean dbchk=false;

            while(cursor.moveToNext()) {
                mxscore = cursor.getInt(1);
                mxtime = cursor.getInt(2);
                dbchk=true;
            }

            if(!dbchk){
                mxscore=mxtime=0;

                ContentValues nullvalues=new ContentValues();
                nullvalues.put("id","tmp");
                nullvalues.put("score",0);
                nullvalues.put("time",0);

                sdb.insert("record",null,nullvalues);
            }

            maxtime=(TextView)findViewById(R.id.maxtime);
            maxscore=(TextView)findViewById(R.id.maxscore);

            maxscore.setText(Integer.toString(mxscore));
            maxtime.setText(Integer.toString(mxtime)+"s");

            dbmgr.close();

        }catch (SQLiteException e){

        }



    }


    @Override
    public void onClick(View v) {

        //skin
        if(v.getId()==prev.getId()){
            if(skin>0){
                --skin;
                skinview.setImageBitmap(Skin[skin]);
                skinnameview.setText(Skinname[skin]);
            }
        }

        if(v.getId()==next.getId()){
            if(skin<11){
                ++skin;
                skinview.setImageBitmap(Skin[skin]);
                skinnameview.setText(Skinname[skin]);
            }
        }


        //game
        if(v.getId()==start.getId()) {
            Intent intent = new Intent(this, Game.class);

            intent.putExtra("skin",skin);

            startActivityForResult(intent, 1);
        }

        if(v.getId()==exit.getId()){
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){

        if(resultCode==RESULT_OK){
            if(requestCode==1){

                int time,score;
                time=data.getIntExtra("time",0);
                score=data.getIntExtra("score",0);

                maxtime.setText(Integer.toString(Math.max(mxtime,time))+"s");
                maxscore.setText(Integer.toString(Math.max(mxscore,score)));

                if(mxtime<time){
                    mxtime=time;
                    maxtime.append("  (New Best!)");
                }
                else maxtime.append("  ("+Integer.toString(time)+"s)");

                if(mxscore<score){
                    mxscore=score;
                    maxscore.append("  (New Best!)");
                }
                else maxscore.append("  ("+Integer.toString(score)+")");

                try {
                    DBManager dbmgr = new DBManager(this);

                    SQLiteDatabase sdb = dbmgr.getReadableDatabase();

                    ContentValues values = new ContentValues();
                    values.put("score", mxscore);
                    values.put("time", mxtime);
                    sdb.update("record", values, "id=?", new String[]{"tmp"});

                    dbmgr.close();

                }catch (SQLException e){

                }

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.help) {
            Intent intent=new Intent(this,Help.class);
            startActivity(intent);
        }

        if(id==R.id.background1)layout.setBackgroundResource(R.drawable.background1);
        if(id==R.id.background2)layout.setBackgroundResource(R.drawable.background2);
        if(id==R.id.background3)layout.setBackgroundResource(R.drawable.background3);
        if(id==R.id.background4)layout.setBackgroundResource(R.drawable.background4);
        if(id==R.id.background5)layout.setBackgroundResource(R.drawable.background5);
        if(id==R.id.background6)layout.setBackgroundResource(R.drawable.background6);
        if(id==R.id.background7)layout.setBackgroundResource(R.drawable.background7);
        if(id==R.id.background8)layout.setBackgroundResource(R.drawable.background8);


        return super.onOptionsItemSelected(item);
    }

}
