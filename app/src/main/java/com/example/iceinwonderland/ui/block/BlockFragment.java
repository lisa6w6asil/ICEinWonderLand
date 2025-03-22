package com.example.iceinwonderland.ui.block;

import android.content.Context;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.service.controls.Control;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.data.AreaInfo;
import com.example.iceinwonderland.data.CharacterInfo;
import com.example.iceinwonderland.data.IceInfo;
import com.example.iceinwonderland.ui.GameResultCallback;

import java.util.ArrayList;
import java.util.List;

public class BlockFragment extends Fragment {

    // エリアの列数
    private static final int MAX_COLUMN = 4;

    // 背景のリソースID
    private static final int[] BG_RES_IDS = {
        R.drawable.wall_bg, R.drawable.yuki_bg, R.drawable.yuki_bg, R.drawable.wall_bg,
        R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg,
        R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg,
        R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg,
        R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg,
        R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg,
        R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg,
        R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg,
        R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg, R.drawable.ice_bg,
        R.drawable.wall_bg, R.drawable.yuki_bg, R.drawable.yuki_bg, R.drawable.wall_bg,
    };

    // ブロックのID
    private static final int[] BLOCK_IDS = {
        R.id.block_0_0, R.id.block_1_0, R.id.block_2_0, R.id.block_3_0,
        R.id.block_0_1, R.id.block_1_1, R.id.block_2_1, R.id.block_3_1,
        R.id.block_0_2, R.id.block_1_2, R.id.block_2_2, R.id.block_3_2,
        R.id.block_0_3, R.id.block_1_3, R.id.block_2_3, R.id.block_3_3,
        R.id.block_0_4, R.id.block_1_4, R.id.block_2_4, R.id.block_3_4,
        R.id.block_0_5, R.id.block_1_5, R.id.block_2_5, R.id.block_3_5,
        R.id.block_0_6, R.id.block_1_6, R.id.block_2_6, R.id.block_3_6,
        R.id.block_0_7, R.id.block_1_7, R.id.block_2_7, R.id.block_3_7,
        R.id.block_0_8, R.id.block_1_8, R.id.block_2_8, R.id.block_3_8,
        R.id.block_0_9, R.id.block_1_9, R.id.block_2_9, R.id.block_3_9,
    };

    //　背景のID
    private static final int[] BG_IDS = {
        R.id.bg_0_0, R.id.bg_1_0, R.id.bg_2_0, R.id.bg_3_0,
        R.id.bg_0_1, R.id.bg_1_1, R.id.bg_2_1, R.id.bg_3_1,
        R.id.bg_0_2, R.id.bg_1_2, R.id.bg_2_2, R.id.bg_3_2,
        R.id.bg_0_3, R.id.bg_1_3, R.id.bg_2_3, R.id.bg_3_3,
        R.id.bg_0_4, R.id.bg_1_4, R.id.bg_2_4, R.id.bg_3_4,
        R.id.bg_0_5, R.id.bg_1_5, R.id.bg_2_5, R.id.bg_3_5,
        R.id.bg_0_6, R.id.bg_1_6, R.id.bg_2_6, R.id.bg_3_6,
        R.id.bg_0_7, R.id.bg_1_7, R.id.bg_2_7, R.id.bg_3_7,
        R.id.bg_0_8, R.id.bg_1_8, R.id.bg_2_8, R.id.bg_3_8,
        R.id.bg_0_9, R.id.bg_1_9, R.id.bg_2_9, R.id.bg_3_9,
    };

    // ブロックのMAP
    private static final AreaType[] AREA_MAP = {
        AreaType.WALL, AreaType.WALL, AreaType.CHARACTER, AreaType.WALL,
        AreaType.FREE, AreaType.FREE, AreaType.FREE, AreaType.FREE,
        AreaType.FREE, AreaType.FREE, AreaType.ICE, AreaType.FREE,
        AreaType.FREE, AreaType.FREE, AreaType.FREE, AreaType.FREE,
        AreaType.FREE, AreaType.FREE, AreaType.FREE, AreaType.FREE,
        AreaType.BLOCK, AreaType.FREE, AreaType.FREE, AreaType.FREE,
        AreaType.FREE, AreaType.FREE, AreaType.BLOCK, AreaType.FREE,
        AreaType.FREE, AreaType.FREE, AreaType.FREE, AreaType.FREE,
        AreaType.FREE, AreaType.FREE, AreaType.FREE, AreaType.FREE,
        AreaType.WALL, AreaType.GOAL, AreaType.GOAL, AreaType.WALL,
    };

    // 十字キーのボタン
    private ImageView upButton, downButton, leftButton, rightButton;
    // A,Bボタン
    private ImageView aButton, bButton;
    // キャラクター情報
    private CharacterInfo characterInfo;
    //アイス情報
    private IceInfo iceInfo;

    // エリア情報
    private final List<AreaInfo> areaInfoList = new ArrayList<>();
    //コールバック用インターフェース
    private GameResultCallback callback;

    public static Fragment newInstance(){
        return new BlockFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (GameResultCallback) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context + " must implement BlockListener");
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

        viewSettings(view);

        //背景設定
        for (int i = 0; i < BG_IDS.length; i++){
           view.findViewById(BG_IDS[i]).setBackgroundResource(BG_RES_IDS[i]);
        }

        //エリア情報を作成
        for (int i = 0; i < AREA_MAP.length; i++){
            AreaType areaType = AREA_MAP[i];
            ImageView imageView = view.findViewById(BLOCK_IDS[i]);

            int posX = i % MAX_COLUMN;
            int posY = i / MAX_COLUMN;
            if (areaType == AreaType.CHARACTER) {
                characterSettings(posX, posY, imageView);
            } else if (areaType == AreaType.ICE) {
                iceSettings(posX, posY, imageView);
            } else if (areaType == AreaType.BLOCK){
                imageView.setImageResource((R.drawable.wall_bg));
            }
            areaInfoList.add(new AreaInfo(posX, posY, areaType, imageView));
        }
    }

    private void characterSettings(int posX, int posY, ImageView imageView){
        //キャラクターの設定
        characterInfo = new CharacterInfo(posX, posY, R.drawable.player, imageView, Direction.DOWN);

        changeDirection(characterInfo.getDirection());
    }

    private void iceSettings(int posX, int posY, ImageView imageView){
        //アイスの設定
        imageView.setImageResource(R.drawable.block_koori);
        iceInfo = new IceInfo(posX, posY, R.drawable.block_koori, imageView);
    }

    private void viewSettings(View root){
        //ボタンの設定
        upButton = root.findViewById(R.id.up_button);
        rightButton = root.findViewById(R.id.right_button);
        downButton = root.findViewById(R.id.down_button);
        leftButton = root.findViewById(R.id.left_button);
        aButton = root.findViewById(R.id.a_button);
        bButton = root.findViewById(R.id.b_button);

        //ボタンのクリックリスナー設定
        upButton.setOnClickListener(v -> crossKeyTask(Direction.UP));
        rightButton.setOnClickListener(v -> crossKeyTask(Direction.RIGHT));
        downButton.setOnClickListener(v -> crossKeyTask(Direction.DOWN));
        leftButton.setOnClickListener(v -> crossKeyTask(Direction.LEFT));
        aButton.setOnClickListener(v -> AButtonTask());
        bButton.setOnClickListener(v -> BButtonTask());
    }

    private void crossKeyTask(Direction direction){
        if(characterInfo.getDirection() == direction) {
            moveCharacter(direction);
        }else{
            changeDirection(direction);
        }
    }

    private void moveCharacter(Direction direction){
        //移動するエリア情報の取得
        AreaInfo nextArea = getNextAreaInfo(
                characterInfo.getPositionX(),
                characterInfo.getPositionY(),
                direction
        );
        //移動可能か判定
        if (nextArea != null && nextArea.isMoveable()){
            //移動前のエリア情報の取得
            AreaInfo currentAreaInfo = getAreaInfo(characterInfo.getPositionX(), characterInfo.getPositionY());
            if (currentAreaInfo == null) return;
            //キャラクター情報を更新
            characterInfo.setImageView(nextArea.getImageView());
            characterInfo.setPositionX(nextArea.getPosX());
            characterInfo.setPositionY(nextArea.getPosY());
            changeDirection(direction);

            //移動前のエリア情報を更新
            currentAreaInfo.getImageView().setImageDrawable(null);
            currentAreaInfo.getImageView().setRotation(0f);
        }
    }

    private AreaInfo getNextAreaInfo(int startPosX, int startPosY, Direction direction){

        int posX = startPosX;
        int posY = startPosY;
        switch (direction){
            case UP:
                posY = Math.max(0, posY - 1);
                break;
            case RIGHT:
                posX = Math.min(3, posX + 1);
                break;
            case DOWN:
                posY = Math.min(9, posY +1);
                break;
            case LEFT:
                posX = Math.max(0, posX - 1);
                break;
            default:
                break;
        }

        //移動先が同じ位置の場合はNULLを返す
        if (posX == startPosX && posY == startPosY){
            return null;
        } else {
            return getAreaInfo(posX, posY);
        }
    }

    private AreaInfo getAreaInfo(int posX, int posY){
        return areaInfoList.stream()
                .filter(areaInfo -> areaInfo.getPosX() == posX && areaInfo.getPosY() == posY)
                .findFirst()
                .orElse(null);
    }

    private void changeDirection(Direction direction){
        //キャラクターの位置のViewを取得
        ImageView imageView = characterInfo.getImageView();

        //キャラクターの画像を設定
        imageView.setImageResource(characterInfo.getImageResId());

        //キャラクターの向きを変える処理
        imageView.setRotation(direction.getRotation());

        //キャラクターの情報を更新
        characterInfo.setDirection(direction);
    }

    private void AButtonTask(){

    }

    private void BButtonTask(){

    }

}
