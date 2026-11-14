package com.example.iceinwonderland.ui.result;

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
import com.example.iceinwonderland.ui.BackToStageSelectCallback;

public class GameClearFragment extends Fragment {

    private ImageView nextButton;
    private BackToStageSelectCallback callback;

    public static Fragment newInstance() {
        return new GameClearFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BackToStageSelectCallback) {
            callback = (BackToStageSelectCallback) context;
        } else {
            throw new ClassCastException(context + " must implement BackToStageSelectCallback");
        }
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

        // NEXTを押したら、自分で切り替えずに受付へ「ステージ選択に戻して」と頼む
        nextButton.setOnClickListener(v -> {
            if (callback != null) {
                callback.onBackToStageSelect();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}
