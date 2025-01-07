package com.example.iceinwonderland.ui.stageselect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;

public class StageSelectFragment extends Fragment {

    public static Fragment newInstance(){
        StageSelectFragment fragment = new StageSelectFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stage_select, container, false);
    }
}

