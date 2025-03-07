package com.example.iceinwonderland.ui.fifteenpuzzle;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.ui.GameResultCallback;

import java.util.ArrayList;
import java.util.Collections;

public class PuzzleFragment extends Fragment {

    private View overlayView;
    private ImageButton[] tiles = new ImageButton[16]; // ImageButton に変更
    private ArrayList<Integer> tileOrder = new ArrayList<>();
    private TextView startText, timerLabel;
    private CountDownTimer countDownTimer;
    private final long timeLimit = 120000; // 120秒（ミリ秒）
    private GameResultCallback callback;


    public static Fragment newInstance() {
        return new PuzzleFragment();
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
        return inflater.inflate(R.layout.fragment_puzzle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        overlayView = view.findViewById(R.id.overlay_view);
        startText = view.findViewById(R.id.start_text);
        timerLabel = view.findViewById(R.id.timerLabel);

        timerLabel.setText("120");

        // ImageButton の初期化
        for (int i = 0; i < 16; i++) {
            String tileID = "button_" + i;
            int resID = getResources().getIdentifier(tileID, "id", requireActivity().getPackageName());
            tiles[i] = view.findViewById(resID);
        }

        // 初期配置リストを作成
        for (int i = 0; i < 16; i++) {
            tileOrder.add(i);
        }

        // 「タップしてスタート」用リスナー
        overlayView.setOnClickListener(v -> {
            startText.setVisibility(View.GONE);
            overlayView.setVisibility(View.GONE);
            shuffleTiles();
            startTimer();
        });

        // タイルクリックリスナー
        for (int i = 0; i < 16; i++) {
            final int index = i;
            tiles[i].setOnClickListener(v -> moveTile(index));
        }

        updateGrid(); // 初回描画
    }

    // タイルを動かす処理
    private void moveTile(int index) {
        int emptyIndex = findEmptyTile();

        if (isMovable(index, emptyIndex)) {
            Collections.swap(tileOrder, index, emptyIndex);
            updateGrid();

            if (checkGoal()) {
                showGameClearDialog();
            }
        }
    }

    // 空のタイルを探す
    private int findEmptyTile() {
        return tileOrder.indexOf(15);
    }

    // 動かせるかチェック
    private boolean isMovable(int index, int emptyIndex) {
        int row1 = index / 4, col1 = index % 4;
        int row2 = emptyIndex / 4, col2 = emptyIndex % 4;
        return (row1 == row2 && Math.abs(col1 - col2) == 1) ||
                (col1 == col2 && Math.abs(row1 - row2) == 1);
    }

    // UI更新
    private void updateGrid() {
        for (int i = 0; i < 16; i++) {
            int num = tileOrder.get(i);

            if (num == 15) {

                tiles[i].setVisibility(View.INVISIBLE); // 空のタイルを非表示
            } else {
                tiles[i].setVisibility(View.VISIBLE);
                tiles[i].setImageResource(getTileImage(num)); // setImageResource() を使う
            }
        }
    }

    // タイルの画像を取得
    private int getTileImage(int num) {
        switch (num) {
            case 0:
                return R.drawable.keyboard_1_white;
            case 1:
                return R.drawable.keyboard_2_white;
            case 2:
                return R.drawable.keyboard_3_white;
            case 3:
                return R.drawable.keyboard_4_white;
            case 4:
                return R.drawable.keyboard_5_white;
            case 5:
                return R.drawable.keyboard_6_white;
            case 6:
                return R.drawable.keyboard_7_white;
            case 7:
                return R.drawable.keyboard_8_white;
            case 8:
                return R.drawable.keyboard_9_white;
            case 9:
                return R.drawable.keyboard_10_white;
            case 10:
                return R.drawable.keyboard_11_white;
            case 11:
                return R.drawable.keyboard_12_white;
            case 12:
                return R.drawable.keyboard_13_white;
            case 13:
                return R.drawable.keyboard_14_white;
            case 14:
                return R.drawable.keyboard_15_white;
            default:
                return R.drawable.game2rectangle;
        }
    }

    // ゲームクリア判定
    private boolean checkGoal() {
        for (int i = 0; i < 16; i++) {
            if (tileOrder.get(i) != i) return false;
        }
        return tileOrder.get(15) == 15;
    }

    // ゲームクリア時のダイアログ
    private void showGameClearDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("ゲームクリア！")
                .setMessage("おめでとうございます！")
                .setPositiveButton("OK", (dialog, which) -> moveResult(true))
                .setCancelable(false)
                .show();
    }

    private void moveResult(boolean isClear) {
        if (callback == null) return;
        callback.onGameResult(isClear);
    }

    // タイルをシャッフル
    private void shuffleTiles() {
        do {
            Collections.shuffle(tileOrder);
        } while (!isSolvable());
        //グリッド表示の更新
        updateGrid();
    }

    // クリア可能なシャッフルか判定
    // 15パズルクリアの可能性は空白マスを含む逆順数び奇数か偶数かで判定
    //@return クリア可能ならtrue
    private boolean isSolvable() {
        int inversions = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = i + 1; j < 15; j++) {
                if (tileOrder.get(i) > tileOrder.get(j)) inversions++;
            }
        }
        return inversions % 2 == 0;
    }

    //タイマー開始
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLimit, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerLabel.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                timerLabel.setText("0");
                moveResult(false);
            }
        };
        countDownTimer.start();
    }
}
















