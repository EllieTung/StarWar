package iii.org.tw.starwar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by YunHua on 9/29/16.
 */
public class Ghost implements Runnable {
    private boolean flag=true;
    public int gX,gY;
    private int gDirection;
    private int gStep;
    private int gSpeed;
    public int gW,gH;
    private Bitmap bmpGhostl=null,bmpGhostr=null;
    Rect gRect=new Rect();
    private int hitGhost=5;

    public Ghost(Bitmap bmpGhostl,Bitmap bmpGhostr) {
        this.bmpGhostl=bmpGhostl;
        this.bmpGhostr=bmpGhostr;
        ghostInit();
        new Thread(this).start();
    }
    private void ghostInit(){
        gW=bmpGhostl.getWidth();
        gH=bmpGhostl.getHeight();
        gX=(int)(Math.random()*(OuterSpace.viewW-gW));
        gY=(int)(Math.random()*(OuterSpace.viewH-gH));
        gSpeed=(int)(Math.random()*16+10);
        ghostStepAndDirection();
    }
    private void ghostStepAndDirection(){
        gStep=(int)(Math.random()*16+5);
        gDirection=(int)(Math.random()*8);
    }
    protected void drawGhost(Canvas canvas){
        if(gDirection==3||gDirection==4||gDirection==5){
            canvas.drawBitmap(this.bmpGhostl,gX,gY,null);
        }else{
            canvas.drawBitmap(this.bmpGhostr,gX,gY,null);
        }
    }
    private void ghostPosition(){
        if(gStep<=0){
            ghostStepAndDirection();
        }
        if (gDirection == 1 || gDirection == 2 || gDirection == 3){
            // y 值增加
            gY += gSpeed;
        }
        if (gDirection == 5 || gDirection == 6 || gDirection == 7){
            // y 值減少
            gY -= gSpeed;
        }
        if (gDirection == 1 || gDirection == 0 || gDirection == 7){
            // x 值增加
            gX += gSpeed;
        }
        if (gDirection == 3 || gDirection == 4 || gDirection == 5){
            // x 值減少
            gX -= gSpeed;
        }
        if (gX <= 0) {
            gX = 0;
            ghostStepAndDirection();  //重新產生步數與方向
        }
        if (gX >= 15*OuterSpace.viewW/16) {
            gX = 15*OuterSpace.viewW/16;
            ghostStepAndDirection();
        }
        if (gY <= 0) {

            gY = 0;
            ghostStepAndDirection();
        }
        if (gY >= 7*OuterSpace.viewH/9) {
            gY = 7*OuterSpace.viewH/9;
            ghostStepAndDirection();
        }

        gRect.set(gX, gY, gX + gW, gY + gH) ;
        gStep--;
    }
    protected void ghostTouch(int touchX,int touchY ){
        if(gRect.contains(touchX,touchY)){
            hitGhost--;
            if(hitGhost<=0) {
                flag = false;
            }
        }
    }

    protected boolean ghostLife(){
        return flag;
    }

    @Override
    public void run() {
        while (flag){
            ghostPosition();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }Thread.interrupted();

    }
}
