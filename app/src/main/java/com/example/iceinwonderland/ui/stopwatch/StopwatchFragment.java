package com.example.iceinwonderland.ui.stopwatch;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.ui.result.GameclearFragment;
import com.example.iceinwonderland.ui.result.GameoverFragment;

import java.util.Locale;
import java.util.Random;

public class StopwatchFragment extends Fragment {
    public  static  Fragment newInstance() {
        return new StopwatchFragment();
    }

    private TextView timerText;
    private TextView targetTimeText;
    private TextView statusMessage;
    private ImageView startButton, stopButton, nextButton;
    private CountDownTimer countUpTimer;
    private long timeElapsedMillis; // 経過時間
    private long targetTimeMillis; // 目標時間
    private boolean isGameRunning = false;
    private boolean isFirstStart = true; // 最初のゲーム開始か判定するフラグ


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stopwatch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // UI要素の初期化
        timerText = view.findViewById(R.id.timerText);
        targetTimeText = view.findViewById(R.id.targetTimeText);
        statusMessage = view.findViewById(R.id.statusMessage);
        startButton = view.findViewById(R.id.startbutton);
        stopButton = view.findViewById(R.id.stopbutton);
        nextButton = view.findViewById(R.id.nextbutton);

        // NEXTボタンは最初は非表示
        nextButton.setVisibility(View.GONE);

        // ゲーム開始ボタンのクリックリスナー
        startButton.setOnClickListener(v -> startGame());

        // ストップボタンのクリックリスナー
        stopButton.setOnClickListener(v -> stopGame());

        // 最初の目標タイム設定（1回目だけ設定）
        setInitialTargetTime();
    }

    // 最初の目標タイム設定
    private void setInitialTargetTime() {
        Random random = new Random();
        int[] targetTimes = {3000};
        // 目標タイム一旦3秒 (3秒、5秒、7秒、10秒)int[] targetTimes = {3000, 5000, 7000, 10000};
        targetTimeMillis = targetTimes[random.nextInt(targetTimes.length)];
        targetTimeText.setText(String.format("目標タイム: %.3f秒", targetTimeMillis / 1000.0));
    }

    // 新しい目標タイムを設定するメソッド（2回目以降に呼ばれる）
    private void setRandomTargetTime() {
        Random random = new Random();
        int[] targetTimes = {3000};
        // 目標タイム一旦3秒 (3秒、5秒、7秒、10秒) int[] targetTimes = {3000, 5000, 7000, 10000};
        targetTimeMillis = targetTimes[random.nextInt(targetTimes.length)];
        targetTimeText.setText(String.format("目標タイム: %.3f秒", targetTimeMillis / 1000.0));
    }

    private void startGame() {
        // 最初のゲーム開始時には目標タイムは変更しない
        if (!isFirstStart) {
            setRandomTargetTime(); // 2回目以降に目標タイムを再設定
        }
        isFirstStart = false; // 2回目以降は目標タイムを変更する

        // ゲーム開始時
        isGameRunning = true;
        statusMessage.setText(""); // クリアメッセージを空にする

        // スタートボタンを無効にする（ゲーム開始中は押せない）
        startButton.setEnabled(false);

        // ストップボタンを有効にする
        stopButton.setEnabled(true);
        stopButton.setVisibility(View.VISIBLE);

        // タイマー初期化
        timeElapsedMillis = 0;

        // カウントアップタイマー開始
        countUpTimer = new CountDownTimer(Long.MAX_VALUE, 10) { // 無限にカウントアップ
            @Override
            public void onTick(long millisUntilFinished) {
                timeElapsedMillis += 10; // 10ミリ秒ずつ加算
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                // 無限タイマーなので、ここは呼ばれない
            }
        };

        countUpTimer.start();
    }

    private void stopGame() {
        if (isGameRunning) {
            countUpTimer.cancel();  // タイマーをキャンセル
            isGameRunning = false;
            startButton.setEnabled(true);  // ゲーム終了後、再度ゲーム開始ボタンを有効にする
            stopButton.setEnabled(false);  // ストップボタンを無効にする

            // 最後の結果を表示
            checkGameResult();
        }
    }

    // タイマーの表示を更新
    private void updateTimerDisplay() {
        int seconds = (int) (timeElapsedMillis / 1000);
        int milliseconds = (int) (timeElapsedMillis % 1000);
        String timeFormatted = String.format(Locale.getDefault(), "%d.%03d", seconds, milliseconds);
        timerText.setText(timeFormatted);
    }

    // ゲームの結果をチェックして表示
    private boolean checkGameResult() {
        // 結果の判定
        long timeDifference = Math.abs(targetTimeMillis - timeElapsedMillis);

        if (timeDifference < 100) {
            statusMessage.setText("クリア！");
            statusMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));// 緑色で表示
            nextButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.GONE);
            showNextButton(true);
        } else {
            statusMessage.setText("残念！");
            statusMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));// 赤色で表示
            nextButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.GONE);
            showNextButton(false);
        }
        return false;
    }

    // Nextボタンを表示して遷移処理をセットする
    private void showNextButton(boolean isGameClear) {
        nextButton.setOnClickListener(v -> {
            Fragment nextFragment;
            if (isGameClear) {
                nextFragment = new GameclearFragment();
            } else {
                nextFragment = new GameoverFragment();
            }
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, nextFragment)
                    .commit();
        });
    }
}
