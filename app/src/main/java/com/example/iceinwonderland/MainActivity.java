package com.example.iceinwonderland;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.ui.BackToStageSelectCallback;
import com.example.iceinwonderland.ui.GameResultCallback;
import com.example.iceinwonderland.ui.GameRetryCallback;
import com.example.iceinwonderland.ui.StageInfo;
import com.example.iceinwonderland.ui.block.BlockFragment;
import com.example.iceinwonderland.ui.catchball.CatchBallFragment;
import com.example.iceinwonderland.ui.result.GameClearFragment;
import com.example.iceinwonderland.ui.result.GameOverFragment;
import com.example.iceinwonderland.ui.stopwatch.StopwatchFragment;
import com.example.iceinwonderland.ui.quiz.QuizFragment;
import com.example.iceinwonderland.ui.fifteenpuzzle.PuzzleFragment;
import com.example.iceinwonderland.ui.stageselect.StageSelectFragment;
import com.example.iceinwonderland.ui.title.TitleFragment;
import com.example.iceinwonderland.ui.video.VideoFragment;


public class MainActivity extends AppCompatActivity
        implements StageSelectFragment.StageSelectListener, GameResultCallback, GameRetryCallback,
        BackToStageSelectCallback, TitleFragment.TitleListener
{
    private ICEinWonderLandApplication application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        //アプリケーションクラスを取得
        application = (ICEinWonderLandApplication)getApplication();

        // 初回起動のときだけタイトル画面を入れる(画面回転などで作り直されたときは、前の画面が自動で戻るため)
        if (savedInstanceState == null) {
            showTitleFragment();
        }
    }

    private void showTitleFragment(){
        changeFragment(TitleFragment.newInstance());
    }

    private void showVideoFragment(){
        Fragment fragment = VideoFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,fragment)
                .commitNow();
    }

    private void showStageSelectFragment(){
        Fragment fragment = StageSelectFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,fragment)
                .commitNow();
    }

    //タイトル画面のスタートが押された時呼ばれるところ
    @Override
    public void onStartPressed() {
        showVideoFragment();
    }

    //ステージが押された時呼ばれるところ
    @Override
    public void onStageSelect(StageInfo stageInfo) {
        showSelectStage(stageInfo);
    }
    private void showSelectStage(StageInfo stageInfo){
        Fragment fragment = null;
        //選択されたステージ情報をアプリケーションクラスに保存
        application.setCurrentStageInfo(stageInfo);
        moveToStage(stageInfo);
    }

    private void moveToStage(StageInfo stageInfo){
        Fragment fragment = null;
        switch(stageInfo){
            case Sea:
                fragment = CatchBallFragment.newInstance();
                break;
            case Snowmountain:
                fragment = BlockFragment.newInstance();
                break;
            case House:
                fragment = PuzzleFragment.newInstance();
                break;
            case Tree:
                fragment = QuizFragment.newInstance();
                break;
            case Castle:
                fragment = StopwatchFragment.newInstance();
                break;
        }
        changeFragment(fragment);
    }

    private void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,fragment)
                .commitNow();
    }

    @Override
    public void onGameResult(boolean isClear) {
        Fragment fragment;
        if(isClear){
            fragment = GameClearFragment.newInstance();
        }else {
            fragment = GameOverFragment.newInstance();
        }
        changeFragment(fragment);
    }

    @Override
    public void onGameRetry() {
        //選択中のステージを再表示する
        StageInfo selectStageInfo = application.getCurrentStageInfo();
        moveToStage(selectStageInfo);
    }

    //クリア画面のNEXTが押された時呼ばれるところ
    @Override
    public void onBackToStageSelect() {
        showStageSelectFragment();
    }
}
