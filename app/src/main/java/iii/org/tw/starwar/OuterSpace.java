package iii.org.tw.starwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by YunHua on 9/28/16.
 */
public class OuterSpace extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    SurfaceHolder sHolder;
    Bitmap bmpControler,bmpBackground,bmpGhostr,bmpGhostl;
    private Canvas canvas=null;
    int cX,cY,bgX,bgX2=-1280;
    Rect srcRect,bgRect,bgRect2;
    int originTouchX,originTouchY,touchX,touchY;
    boolean cIsTouch=false;
    int tempX,tempY;
    private Thread osThread;
    private boolean flag=true;
    static int viewW,viewH;
    private Pacman pacman;
    private Ghost ghost;
    private int ghostAmount;
    private SoundPool soundPool;
    private int bomb,scream;
    private CopyOnWriteArrayList<Ghost> ghosts;
    private boolean isWin=false;


    public OuterSpace(Context context) {
        super(context);
//---------- for surfaceView
        getHolder().addCallback(this);
        sHolder=getHolder();
//---------- for rolling background
        srcRect=new Rect(0,0,1280,720);
        bgRect=new Rect(bgX,0,1280,720);
        bgRect2=new Rect(bgX2,0,0,720);
//----------
        viewW=getResources().getDisplayMetrics().widthPixels;
        viewH=getResources().getDisplayMetrics().heightPixels;
        pacman=new Pacman(context);
//----------for sound
        soundPool=new SoundPool(5, AudioManager.STREAM_MUSIC, 5);
        bomb=soundPool.load(context,R.raw.bomb,1);
        scream=soundPool.load(context,R.raw.scream,1);

        bmpControler= BitmapFactory.decodeResource(getResources(),R.drawable.controller);
        bmpBackground=BitmapFactory.decodeResource(getResources(),R.drawable.gamebackground);
        bmpGhostl=BitmapFactory.decodeResource(getResources(),R.drawable.ghost_l);
        bmpGhostr=BitmapFactory.decodeResource(getResources(),R.drawable.ghost_r);

        //ghost=new Ghost(bmpGhostl,bmpGhostr);
        ghostAmount=5;


        pacman.pX=viewW/10;
        pacman.pY=viewH/3;
        cX=viewW/16;
        cY=6*viewH/9;
        osThread=new Thread(this);
        osInit();
    }
    private void osInit(){
        ghosts=new CopyOnWriteArrayList<>();
        for(int i=0;i<ghostAmount;i++){
            Ghost ghost=new Ghost(bmpGhostl,bmpGhostr);
            ghosts.add(ghost);
        }
    }

    public void DoDraw(){
        if(pacman.pX==viewW/16){
            bgX+=0;
            bgX2+=0;
        }else if(pacman.pX<viewW/16){
            bgX+=6;
            bgX2+=6;
        }else if(pacman.pX>15*viewW/16){
            bgX -= 12;
            bgX2 -= 12;
        }else{
            bgX-=6;
            bgX2-=6;
        }
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
            for(Ghost g:ghosts) {
                g.ghostTouch(originTouchX, originTouchY);
                if(g.ghostTouch(originTouchX, originTouchY)){
                    soundPool.play(bomb, 1, 1, 0, 0, 1);
                    Log.d("Ellie","bomb");
                }
            }

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
        osThread.interrupt();
    }

    @Override
    public void run() {
        while (flag) {
            if (pacman.pacmanLife() && isWin) {
                   Log.d("Ellie","win");
                } else if(!isWin){
                    for (Ghost a : ghosts) {
                        pacman.pacmanCollision((a.gX + a.gW / 2), (a.gY + a.gH / 2));
                    }

                    try {
                        Thread.sleep(100);
                        canvas = sHolder.lockCanvas();
                        DoDraw();
                        pacman.drawPacman(canvas);
                        for (Ghost b : ghosts) {
                            if (b.ghostLife()) {
                                b.drawGhost(canvas);
                            }
                        }
                        for (Ghost c : ghosts) {
                            if (!c.ghostLife()) {
                                soundPool.play(scream, 1, 1, 0, 0, 1);
                                ghosts.remove(c);
                                Log.d("Ellie", "Screm");
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (canvas != null) {
                            sHolder.unlockCanvasAndPost(canvas);
                            Log.d("Ellie",""+ghosts.size());

                            if(ghosts.size()==0){
                                isWin=true;
                                flag=false;

                            }
                        }
                    }
            }else{
                Log.d("Ellie","pacman die");
                flag=false;
            }
        }

    }
}
