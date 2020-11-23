package com.kypcop.chroniclesofwwii.game.screen.gameElements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kypcop.chroniclesofwwii.R;

public class BackgroundView extends SurfaceView implements SurfaceHolder.Callback {


    Bitmap backgroundBitmap;
    BackgroundThread backgroundThread;
    boolean scaled = false;

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.world_4k);
        getHolder().addCallback(this);
    }


    public BackgroundView(Context context) {
        super(context);
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.world_4k);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        backgroundThread = new BackgroundThread(holder);
        backgroundThread.setRunning(true);
        backgroundThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        backgroundThread.setRunning(false);
        while(retry){
            try {
                backgroundThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class BackgroundThread extends Thread{
        SurfaceHolder surfaceHolder;
        private volatile boolean running = false;

        BackgroundThread(SurfaceHolder surfaceHolder){
            this.surfaceHolder = surfaceHolder;
        }

        void setRunning(boolean b){
            running = b;
        }

        @Override
        public void run() {

            int dx = 2;
            int dy = 1;

            int width = getWidth();
            int height = getHeight();

            if(!scaled){
                backgroundBitmap = checkBitmap(backgroundBitmap, width, height);
            }

            int top = backgroundBitmap.getHeight() / 3;
            int left = backgroundBitmap.getWidth() / 3;



            Canvas canvas;
            Rect dst = new Rect(0, 0, width, height);
            Rect src = new Rect(left, top, left + width, top + height);

            while(running){
                canvas = null;
                try{
                    long then = System.currentTimeMillis();
                    canvas = surfaceHolder.lockCanvas();
                    if(canvas == null) continue;

                    canvas.drawBitmap(backgroundBitmap, src, dst, null);

                    if(backgroundBitmap.getWidth() < src.right && dx > 0) dx = -dx;
                    if(src.left < 0 && dx < 0) dx = -dx;
                    if(backgroundBitmap.getHeight() < src.bottom && dy > 0) dy = -dy;
                    if(src.top < 0 && dy < 0) dy = -dy;

                    src.offset(dx, dy);
                    long now = System.currentTimeMillis();
                    long dif = now - then;
                    if(dif < 20){
                        sleep(20 - dif);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if(canvas != null){
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        public Bitmap checkBitmap(Bitmap oldBitmap, int width, int height){
            Bitmap bitmap = oldBitmap;

            while (bitmap.getHeight() < height / 2) {
                bitmap = Bitmap.createScaledBitmap(bitmap,
                        bitmap.getWidth() * 3/2, bitmap.getHeight() * 3/2, false);
            }
            while (bitmap.getHeight() / 2 > height) {
                bitmap = Bitmap.createScaledBitmap(bitmap,
                        bitmap.getWidth() * 2/3, bitmap.getHeight() * 2/3, false);
            }
            return bitmap;
        }

    }
}
