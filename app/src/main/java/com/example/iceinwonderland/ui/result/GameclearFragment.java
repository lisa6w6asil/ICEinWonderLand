package com.example.iceinwonderland.ui.result;

import android.content.Intent;
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
import com.example.iceinwonderland.ui.stageselect.StageSelectFragment;

public class GameclearFragment extends Fragment {

    private ImageView nextButton;


    public static Fragment newInstance() {
        return new GameclearFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_clear, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nextButton = view.findViewById(R.id.next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = StageSelectFragment.newInstance();
                if (fragment != null) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.gameclear, fragment)
                            .commit();
                }
            }
        });
    }
}