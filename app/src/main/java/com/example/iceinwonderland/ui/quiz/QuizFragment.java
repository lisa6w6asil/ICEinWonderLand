package com.example.iceinwonderland.ui.quiz;

import android.content.Context;
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
import com.example.iceinwonderland.data.QuizData;
import com.example.iceinwonderland.ui.GameResultCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizFragment extends Fragment {

    private String quizAnswer;
    private TextView quizText, timerLabel;
    private Button option1;
    private Button option2;
    private Button option3;
    private Button option4;
    private ImageView resetButton;
    private CountDownTimer countDownTimer;
    private final long timeLimit = 30000; // 30秒（ミリ秒）
    private long remainingTime = timeLimit; // 一時停止したときの残り時間
    private boolean finished = false;      // 勝ち負けの報告が済んだか
    private GameResultCallback callback;

    public static Fragment newInstance(){
        return new QuizFragment();
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
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quizText = view.findViewById(R.id.question_text);
        timerLabel = view.findViewById(R.id.timerLabel);
        resetButton = view.findViewById(R.id.reset);

        timerLabel.setText(String.valueOf(timeLimit / 1000));

        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);

        createQuiz();

        // 4つの選択肢は、押されたボタンの文字で答え合わせする
        View.OnClickListener answerListener = v -> checkAnswer(((Button) v).getText().toString());
        option1.setOnClickListener(answerListener);
        option2.setOnClickListener(answerListener);
        option3.setOnClickListener(answerListener);
        option4.setOnClickListener(answerListener);

        resetButton.setOnClickListener(v -> createQuiz());
    }

    // 画面が見えたとき:残り時間からタイマーを動かす(初回は30秒から)
    @Override
    public void onResume() {
        super.onResume();
        if (!finished) {
            startTimer(remainingTime);
        }
    }

    // 画面が見えなくなりかけたとき:タイマーを止める(ホームに行く、別の画面に切り替わる、など)
    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    // 答えを判定して、受付に1回だけ報告する
    private void checkAnswer(String selectAnswer) {
        if (finished) return;
        finished = true;
        stopTimer();

        if (callback == null) return;
        callback.onGameResult(selectAnswer.equals(quizAnswer));
    }

    private void createQuiz(){
        int quizIndex = new Random().nextInt(QuizData.QUESTIONS.length);
        String quizStr = QuizData.QUESTIONS[quizIndex][0];
        quizText.setText(quizStr);

        //正解を取得
        quizAnswer = QuizData.QUESTIONS[quizIndex][1];

        //選択肢を取得
        List<String> answerList = new ArrayList<>(Arrays.asList(QuizData.QUESTIONS[quizIndex]));
        answerList.remove(0);

        //選択肢をシャッフル
        Collections.shuffle(answerList);

        option1.setText(answerList.get(0));
        option2.setText(answerList.get(1));
        option3.setText(answerList.get(2));
        option4.setText(answerList.get(3));
    }

    //タイマー開始
    private void startTimer(long millis) {
        stopTimer(); // 念のため、前のタイマーが残っていたら止めてから作る
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
                checkAnswer("isTimeUp");
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
