package com.dhcollator.roulettemodules.send_video;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import com.dhcollator.roulettemodules.R;


public class PlayVideoActivity extends AppCompatActivity {

    public static final String FILE_PATH = "FILE_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        String filePath = getIntent().getStringExtra(FILE_PATH);

        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoPath(filePath);
        videoView.start();
    }
}
