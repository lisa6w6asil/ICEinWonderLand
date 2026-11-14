package com.example.iceinwonderland.ui.title;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;

public class TitleFragment extends Fragment {

    // 受付(MainActivity)への電話番号
    private TitleListener listener;

    public interface TitleListener {
        void onStartPressed();
    }

    public static Fragment newInstance() {
        return new TitleFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TitleListener) {
            listener = (TitleListener) context;
        } else {
            throw new ClassCastException(context + " must implement TitleListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_title, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // スタートが押されたら、自分で切り替えずに受付へ「始めて」と頼む
        view.findViewById(R.id.start).setOnClickListener(v -> {
            if (listener != null) {
                listener.onStartPressed();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
