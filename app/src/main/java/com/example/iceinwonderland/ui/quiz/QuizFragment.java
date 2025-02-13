package com.example.iceinwonderland.ui.quiz;

import static android.text.TextUtils.replace;

import android.os.Bundle;
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
import com.example.iceinwonderland.ui.StageInfo;
import com.example.iceinwonderland.ui.result.GameclearFragment;
import com.example.iceinwonderland.ui.result.GameoverFragment;
import com.example.iceinwonderland.ui.stageselect.StageSelectFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizFragment extends Fragment {

    private String quizAnswer;
    private TextView quizText;
    private Button option1;
    private Button option2;
    private Button option3;
    private Button option4;
    private ImageView resetButton;



    public  static  Fragment newInstance(){
        return new QuizFragment();
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
        resetButton = view.findViewById(R.id.reset);


        // ボタンを設定
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);

        // クイズを作成
        createQuiz();

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
        if (selectAnswer.equals(quizAnswer)) {
            Toast.makeText(getContext(), "正解！", Toast.LENGTH_SHORT).show();
            //クリア画面へ上澄みを別の上澄変える（クリアの上澄み）
            //todo:ok:クリア画面のフラグメント（上澄）それを表示、ゲームオーバーも同じ
            //ステージセレクト画面を表示
            Fragment clearfragment = GameclearFragment.newInstance();
            if (clearfragment != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_quiz, clearfragment)
                        .commit();
            }
        } else {
                Toast.makeText(getContext(), "不正解！", Toast.LENGTH_SHORT).show();
                //ゲームオーバー画面へ
                Fragment overfragment = GameoverFragment.newInstance();
                if (overfragment != null) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_quiz, overfragment)
                            .commit();
                }
        }
    }

    private void createQuiz(){
        //todo:クイズをリセットする。同じ問題は嫌かも...別にいいかなぁ
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

}

