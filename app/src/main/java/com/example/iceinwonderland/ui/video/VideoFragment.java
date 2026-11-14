package com.example.iceinwonderland.ui.video;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.ui.StageInfo;
import com.example.iceinwonderland.ui.stageselect.StageSelectFragment;

public class VideoFragment extends Fragment {

    // 受付(MainActivity)への電話番号。ステージ選択画面と同じ窓口を使う
    private StageSelectFragment.StageSelectListener listener;

    public static Fragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof StageSelectFragment.StageSelectListener) {
            listener = (StageSelectFragment.StageSelectListener) context;
        } else {
            throw new ClassCastException(context + " must implement StageSelectListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        VideoView videoView = view.findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.op);
        videoView.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(requireContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.start();

        // 動画が終わったら、自分で切り替えずに受付へ「海ステージを始めて」と頼む
        videoView.setOnCompletionListener(mp -> {
            if (listener != null) {
                listener.onStageSelect(StageInfo.Sea);
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // 画面から外れたあとに動画終了の合図が来ても、電話しないようにする
        listener = null;
    }
}
