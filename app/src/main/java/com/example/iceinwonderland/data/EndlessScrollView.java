package com.example.iceinwonderland.data;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.iceinwonderland.R;

public class EndlessScrollView extends View {

    private Bitmap backgroundBitmap;
    private float backgroundY;
    private Paint paint;

    public EndlessScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EndlessScrollView(Context context) {
        super(context);
        init();
    }

    private void init(){
        //
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        paint = new Paint();

        startBackgroundAnimation();
    }

    private void startBackgroundAnimation(){
        ValueAnimator animator = ValueAnimator.ofFloat(getHeight()+((float)backgroundBitmap.getHeight()/5),0);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
           backgroundY = (float) animation.getAnimatedValue();
           invalidate();
        });

        animator.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int diff = 0;
        if(getWidth()<= backgroundBitmap.getWidth()) {
            diff = (getWidth() - backgroundBitmap.getWidth()) / 2;
        }
        canvas.drawBitmap(backgroundBitmap, diff,-getHeight() + backgroundY, paint);
        if(backgroundY >= getHeight()){
            backgroundY = 0;
        }
    }
}
