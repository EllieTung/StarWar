package iii.org.tw.starwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by YunHua on 9/28/16.
 */
public class OuterSpace extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    SurfaceHolder sHolder;
    Bitmap bmpControler,bmpBackground,bmpGhostr,bmpGhostl;
    private Canvas canvas=null;
    int cX,cY,pX,pY,bgX,bgX2=-1280;
    Rect srcRect,bgRect,bgRect2;
    int originTouchX,originTouchY,touchX,touchY;
    boolean cIsTouch=false;
    int tempX,tempY;
    private Thread osThread;
    private boolean flag=true;
    static int viewW,viewH;
    private int turningNumber=1;
    private Pacman pacman;
    private Ghost ghost;


    public OuterSpace(Context context) {
        super(context);
        getHolder().addCallback(this);
        sHolder=getHolder();
        srcRect=new Rect(0,0,1280,720);
        bgRect=new Rect(bgX,0,1280,720);
        bgRect2=new Rect(bgX2,0,0,720);
        viewW=getResources().getDisplayMetrics().widthPixels;
        viewH=getResources().getDisplayMetrics().heightPixels;
        pacman=new Pacman(context);

        bmpControler= BitmapFactory.decodeResource(getResources(),R.drawable.controller);
        bmpBackground=BitmapFactory.decodeResource(getResources(),R.drawable.gamebackground);
        bmpGhostl=BitmapFactory.decodeResource(getResources(),R.drawable.ghost_l);
        bmpGhostr=BitmapFactory.decodeResource(getResources(),R.drawable.ghost_r);

        ghost=new Ghost(bmpGhostl,bmpGhostr);


        pacman.pX=viewW/10;
        pacman.pY=viewH/3;
        cX=viewW/16;
        cY=6*viewH/9;
        osThread=new Thread(this);
    }

    public void DoDraw(){

        bgX-=3;
        bgX2-=3;
        bgRect.set(bgX,0,1280+bgX,720);
        bgRect2.set(bgX2,0,1280+bgX2,720);
        canvas.drawBitmap(bmpBackground,srcRect,bgRect,null);
        canvas.drawBitmap(bmpBackground,srcRect,bgRect2,null);
        if(bgX<=-1280){
            bgX=1280;
        }
        if(bgX2<=-1280){
            bgX2=1280;
        }

        //canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bmpControler, cX, cY, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            originTouchX = (int) event.getX();
            originTouchY = (int) event.getY();
            if((((int) (event.getX())) >= viewW / 16) &&
                 (((int) (event.getX())) <= (viewW / 16 + bmpControler.getWidth())) &&
                    (((int) (event.getY())) >= (6* viewH / 9)) &&
                         (((int) (event.getX())) <= ((6 * viewH / 9) + bmpControler.getHeight()))){

                            tempX=originTouchX-cX;
                            tempY=originTouchY-cY;
                            cIsTouch=true;
                        }

            ghost.ghostTouch(originTouchX,originTouchY);

        } else if (event.getAction() == MotionEvent.ACTION_MOVE && cIsTouch) {
            touchX = (int) event.getX();
            touchY = (int) event.getY();
            if (touchX - originTouchX > 0) {
                if (touchX >= (viewW / 8 )) {
                    cX =(viewW / 8 )- tempX;
                    pacman.pX+=5;
                    pacman.pacmanDirect=1;
                } else {
                    cX = touchX - tempX;
                    pacman.pX+=5;
                    pacman.pacmanDirect=1;
                }
            }
            if (touchX - originTouchX <= 0) {
                if (touchX <= 0) {
                    cX = 0- tempX;
                    pacman.pX-=5;
                    pacman.pacmanDirect=2;
                } else {
                    cX = touchX - tempX;
                    pacman.pX-=5;
                    pacman.pacmanDirect=2;
                }
            }
            if (touchY - originTouchY > 0) {
                if (touchY >= (7*viewH/9)) {
                    cY = (7*viewH/9)-tempY;
                    pacman.pY+=3;
                } else {
                    cY = touchY - tempY;
                    pacman.pY+=3;
                }
            }

            if (touchY - originTouchY <= 0) {
                if (touchY <= 5 * viewH / 9) {
                    cY = (5 * viewH / 9)-tempY;
                    pacman.pY-=3;
                } else {
                    cY = touchY - tempY;
                    pacman.pY-=3;
                }
            }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                cX = viewW / 16;
                cY = 6 * viewH / 9;
                cIsTouch = false;
            }
            return true;
        }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        osThread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {


    }

    @Override
    public void run() {
        while (flag) {
            pacman.pacmanCollision((ghost.gX+ghost.gW/2),(ghost.gY+ghost.gH/2));

            try {
                Thread.sleep(100);
                canvas = sHolder.lockCanvas();
                DoDraw();
                pacman.drawPacman(canvas);
                if(ghost.ghostLife()) {
                    ghost.drawGhost(canvas);
                }



            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if(canvas!=null){
                    sHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

    }
}
