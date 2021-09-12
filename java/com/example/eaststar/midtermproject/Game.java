package com.example.eaststar.midtermproject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

//게임 실행 화면 구성 및 구현 클래스
//비트맵 이미지를 이용해 배경, 메르시, 아이템, 장애물을 표시
public class Game extends AppCompatActivity{

    int width,height;

    int time,score;
    double fall;
    boolean flag;

    Typeface font;

    Bitmap Skin[]=new Bitmap[12];
    Bitmap Item[]=new Bitmap[4];
    Bitmap Obstacle[]=new Bitmap[2];


    Intent it;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        it=getIntent();

        setContentView(new MyView(this));
    }

    //메르시에 대한 정보를 저장해 놓는 클래스
    //메르시를 움직이게 하는 메소드를 포함
    class Mercy{
        int x,y;
        int w,h;
        double b;
        double g=25;
        double vx;
        double vy;
        int d;
        int scale,k;
        int skin;
        int resttime[];

        public Mercy(int x,int y,int w,int h,int skin){
            this.x=x;
            this.y=y;
            this.w=w;
            this.h=h;
            this.skin=skin;
            b=1;
            vx=0;
            vy=0;
            d=1;
            scale=k=1;
            resttime=new int[4];
            resttime[0]=resttime[1]=resttime[2]=resttime[3]=0;
        }

        void move(){
            x+=vx*k;
            if(x-w/2<0&&vx<0)x=w/4;
            if(x+w/2>width&&vx>0)x=width-w/4;

            vy+=g-b*vy;
            if(vy<0)vy=0;
        }

    }

    //아이템에 대한 정보를 저장해 놓는 클래스
    //아이템을 움직이게 하는 메소드를 포함
    class Item{
        int x,y;
        int w,h;
        int type;

        public Item(int x,int y,int w,int h,int type){
            this.x=x;
            this.y=y;
            this.w=w;
            this.h=h;
            this.type=type;
        }

        void move(double vy){
            this.y+=vy;
        }

    }

    //장애물에 대한 정보를 저장해 놓는 클래스
    //장애물을 움직이게 하는 메소드 포함
    class Obstacle{
        int x,y;
        int w,h;
        double vx,vy;
        int type;

        public Obstacle(int x,int y,int w,int h,int type,double vx,double vy){
            this.x=x;
            this.y=y;
            this.w=w;
            this.h=h;
            this.type=type;
            this.vx=vx;
            this.vy=vy;
        }

        void move(double f){
            this.x+=vx;
            this.y+=this.vy+f;
        }
    }

    //게임의 플레이를 처리하는 부분
    //화면의 터치를 처리하여 메르시를 움직이게 함
    class MyView extends View{

        Mercy mercy;
        ArrayList<Item> itemlist;
        ArrayList<Obstacle> obstaclelist;


        Handler mHandler=new Handler(){
            public void handleMessage(Message msg){

                if(flag) {
                    invalidate();
                    mHandler.sendEmptyMessageDelayed(0, 50);
                }
            }
        };

        public MyView(Context context) {
            super(context);

            DisplayMetrics disp=getApplicationContext().getResources().getDisplayMetrics();

            width=disp.widthPixels;
            height=disp.heightPixels-500;

            this.setBackgroundResource(R.drawable.background5);

            //base
            time=score=0;
            fall=20000;
            flag=true;

            font= Typeface.createFromAsset(context.getAssets(),"fonts/koverwatch.ttf");


            //bitmap
            Skin[0]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.classic);
            Skin[1]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.celestial);
            Skin[2]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.mist);
            Skin[3]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.orchid);
            Skin[4]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.verdant);
            Skin[5]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.amber);
            Skin[6]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.cobalt);
            Skin[7]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.eidgenossin);
            Skin[8]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.sigrun);
            Skin[9]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.valkyrie);
            Skin[10]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.devil);
            Skin[11]= BitmapFactory.decodeResource(context.getResources(),R.mipmap.imp);


            Item[0]=BitmapFactory.decodeResource(context.getResources(),R.mipmap.guardian_angel);
            Item[1]=BitmapFactory.decodeResource(context.getResources(),R.mipmap.angelic_descent);
            Item[2]=BitmapFactory.decodeResource(context.getResources(),R.mipmap.credit);
            Item[3]=BitmapFactory.decodeResource(context.getResources(),R.mipmap.point);


            Obstacle[0]=BitmapFactory.decodeResource(context.getResources(),R.mipmap.trap);
            Obstacle[1]=BitmapFactory.decodeResource(context.getResources(),R.mipmap.frag);

            //game
            int skin=it.getIntExtra("skin",0);

            mercy=new Mercy(width/2,400,Skin[skin].getWidth(),Skin[skin].getHeight(),skin);

            itemlist=new ArrayList<>();

            obstaclelist=new ArrayList<>();

            mHandler.sendEmptyMessageDelayed(0,0);

        }


        Bitmap flip(Bitmap d) {
            Matrix m = new Matrix();
            m.preScale(-1, 1);
            Bitmap dst = Bitmap.createBitmap(d,0,0,d.getWidth(),d.getHeight(),m,false);
            dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
            return dst;
        }


        int dis(int x,int y){
            return x*x+y*y;
        }

        @Override
        protected void onDraw(Canvas canvas) {


            //text
            Paint pnt=new Paint();

            pnt.setAntiAlias(true);
            pnt.setColor(Color.WHITE);
            pnt.setTextSize(70);
            pnt.setTypeface(font);

            canvas.drawText("Time: "+Integer.toString(time/20)+"s",10,70,pnt);
            canvas.drawText("Score: "+Integer.toString(score),10,140,pnt);


            if(fall<=0){
                flag=false;
                String s="0M";
                Rect rect=new Rect();
                pnt.getTextBounds(s,0,s.length(),rect);
                pnt.setTextSize(100);

                canvas.drawText(s,width/2-rect.width()/2,100,pnt);

                Handler finHandler=new Handler(){
                    public void handleMessage(Message msg){

                    }
                };

                finHandler.sendEmptyMessageDelayed(0,500);

                it.putExtra("time",time/20);
                it.putExtra("score",score);

                setResult(RESULT_OK,it);
                finish();
            }

            String s=Integer.toString((int)fall)+"M";
            Rect rect=new Rect();
            pnt.getTextBounds(s,0,s.length(),rect);
            pnt.setTextSize(100);

            canvas.drawText(s,width/2-rect.width()/2,100,pnt);

            ++time;
            fall-=mercy.vy;


            //bitmap
            for(int i=0;i<4;++i){
                if(mercy.resttime[i]>0)--mercy.resttime[i];
            }


            if(mercy.resttime[1]>0)mercy.b=1.6;
            else mercy.b=1;

            if(mercy.resttime[2]>0)mercy.k=0;
            else if(mercy.resttime[0]>0)mercy.k=2;
            else mercy.k=1;

            Rect range=new Rect(mercy.x-mercy.w/2*mercy.scale,mercy.y-mercy.h/2*mercy.scale,
                    mercy.x+mercy.w/2*mercy.scale,mercy.y+mercy.h/2*mercy.scale);

            if(mercy.d>0)canvas.drawBitmap(Skin[mercy.skin],null,range,null);
            else canvas.drawBitmap(flip(Skin[mercy.skin]),null,range,null);

            mercy.move();


            for(int i=itemlist.size()-1;i>=0;--i){
                Item item=itemlist.get(i);
                item.move(-mercy.vy);
                if(dis(mercy.x-item.x,mercy.y-item.y)<dis(mercy.w+item.w,mercy.h+item.h)/16){

                    if(mercy.resttime[item.type]==0){
                        if(item.type==0)mercy.k=2;
                        else if(item.type==1)mercy.b=1.6;
                        else if(item.type==2)score+=10;
                        else score+=100;
                    }

                    if(item.type==0)mercy.resttime[item.type]=30;
                    else if(item.type==1)mercy.resttime[item.type]=20;
                    else{}

                    itemlist.remove(i);
                }
                else {
                    if (item.y + item.h / 2 < 0) itemlist.remove(i);
                    else {
                        range = new Rect(item.x - item.w / 2, item.y - item.h / 2,
                                    item.x + item.w / 2, item.y + item.h / 2);
                        canvas.drawBitmap(Item[item.type], null, range, null);
                    }
                }
            }


            for(int i=obstaclelist.size()-1;i>=0;--i){
                Obstacle obstacle=obstaclelist.get(i);
                obstacle.move(-mercy.vy);
                if(dis(mercy.x-obstacle.x,mercy.y-obstacle.y)<dis(mercy.w+obstacle.w,mercy.h+obstacle.h)/25){

                    if(mercy.resttime[obstacle.type+2]==0){
                        if(obstacle.type==0)mercy.k=0;
                        else fall=0;
                    }

                    if(obstacle.type==0)mercy.resttime[obstacle.type+2]=60;
                    else{}

                    obstaclelist.remove(i);
                }
                else {
                    if(obstacle.x+obstacle.w/2<0)obstaclelist.remove(i);
                    else if(obstacle.x-obstacle.w/2>width)obstaclelist.remove(i);
                    else if(obstacle.y+obstacle.h/2<0)obstaclelist.remove(i);
                    else {
                        range = new Rect(obstacle.x - obstacle.w / 2, obstacle.y - obstacle.h / 2,
                                obstacle.x + obstacle.w / 2, obstacle.y + obstacle.h / 2);
                        canvas.drawBitmap(Obstacle[obstacle.type], null, range, null);
                    }
                }
            }


            Random random=new Random();
            if(random.nextInt((int)fall/20+1000)<250) {
                for (int i = 0; i < 2; ++i) {
                    int rand = random.nextInt(100);
                    int type;

                    if (rand < 10) type = 0;
                    else if (rand < 20) type = 1;
                    else if (rand < 70) type = 2;
                    else if (rand < 75) type = 3;
                    else continue;

                    int w = Item[type].getWidth();
                    int h = Item[type].getHeight();

                    Item item = new Item(random.nextInt(width - w) + w / 2, height + h / 2, w, h, type);

                    itemlist.add(item);

                }
            }
            if(random.nextInt((int)fall/20+1000)<200) {
                for (int i = 0; i < 1; ++i) {
                    int rand = random.nextInt(100);
                    int type;

                    if (rand < 10) type = 0;
                    else if (rand < 30) type = 1;
                    else continue;

                    int w = Obstacle[type].getWidth();
                    int h = Obstacle[type].getHeight();
                    double vx=0,vy=0;

                    if(type==1){
                        vx=random.nextInt(50)-25;
                        vy=random.nextInt(50)-25;
                    }

                    Obstacle obstacle = new Obstacle(random.nextInt(width - w) + w / 2, height + h / 2, w, h, type, vx, vy);

                    obstaclelist.add(obstacle);

                }
            }

        }


        //move
        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if(event.getAction()==MotionEvent.ACTION_DOWN
                    ||event.getAction()==MotionEvent.ACTION_MOVE){

                int x=(int)event.getX();
                if(x<width/2){
                    mercy.vx=-8;
                    mercy.d=-1;
                }
                else{
                    mercy.vx=8;
                    mercy.d=1;
                }
            }

            if(event.getAction()==MotionEvent.ACTION_DOWN){
                onHandler();
            }

            if(event.getAction()==MotionEvent.ACTION_UP){
                mercy.vx=0;
                tHandler.removeCallbacks(r);
            }

            return true;
        }


        Handler tHandler=new Handler();
        Runnable r;

        void onHandler() {
            if (flag) {
                r = new Runnable() {

                    @Override
                    public void run() {
                        mercy.move();
                        tHandler.postDelayed(r, 10);
                    }

                };

                tHandler.postDelayed(r, 10);
            }

        }

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
