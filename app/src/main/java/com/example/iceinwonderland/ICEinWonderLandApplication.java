package com.example.iceinwonderland;

import android.app.Application;

import com.example.iceinwonderland.ui.StageInfo;

public class ICEinWonderLandApplication extends Application {

    //現在選択中のステージ
        private StageInfo currentStageInfo;

        public StageInfo getCurrentStageInfo(){
            return currentStageInfo;
        }

        public void setCurrentStageInfo(StageInfo stageInfo){
            this.currentStageInfo = stageInfo;
        }
}
