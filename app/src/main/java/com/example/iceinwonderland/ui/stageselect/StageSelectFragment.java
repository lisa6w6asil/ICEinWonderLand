package com.example.iceinwonderland.ui.stageselect;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.ui.StageInfo;

public class StageSelectFragment extends Fragment implements View.OnClickListener {

    private ImageView treeImage;

    private StageSelectListener listener;

    public interface StageSelectListener{
        void onStageSelect(StageInfo stageInfo);
    }


    public static Fragment newInstance(){
        StageSelectFragment fragment = new StageSelectFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (StageSelectListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context + "must implement StageSelectListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stage_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //各Viewの設定
        viewSettings(view);

    }

    private void viewSettings(View root){
        //画像よみこみ
        treeImage = root.findViewById(R.id.tree);

        //クリック処理の設定
        treeImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if(listener == null) return;
        //木だったらオンステージセレクトを呼んでメインに伝える
        if(resId == R.id.tree){
            listener.onStageSelect(StageInfo.Tree);
        }
    }
}

