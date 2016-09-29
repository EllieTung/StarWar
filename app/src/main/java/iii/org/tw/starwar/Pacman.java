package iii.org.tw.starwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by YunHua on 9/29/16.
 */
public class Pacman implements Runnable {
    private boolean flag=true;
    public int pX,pY;
    private int pW,pH;
    private int turningNumber=1;
    private Bitmap pmr,pmr2,pml,pml2;
    private Context context;
    private Paint paint;
    public int maxHp=100,currentHp;
    public int pacmanDirect;
    Rect pacmanRect=new Rect();

    public Pacman(Context context) {
        this.context=context;

        pmr= BitmapFactory.decodeResource(context.getResources(),R.drawable.pmr);
        pmr2=BitmapFactory.decodeResource(context.getResources(),R.drawable.pmr2);
        pml=BitmapFactory.decodeResource(context.getResources(),R.drawable.pml);
        pml2=BitmapFactory.decodeResource(context.getResources(),R.drawable.pml2);

        currentHp=maxHp;
        paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);

        pW=pmr.getWidth();
        pH=pmr.getHeight();
        new Thread(this).start();

    }

    protected boolean pacmanCollision(int ghostX,int ghostY){
        if(pacmanRect.contains(ghostX,ghostY)){
            currentHp-=10;
            if(currentHp<=0){
                flag=false;
            }
            return true;
        }
        return false;
    }
    protected boolean pacmanLife(){
        return flag;
    }
    private Bitmap turningImage(int pacmanDirect){
        switch (pacmanDirect) {
            case 1:
                if (turningNumber == 1) {
                    turningNumber = 2;
                    return pmr;
                } else {
                    turningNumber = 1;
                    return pmr2;
                }
            case 2:
                if (turningNumber == 1) {
                    turningNumber = 2;
                    return pml;
                } else {
                    turningNumber = 1;
                    return pml2;
                }
                default:
                    return pmr;
        }
    }

    protected void drawPacman(Canvas canvas){
        canvas.drawBitmap(turningImage(pacmanDirect),pX,pY,null);
        int hpWidth = (int)( ((float)currentHp/(float)maxHp) *(float)(pmr.getWidth()));
        if(hpWidth<=0){
            hpWidth=0;
        }
        canvas.drawLine(pX,pY-10,pX+hpWidth,pY-10,paint);


    }

    @Override
    public void run() {
        while (flag){
            pacmanRect.set(pX,pY,pX+pW,pY+pH);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }Thread.interrupted();
    }
}
