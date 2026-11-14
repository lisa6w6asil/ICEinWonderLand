package com.example.iceinwonderland.ui.fifteenpuzzle;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.ui.GameResultCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PuzzleFragment extends Fragment {

    // order[位置] = その位置にあるタイル(0〜14が数字の1〜15、15が空白)
    private static final int EMPTY = 15;
    private static final int SHUFFLE_MOVES = 200;  // 混ぜるときに動かす回数
    private static final long TIME_LIMIT = 120000; // 制限時間 120秒
    private static final int IN_PLACE_TINT = Color.parseColor("#AFCEDE"); // 正しい位置のタイルの色

    private static final int[] TILE_IDS = {
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9, R.id.button_10, R.id.button_11,
            R.id.button_12, R.id.button_13, R.id.button_14, R.id.button_15
    };
    private static final int[] TILE_IMAGES = {
            R.drawable.keyboard_1_white, R.drawable.keyboard_2_white, R.drawable.keyboard_3_white,
            R.drawable.keyboard_4_white, R.drawable.keyboard_5_white, R.drawable.keyboard_6_white,
            R.drawable.keyboard_7_white, R.drawable.keyboard_8_white, R.drawable.keyboard_9_white,
            R.drawable.keyboard_10_white, R.drawable.keyboard_11_white, R.drawable.keyboard_12_white,
            R.drawable.keyboard_13_white, R.drawable.keyboard_14_white, R.drawable.keyboard_15_white
    };

    private final ImageButton[] tiles = new ImageButton[16];
    private final int[] order = new int[16];
    private final Random random = new Random();

    private View overlayView;
    private TextView startText, timerLabel, moveCountLabel;
    private CountDownTimer countDownTimer;
    private AlertDialog clearDialog;
    private long remainingTime = TIME_LIMIT;
    private int moves = 0;
    private boolean started = false;  // 「タップしてスタート」を押したか
    private boolean locked = true;    // タイルを動かせない状態か(開始前・終了後)
    private boolean finished = false; // 勝ち負けの報告が済んだか
    private GameResultCallback callback;

    public static Fragment newInstance() {
        return new PuzzleFragment();
    }

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
        return inflater.inflate(R.layout.fragment_puzzle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        overlayView = view.findViewById(R.id.overlay_view);
        startText = view.findViewById(R.id.start_text);
        timerLabel = view.findViewById(R.id.timerLabel);
        moveCountLabel = view.findViewById(R.id.moveCount);

        for (int i = 0; i < 16; i++) {
            tiles[i] = view.findViewById(TILE_IDS[i]);
            final int index = i;
            tiles[i].setOnClickListener(v -> moveTile(index));
            order[i] = i; // 最初は完成形
        }

        timerLabel.setText(String.valueOf(TIME_LIMIT / 1000));

        // 「タップしてスタート」
        overlayView.setOnClickListener(v -> {
            startText.setVisibility(View.GONE);
            overlayView.setVisibility(View.GONE);
            started = true;
            shuffleTiles();
            startTimer(remainingTime);
        });

        // RESET:並べ直す(タイマーはそのまま)
        view.findViewById(R.id.resetButton).setOnClickListener(v -> {
            if (started && !locked) shuffleTiles();
        });

        updateGrid();
    }

    // 画面が見えたとき:始まっていれば残り時間から再開
    @Override
    public void onResume() {
        super.onResume();
        if (started && !finished) startTimer(remainingTime);
    }

    // 画面が見えなくなりかけたとき:タイマーを止める
    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (clearDialog != null) {
            clearDialog.dismiss();
            clearDialog = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    // 空白の隣のタイルなら動かす
    private void moveTile(int index) {
        if (locked) return;
        int empty = findEmptyTile();
        if (!isAdjacent(index, empty)) return;
        swap(index, empty);
        moves++;
        updateGrid();
        if (checkGoal()) onCleared();
    }

    private int findEmptyTile() {
        for (int i = 0; i < 16; i++) if (order[i] == EMPTY) return i;
        return -1;
    }

    // 上下左右に隣り合っているか
    private boolean isAdjacent(int a, int b) {
        int r1 = a / 4, c1 = a % 4;
        int r2 = b / 4, c2 = b % 4;
        return (Math.abs(r1 - r2) == 1 && c1 == c2) || (Math.abs(c1 - c2) == 1 && r1 == r2);
    }

    private void swap(int a, int b) {
        int t = order[a];
        order[a] = order[b];
        order[b] = t;
    }

    private boolean checkGoal() {
        for (int i = 0; i < 16; i++) if (order[i] != i) return false;
        return true;
    }

    // 完成形から「本当に動かせる手」をランダムにくり返して混ぜる。
    // 完全にランダムに並べると、解けない並びが半分混ざるため。
    // この方法なら、どんな並びになっても必ず解ける。
    private void shuffleTiles() {
        do {
            for (int i = 0; i < 16; i++) order[i] = i;
            int empty = EMPTY;
            int previous = -1;
            for (int n = 0; n < SHUFFLE_MOVES; n++) {
                List<Integer> neighbors = new ArrayList<>();
                for (int p = 0; p < 16; p++) {
                    if (p != previous && isAdjacent(p, empty)) neighbors.add(p);
                }
                int pick = neighbors.get(random.nextInt(neighbors.size()));
                swap(pick, empty);
                previous = empty;
                empty = pick;
            }
        } while (checkGoal()); // 偶然そろってしまったらやり直し

        moves = 0;
        locked = false;
        updateGrid();
    }

    // 今の order に合わせて見た目を更新
    private void updateGrid() {
        for (int i = 0; i < 16; i++) {
            int n = order[i];
            if (n == EMPTY) {
                tiles[i].setVisibility(View.INVISIBLE);
            } else {
                tiles[i].setVisibility(View.VISIBLE);
                tiles[i].setImageResource(TILE_IMAGES[n]);
                // 正しい位置にあるタイルは色を付けて目立たせる(始まってから)
                if (started && n == i) {
                    tiles[i].setColorFilter(IN_PLACE_TINT, PorterDuff.Mode.MULTIPLY);
                } else {
                    tiles[i].clearColorFilter();
                }
            }
        }
        moveCountLabel.setText(String.format(Locale.getDefault(), "動かした回数 %d", moves));
    }

    // そろったとき:タイマーを止め、タイルを固定してからダイアログ
    private void onCleared() {
        if (finished) return;
        finished = true;
        locked = true;
        stopTimer();
        clearDialog = new AlertDialog.Builder(requireContext())
                .setTitle("ゲームクリア！")
                .setMessage(moves + "回でそろいました。おめでとうございます！")
                .setPositiveButton("OK", (dialog, which) -> report(true))
                .setCancelable(false)
                .show();
    }

    // 時間切れのとき
    private void onTimeUp() {
        if (finished) return;
        finished = true;
        locked = true;
        report(false);
    }

    private void report(boolean isClear) {
        if (callback == null) return;
        callback.onGameResult(isClear);
    }

    private void startTimer(long millis) {
        stopTimer();
        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                timerLabel.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                remainingTime = 0;
                timerLabel.setText("0");
                onTimeUp();
            }
        };
        countDownTimer.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}