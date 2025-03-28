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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.data.QuizData;
import com.example.iceinwonderland.ui.GameResultCallback;
import com.example.iceinwonderland.ui.result.GameClearFragment;
import com.example.iceinwonderland.ui.result.GameOverFragment;

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
    private GameResultCallback callback;

    public  static  Fragment newInstance(){
        return new QuizFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            callback = (GameResultCallback) context;
        }catch(ClassCastException e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quizText = view.findViewById(R.id.question_text);
        timerLabel = view.findViewById(R.id.timerLabel);
        resetButton = view.findViewById(R.id.reset);

        timerLabel.setText("30");


        // ボタンを設定
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);

        // クイズを作成
        createQuiz();
        startTimer();

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = ((Button)v).getText().toString();
                checkAnswer(str);  // 1番の選択肢
            }
        });


        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = ((Button)v).getText().toString();
                checkAnswer(str);  // 2番の選択肢
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = ((Button)v).getText().toString();
                checkAnswer(str);  // 3番の選択肿
            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = ((Button)v).getText().toString();
                checkAnswer(str);   // 4番の選択肢
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQuiz();
            }
        });
    }

    // 答えを判定するメソッド
    private void checkAnswer(String selectAnswer) {
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

        // 選択肢ボタンのクリックリスナー設定
        option1.setText(answerList.get(0));
        option2.setText(answerList.get(1));
        option3.setText(answerList.get(2));
        option4.setText(answerList.get(3));
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
                checkAnswer("isTimeUp");
            }
        };
        countDownTimer.start();
    }

}

