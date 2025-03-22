package com.example.iceinwonderland.ui.video;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.iceinwonderland.R;
import com.example.iceinwonderland.ui.catchball.CatchBallFragment;
import com.example.iceinwonderland.ui.stageselect.StageSelectFragment;

public class VideoFragment extends Fragment {

    public static Fragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // フラグメントのレイアウトを設定
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        // VideoViewを取得
        VideoView videoView = view.findViewById(R.id.videoView);

        // 動画のURIを設定（rawフォルダにある動画）
        Uri videoUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.op);

        // 動画のURIをVideoViewに設定
        videoView.setVideoURI(videoUri);

        // MediaControllerを設定して、動画の操作（再生、一時停止など）を可能にする
        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // 動画の再生
        videoView.start();

        // 動画再生終了時のリスナーを設定
        videoView.setOnCompletionListener(mp -> {
            // 動画が終了したらStageSelectFragmentに遷移
            replaceFragment(new CatchBallFragment());
        });

        return view;
    }

    // フラグメントを置き換えるメソッド
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment); // R.id.fragment_containerはフラグメントを表示する親ビュー
        transaction.addToBackStack(null);  // 戻るボタンで前の画面に戻れるようにする
        transaction.commit();
    }
}
