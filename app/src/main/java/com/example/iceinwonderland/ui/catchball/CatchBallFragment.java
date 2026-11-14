package com.example.iceinwonderland.ui.catchball;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

public class CatchBallFragment extends Fragment {

    private TextView  startLabel, timerLabel;
    private ImageView box, orange, pink, black;
    private int frameHeight, boxSize, screenWidth, screenHeight;
    private float boxY;
    private float orangeX, orangeY;
    private float pinkX, pinkY;
    private float blackX, blackY;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private boolean action_flg = false;
    private boolean start_flg = false;

    private CountDownTimer countDownTimer;
    private final long timeLimit = 15000; // 15秒（ミリ秒）
    private long remainingTime = timeLimit; // 一時停止したときの残り時間
    private boolean paused = false;   // 一時停止中か
    private boolean finished = false; // 勝ち負けの報告が済んだか
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startLabel = view.findViewById(R.id.startLabel);
        timerLabel = view.findViewById(R.id.timerLabel);
        box = view.findViewById(R.id.box);
        orange = view.findViewById(R.id.orange);
        pink = view.findViewById(R.id.pink);
        black = view.findViewById(R.id.black);

        // 初期位置設定（画面外）
        orange.setX(-80.0f);
        orange.setY(-80.0f);
        pink.setX(-80.0f);
        pink.setY(-80.0f);
        black.setX(-80.0f);
        black.setY(-80.0f);

        timerLabel.setText("Time : " + timeLimit / 1000);

        // boxのY座標を正しく取得するためにpost()を使用
        view.post(() -> {
            frameHeight = view.getHeight();
            screenWidth = view.getWidth();   // この画面が実際に使える幅
            screenHeight = view.getHeight(); // この画面が実際に使える高さ
            boxSize = box.getHeight();
            boxY = box.getY();
        });

        // タッチイベントを設定
        view.setOnTouchListener((v, event) -> {
            if (finished) return true;

            if (!start_flg || paused) {
                // ゲーム開始、または一時停止からの再開
                start_flg = true;
                paused = false;
                startLabel.setVisibility(View.GONE);
                startTimer(remainingTime);
                startGameLoop();
            } else {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    action_flg = true;  // タッチしたら上に移動
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    action_flg = false; // 離したら下に落ちる
                }
            }
            return true;
        });
    }

    // 画面が見えなくなりかけたとき(ホームに戻る、別アプリに切り替えるなど)
    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

    // 別の画面に切り替わって、この画面が壊れるとき
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
        stopGameLoop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    private void startGameLoop() {
        runnable = new Runnable() {
            @Override
            public void run() {
                changePos();
                if (!finished && !paused) {
                    handler.postDelayed(this, 20);
                }
            }
        };
        handler.post(runnable);
    }

    private void stopGameLoop() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void pauseGame() {
        if (!start_flg || paused || finished) return;
        paused = true;
        action_flg = false;
        stopTimer();
        stopGameLoop();
        startLabel.setText("タップで再開");
        startLabel.setVisibility(View.VISIBLE);
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
            moveResult(false);
            return;
        }
    }

    public void boxMove() {
        if (action_flg) {
            boxY -= 20; // 上に移動
        } else {
            boxY += 20; // 下に落ちる
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

    private void startTimer(long millis) {
        Log.d("startTimer", "startTimer: " + millis);
        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                timerLabel.setText("Time : " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                moveResult(true);
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void moveResult(boolean isClear) {
        if (finished) return; // 2回報告しないようにする
        finished = true;
        stopTimer();
        stopGameLoop();

        if (callback == null) return;
        callback.onGameResult(isClear);
    }
}
