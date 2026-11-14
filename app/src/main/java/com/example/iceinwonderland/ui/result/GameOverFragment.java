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
import com.example.iceinwonderland.ui.GameRetryCallback;

public class GameOverFragment extends Fragment {

    private ImageView retryButton;
    private ImageView topButton;
    private GameRetryCallback retryCallback;          // 「やり直して」の電話番号
    private BackToStageSelectCallback backCallback;   // 「ステージ選択に戻して」の電話番号

    public static Fragment newInstance() {
        return new GameOverFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GameRetryCallback && context instanceof BackToStageSelectCallback) {
            retryCallback = (GameRetryCallback) context;
            backCallback = (BackToStageSelectCallback) context;
        } else {
            throw new ClassCastException(context + " must implement GameRetryCallback and BackToStageSelectCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_over, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        retryButton = view.findViewById(R.id.retry);
        topButton = view.findViewById(R.id.top);

        // リトライ:受付に「同じステージをやり直して」と頼む
        retryButton.setOnClickListener(v -> {
            if (retryCallback != null) {
                retryCallback.onGameRetry();
            }
        });

        // TOP:自分で切り替えずに、受付に「ステージ選択に戻して」と頼む
        topButton.setOnClickListener(v -> {
            if (backCallback != null) {
                backCallback.onBackToStageSelect();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        retryCallback = null;
        backCallback = null;
    }
}
