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
        //初期位置？
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        paint = new Paint();

        startBackgroundAnimation();
    }

    private void startBackgroundAnimation(){
        //どれくらい動かしたいか？
        ValueAnimator animator = ValueAnimator.ofFloat(getHeight()+((float)backgroundBitmap.getHeight()/5),0);
        //何ミリ秒？
        animator.setDuration(1000);
        //何回？
        animator.setRepeatCount(ValueAnimator.INFINITE);
        //経過時間の最後に達したらどうするか
        animator.setRepeatMode(ValueAnimator.RESTART);
        //経過時間の割合を計算するために使用する時間補間器
        animator.setInterpolator(new LinearInterpolator());
        //リスナーを渡す
        animator.addUpdateListener(animation -> {
            //変化したbackgroundYのfloat受け取る
           backgroundY = (float) animation.getAnimatedValue();
           invalidate();
        });
        //アニメーションスタート！
        animator.start();
    }

    @Override
    //onDraw=Viewを描画する時に呼び出される
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        //diff=データの違いを示すための単語
        int diff = 0;
        if(getWidth()<= backgroundBitmap.getWidth()) {
            diff = (getWidth() - backgroundBitmap.getWidth()) / 2;
        }
        //Canvasクラスのメソッド指定されたBitmapを描画する
        canvas.drawBitmap(backgroundBitmap, diff,-getHeight() + backgroundY, paint);
        if(backgroundY >= getHeight()){
            backgroundY = 0;
        }
    }
}
