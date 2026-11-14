package com.example.iceinwonderland.ui.stopwatch;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.ui.GameResultCallback;

import java.util.Locale;
import java.util.Random;

public class StopwatchFragment extends Fragment {
    public static Fragment newInstance() {
        return new StopwatchFragment();
    }

    // 目標タイムの候補(3秒、5秒、7秒、10秒)
    private static final int[] TARGET_TIMES = {3000, 5000, 7000, 10000};
    // 目標との差がこれ未満ならクリア(0.1秒)
    private static final long CLEAR_RANGE_MILLIS = 100;

    private TextView timerText;
    private TextView targetTimeText;
    private TextView statusMessage;
    private ImageView startButton, stopButton, nextButton;
    private CountDownTimer countUpTimer;
    private long timeElapsedMillis; // 経過時間
    private long targetTimeMillis;  // 目標時間
    private long startTimeMillis;   // 計測を始めた時刻
    private boolean isGameRunning = false;
    private GameResultCallback callback; // 受付(MainActivity)への電話番号

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GameResultCallback) {
            callback = (GameResultCallback) context;
        } else {
            throw new ClassCastException(context + " must implement GameResultCallback");
        }
    }

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

        startButton.setOnClickListener(v -> startGame());
        stopButton.setOnClickListener(v -> stopGame());

        setRandomTargetTime();
    }

    // 画面が壊れるとき、計測中のタイマーを止める
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countUpTimer != null) {
            countUpTimer.cancel();
            countUpTimer = null;
        }
        isGameRunning = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    // 目標タイムを候補の中からランダムに決める
    private void setRandomTargetTime() {
        targetTimeMillis = TARGET_TIMES[new Random().nextInt(TARGET_TIMES.length)];
        targetTimeText.setText(String.format(Locale.getDefault(), "目標タイム: %.3f秒", targetTimeMillis / 1000.0));
    }

    private void startGame() {
        isGameRunning = true;
        statusMessage.setText("");

        // 計測中はスタートを押せない、ストップを押せる
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        stopButton.setVisibility(View.VISIBLE);

        timeElapsedMillis = 0;
        startTimeMillis = SystemClock.elapsedRealtime();

        // 表示を10ミリ秒ごとに更新する(時間そのものは時計の差で測る)
        countUpTimer = new CountDownTimer(Long.MAX_VALUE, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeElapsedMillis = SystemClock.elapsedRealtime() - startTimeMillis;
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
        if (!isGameRunning) return;

        // 押した瞬間の時間で判定する
        timeElapsedMillis = SystemClock.elapsedRealtime() - startTimeMillis;
        updateTimerDisplay();
        countUpTimer.cancel();
        isGameRunning = false;
        stopButton.setEnabled(false);

        showResult();
    }

    private void updateTimerDisplay() {
        int seconds = (int) (timeElapsedMillis / 1000);
        int milliseconds = (int) (timeElapsedMillis % 1000);
        timerText.setText(String.format(Locale.getDefault(), "%d.%03d", seconds, milliseconds));
    }

    // 結果を判定して表示し、NEXTボタンを出す
    private void showResult() {
        boolean isClear = Math.abs(targetTimeMillis - timeElapsedMillis) < CLEAR_RANGE_MILLIS;

        if (isClear) {
            statusMessage.setText("クリア！");
            statusMessage.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
        } else {
            statusMessage.setText("残念！");
            statusMessage.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
        }

        nextButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);

        // NEXTを押したら、自分で切り替えずに受付へ結果を報告する
        nextButton.setOnClickListener(v -> {
            if (callback != null) {
                callback.onGameResult(isClear);
            }
        });
    }
}
