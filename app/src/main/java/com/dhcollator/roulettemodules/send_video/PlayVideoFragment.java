package com.dhcollator.roulettemodules.send_video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.dhcollator.roulettemodules.R;


public class PlayVideoFragment extends Fragment {

    private VideoView videoView;
    private String filePath;

    public PlayVideoFragment() {
    }

    public void setFilePath(String pFilePath) {
        this.filePath = pFilePath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_video, container, false);
        videoView = (VideoView) view.findViewById(R.id.videoView_);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        startVideo();
    }

    public void startVideo() {
        videoView.setVideoPath(filePath);
        videoView.start();
    }
}
