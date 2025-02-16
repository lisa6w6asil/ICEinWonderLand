package com.example.iceinwonderland.ui.fifteenpuzzle;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;

import java.util.ArrayList;
import java.util.Collections;

public class PuzzleFragment extends Fragment {

    private GridLayout gridLayout;
    private ImageButton[] tiles = new ImageButton[16]; // ImageButton に変更
    private ArrayList<Integer> tileOrder = new ArrayList<>();
    private TextView startText;
    private boolean gameStarted = false;

    public static Fragment newInstance() {
        return new PuzzleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_puzzle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridLayout = view.findViewById(R.id.gridLayout);
        startText = view.findViewById(R.id.start_text);

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
        tileOrder.add(15); // 空のタイル

        // 「タップしてスタート」用リスナー
        gridLayout.setOnClickListener(v -> {
            if (!gameStarted) {
                gameStarted = true;
                startText.setVisibility(View.GONE);
                shuffleTiles();
            }
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
            case 0: return R.drawable.keyboard_1_white;
            case 1: return R.drawable.keyboard_2_white;
            case 2: return R.drawable.keyboard_3_white;
            case 3: return R.drawable.keyboard_4_white;
            case 4: return R.drawable.keyboard_5_white;
            case 5: return R.drawable.keyboard_6_white;
            case 6: return R.drawable.keyboard_7_white;
            case 7: return R.drawable.keyboard_8_white;
            case 8: return R.drawable.keyboard_9_white;
            case 9: return R.drawable.keyboard_10_white;
            case 10: return R.drawable.keyboard_11_white;
            case 11: return R.drawable.keyboard_12_white;
            case 12: return R.drawable.keyboard_13_white;
            case 13: return R.drawable.keyboard_14_white;
            case 14: return R.drawable.keyboard_15_white;
            default: return R.drawable.game2rectangle;
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
                .setPositiveButton("OK", (dialog, which) -> shuffleTiles())
                .setCancelable(false)
                .show();
    }

    // タイルをシャッフル
    private void shuffleTiles() {
        do {
            Collections.shuffle(tileOrder);
        } while (!isSolvable());
        updateGrid();
    }

    // クリア可能なシャッフルか判定
    private boolean isSolvable() {
        int inversions = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = i + 1; j < 15; j++) {
                if (tileOrder.get(i) > tileOrder.get(j)) inversions++;
            }
        }
        return inversions % 2 == 0;
    }
}













