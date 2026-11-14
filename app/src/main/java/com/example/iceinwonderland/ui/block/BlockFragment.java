package com.example.iceinwonderland.ui.block;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.ui.GameResultCallback;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 雪山:氷すべりパズルの画面。ルールは IceBoard にあり、ここは表示と操作とタイマーだけ。
 */
public class BlockFragment extends Fragment {

    // 最低何回蹴らないと解けない盤面にするか
    private static final int MIN_KICKS = 3;
    // 制限時間(B で最初からにしても戻さない)
    private static final long TIME_LIMIT = 90000;
    // 1マス滑る時間
    private static final long SLIDE_MS = 70;
    // 向きごとの絵の回転(上 右 下 左。下向きが0度)
    private static final float[] ROT = {180f, 270f, 0f, 90f};
    // ゴールのマスの色(仮)
    private static final int GOAL_COLOR = 0xFFF2A7C3;

    private GameResultCallback callback;

    // 盤面づくりは重いので別スレッドで行う
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private IceBoard board;

    private FrameLayout boardView;
    private ImageView playerView, blockView;
    private TextView timeText, kicksText, messageText;
    private float cellPx;

    private CountDownTimer timer;
    private long remainingTime = TIME_LIMIT;
    // 滑っている最中は操作を受け付けない
    private boolean busy = false;
    // 結果の報告は1回だけ
    private boolean finished = false;

    private final Runnable hideMessage = () -> {
        if (messageText != null) messageText.setVisibility(View.GONE);
    };

    public static Fragment newInstance() {
        return new BlockFragment();
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
        return inflater.inflate(R.layout.fragment_block, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cellPx = 50 * getResources().getDisplayMetrics().density;

        boardView = view.findViewById(R.id.board);
        timeText = view.findViewById(R.id.timeText);
        kicksText = view.findViewById(R.id.kicksText);
        messageText = view.findViewById(R.id.message);

        view.findViewById(R.id.up_button).setOnClickListener(v -> onDirection(0));
        view.findViewById(R.id.right_button).setOnClickListener(v -> onDirection(1));
        view.findViewById(R.id.down_button).setOnClickListener(v -> onDirection(2));
        view.findViewById(R.id.left_button).setOnClickListener(v -> onDirection(3));
        view.findViewById(R.id.a_button).setOnClickListener(v -> onKick());
        view.findViewById(R.id.b_button).setOnClickListener(v -> onRestart());

        updateTime();

        if (board == null) {
            messageText.setText("盤面を作っています…");
            messageText.setVisibility(View.VISIBLE);
            executor.execute(() -> {
                IceBoard made = IceBoard.make(MIN_KICKS, new Random());
                mainHandler.post(() -> onBoardReady(made));
            });
        } else {
            drawBoard();
        }
    }

    private void onBoardReady(IceBoard made) {
        board = made;
        if (boardView == null) return; // 画面がもうない
        messageText.setVisibility(View.GONE);
        drawBoard();
        if (isResumed()) startTimer();
    }

    // ---------- 表示 ----------

    private void drawBoard() {
        boardView.removeAllViews();
        int size = Math.round(cellPx);
        for (int y = 0; y < IceBoard.H; y++) {
            for (int x = 0; x < IceBoard.W; x++) {
                ImageView cell = new ImageView(requireContext());
                cell.setLayoutParams(new FrameLayout.LayoutParams(size, size));
                cell.setTranslationX(x * cellPx);
                cell.setTranslationY(y * cellPx);
                switch (board.cell(x, y)) {
                    case IceBoard.WALL: cell.setBackgroundResource(R.drawable.wall_bg); break;
                    case IceBoard.SNOW: cell.setBackgroundResource(R.drawable.yuki_bg); break;
                    case IceBoard.GOAL: cell.setBackgroundColor(GOAL_COLOR); break;
                    default: cell.setBackgroundResource(R.drawable.ice_bg); break;
                }
                boardView.addView(cell);
            }
        }

        blockView = new ImageView(requireContext());
        blockView.setLayoutParams(new FrameLayout.LayoutParams(size, size));
        blockView.setImageResource(R.drawable.block_koori);
        boardView.addView(blockView);

        playerView = new ImageView(requireContext());
        playerView.setLayoutParams(new FrameLayout.LayoutParams(size, size));
        playerView.setImageResource(R.drawable.player);
        boardView.addView(playerView);

        placeNow();
    }

    /** アニメーションなしで今の位置に置く */
    private void placeNow() {
        playerView.animate().cancel();
        blockView.animate().cancel();
        playerView.setTranslationX(board.playerX() * cellPx);
        playerView.setTranslationY(board.playerY() * cellPx);
        playerView.setRotation(ROT[board.direction()]);
        blockView.setTranslationX(board.blockX() * cellPx);
        blockView.setTranslationY(board.blockY() * cellPx);
        updateKicks();
    }

    private void updateKicks() {
        if (kicksText == null || board == null) return;
        kicksText.setText("蹴った回数 " + board.kicks() + " / 最短 " + board.best + "回");
    }

    private void updateTime() {
        if (timeText == null) return;
        timeText.setText("Time : " + (remainingTime + 999) / 1000);
    }

    private void showMessage(String text) {
        mainHandler.removeCallbacks(hideMessage);
        messageText.setText(text);
        messageText.setVisibility(View.VISIBLE);
        mainHandler.postDelayed(hideMessage, 1000);
    }

    // ---------- 操作 ----------

    private boolean canOperate() {
        return board != null && playerView != null && !busy && !finished;
    }

    /** 十字キー:その向きを向いて、止まるところまで滑る */
    private void onDirection(int d) {
        if (!canOperate()) return;
        int dist = board.move(d);
        playerView.setRotation(ROT[d]);
        if (dist == 0) return;
        busy = true;
        playerView.animate()
                .translationX(board.playerX() * cellPx)
                .translationY(board.playerY() * cellPx)
                .setDuration(dist * SLIDE_MS)
                .withEndAction(() -> busy = false)
                .start();
    }

    /** A ボタン:目の前の氷ブロックを蹴る */
    private void onKick() {
        if (!canOperate()) return;
        int dist = board.kick();
        if (dist < 0) return;               // 目の前にブロックがない
        if (dist == 0) {
            showMessage("この向きには滑らない");
            return;
        }
        updateKicks();
        busy = true;
        blockView.animate()
                .translationX(board.blockX() * cellPx)
                .translationY(board.blockY() * cellPx)
                .setDuration(dist * SLIDE_MS)
                .withEndAction(() -> {
                    busy = false;
                    if (board.isCleared()) report(true);
                })
                .start();
    }

    /** B ボタン:同じ盤面を最初から(タイマーはそのまま) */
    private void onRestart() {
        if (!canOperate()) return;
        board.reset();
        placeNow();
    }

    // ---------- タイマーと結果 ----------

    private void startTimer() {
        if (finished || board == null || timer != null) return;
        timer = new CountDownTimer(remainingTime, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                updateTime();
            }

            @Override
            public void onFinish() {
                remainingTime = 0;
                updateTime();
                report(false);
            }
        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void report(boolean win) {
        if (finished) return;
        finished = true;
        stopTimer();
        if (callback != null) callback.onGameResult(win);
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onDestroyView() {
        stopTimer();
        mainHandler.removeCallbacks(hideMessage);
        if (playerView != null) playerView.animate().cancel();
        if (blockView != null) blockView.animate().cancel();
        busy = false;
        boardView = null;
        playerView = null;
        blockView = null;
        timeText = null;
        kicksText = null;
        messageText = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        executor.shutdownNow();
        mainHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}