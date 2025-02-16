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
import com.example.iceinwonderland.ui.GameResultCallback;
import com.example.iceinwonderland.ui.GameRetryCallback;
import com.example.iceinwonderland.ui.stageselect.StageSelectFragment;

public class GameOverFragment extends Fragment {

    private ImageView retryButton;
    private ImageView topButton;
    private GameRetryCallback callback;

    public  static  Fragment newInstance() {
        return new GameOverFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            callback = (GameRetryCallback) context;
        }catch(ClassCastException e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_over,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       retryButton = view.findViewById(R.id.retry);
        topButton = view.findViewById(R.id.top);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(callback == null)return;
               callback.onGameRetry();
            }
        });

        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = StageSelectFragment.newInstance();
                if (fragment != null) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.gameover, fragment)
                            .commit();
                }
            }
        });
    }
}
