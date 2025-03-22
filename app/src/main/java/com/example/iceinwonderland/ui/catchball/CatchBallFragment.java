package com.example.iceinwonderland.ui.catchball;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.ui.GameResultCallback;
import com.example.iceinwonderland.ui.result.GameOverFragment;

public class CatchBallFragment extends Fragment {

    private TextView  startLabel, timerLabel;
    private ImageView box, orange, pink, black;
    private int frameHeight, boxSize, screenWidth, screenHeight;
    private float boxY;
    private float orangeX, orangeY;
    private float pinkX, pinkY;
    private float blackX, blackY;
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean action_flg = false;
    private boolean start_flg = false;

    private CountDownTimer countDownTimer; // 追加
    private final long timeLimit = 15000; // 15秒（ミリ秒）
    private GameResultCallback callback;

    public static Fragment newInstance() {
        return new CatchBallFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (GameResultCallback) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catchball, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startLabel = view.findViewById(R.id.startLabel);
        timerLabel = view.findViewById(R.id.timerLabel); // タイマー用テキスト
        box = view.findViewById(R.id.box);
        orange = view.findViewById(R.id.orange);
        pink = view.findViewById(R.id.pink);
        black = view.findViewById(R.id.black);

        // スクリーンサイズを取得
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        // 初期位置設定（画面外）
        orange.setX(-80.0f);
        orange.setY(-80.0f);
        pink.setX(-80.0f);
        pink.setY(-80.0f);
        black.setX(-80.0f);
        black.setY(-80.0f);

        timerLabel.setText("Time : 30");

        // **boxのY座標を正しく取得するためにpost()を使用**
        view.post(new Runnable() {
            @Override
            public void run() {
                frameHeight = view.getHeight();
                boxSize = box.getHeight();
                boxY = box.getY(); // **boxの初期位置を取得**
            }
        });

        // **タッチイベントを設定**
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!start_flg) {
                    // **ゲーム開始**
                    start_flg = true;
                    startLabel.setVisibility(View.GONE);

                    // タイマー開始
                    startTimer();

                    handler.post(runnable = new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                            handler.postDelayed(this, 20);
                        }
                    });
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // タッチしたら上に移動
                        action_flg = true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // タッチを離したら下に落ちる
                        action_flg = false;
                    }
                }
                return true;
            }
        });
    }

    public void changePos() {
        boxMove();

        // オレンジボールの移動
        orangeX -= 12;
        if (orangeX < 0) {
            orangeX = screenWidth;
            orangeY = (float) Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        // ピンクボールの移動
        pinkX -= 16;
        if (pinkX < 0) {
            pinkX = screenWidth;
            pinkY = (float) Math.floor(Math.random() * (frameHeight - pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        // 黒トゲの移動
        blackX -= 20;
        if (blackX < 0) {
            blackX = screenWidth;
            blackY = (float) Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        // 当たり判定
        if (hitCheck(orangeX, orangeY, orange)) {
            moveResult(false);
            return;
        }
        if (hitCheck(pinkX, pinkY, pink)) {
            moveResult(false);
            return;
        }
        if (hitCheck(blackX, blackY, black)) {
            callback.onGameResult(false);
            moveResult(false);
            return;
        }
    }

    public void boxMove() {
        if (action_flg) {
            // 上に移動
            boxY -= 20;
        } else {
            // 下に落ちる
            boxY += 20;
        }

        // 画面外に出ないようにする
        if (boxY < 0) boxY = 0;
        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;

        box.setY(boxY);
    }

    public boolean hitCheck(float x, float y, ImageView object) {
        float objectCenterX = x + object.getWidth() / 2;
        float objectCenterY = y + object.getHeight() / 2;
        float boxCenterX = box.getX() + box.getWidth() / 2;
        float boxCenterY = box.getY() + box.getHeight() / 2;

        float boxHalfWidth = box.getWidth() / 2;
        float boxHalfHeight = box.getHeight() / 2;
        float objectHalfWidth = object.getWidth() / 2;
        float objectHalfHeight = object.getHeight() / 2;

        return (Math.abs(objectCenterX - boxCenterX) < boxHalfWidth + objectHalfWidth) &&
                (Math.abs(objectCenterY - boxCenterY) < boxHalfHeight + objectHalfHeight);
    }

    private void startTimer() {
        Log.d("startTimer", "startTimer");
        countDownTimer = new CountDownTimer(timeLimit, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerLabel.setText("Time : " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
               moveResult(true);
            }
        }.start();
    }

    private void stopTimer(){
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void moveResult(boolean isClear) {
        stopTimer();
        handler.removeCallbacks(runnable);
        handler = null;

        if (callback == null)
            return;
        if (isClear) {
            callback.onGameResult(true);
        } else {
            callback.onGameResult(false);
        }

    }
}
