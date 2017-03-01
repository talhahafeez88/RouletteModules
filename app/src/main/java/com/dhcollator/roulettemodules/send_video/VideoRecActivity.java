package com.dhcollator.roulettemodules.send_video;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhcollator.roulettemodules.R;


public class VideoRecActivity extends AppCompatActivity implements RecordVideoFragment.IFragmentInteractionInterface {

    private static final int PERMISSION_REQUEST_CODE = 23;
    public static final int MAX_VIDEO_TIME = 7000; // 7 seconds
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private PlayVideoFragment playVideo;
    private RecordVideoFragment recVideo;

    private ImageView recordBtn;
    private TimerDecrementHandler timerDecrementHandler;
    private TextView video_rec_timer;
    private TextView video_cancel;
    private RelativeLayout snack_bar;
    private LinearLayout recoder_container;
    private boolean isRecording = false;
    private String fileName = null;
    private int pageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean permissionsGranted = true;
        for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[i])
                    != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        PERMISSION_REQUEST_CODE
                );

                permissionsGranted = false;
                break;
            }
        }
        if (permissionsGranted)
            initActivity();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                initActivity();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initActivity() {
        setContentView(R.layout.activity_video_rec);

        pageId = getIntent().getIntExtra(Helper.PAGEID_K, -1);
        recordBtn = (ImageView) findViewById(R.id.btnRecord);
        snack_bar = (RelativeLayout) findViewById(R.id.rl_video_rec_snackbar);
        recoder_container = (LinearLayout) findViewById(R.id.ll_video_rec_container);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                recVideo.releaseMemory();
            }
        });

        ///////Initialize timer//////////
        video_rec_timer = (TextView) findViewById(R.id.timerVideo);
        video_cancel = (TextView) findViewById(R.id.tv_video_rec_cancel);
        video_rec_timer.setText("" + MAX_VIDEO_TIME / 1000);
        /*** Apply popup timer ****/
        timerDecrementHandler = new TimerDecrementHandler(MAX_VIDEO_TIME / 1000
                , video_rec_timer) {
            @Override
            public void onTimeUp() {
            }
        };
        recVideo = new RecordVideoFragment();
        playVideo = new PlayVideoFragment();

        recVideo.setInteractionInterface(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, recVideo);
        ft.commit();
    }

    private void startRecording() {
        fileName = "/videos/" + System.currentTimeMillis() + ".mp4";
        recVideo.onRecClick(fileName);
        timerDecrementHandler.start();
        recoder_container.setVisibility(View.VISIBLE);
        video_cancel.setVisibility(View.VISIBLE);
        video_rec_timer.setVisibility(View.VISIBLE);
        snack_bar.setVisibility(View.GONE);
        isRecording = true;
    }

    private void stopRecording() {
        timerDecrementHandler.stopTimer();
        recoder_container.setVisibility(View.GONE);
        video_cancel.setVisibility(View.GONE);
        video_rec_timer.setVisibility(View.GONE);
        snack_bar.setVisibility(View.VISIBLE);

        timerDecrementHandler.resetTimer(MAX_VIDEO_TIME / 1000);

        recVideo.releaseMemory();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, playVideo);
        ft.commit();

        isRecording = false;
    }

    public void startVideoPlayer(View view) {
//        DebugLog.console("[VideoRecActivity]Video Path:" + getFilesDir() + fileName);
        playVideo.setFilePath(getFilesDir() + fileName);
        playVideo.startVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recVideo != null)
            recVideo.releaseMemory();
    }

    @Override
    public void refreshUI(boolean isRecording) {
        if (recVideo != null)
            recordBtn.setSelected(isRecording);
        if (!isRecording)
            stopRecording();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (recVideo != null)
            recVideo.releaseMemory();
    }

    public void OnCameraSwitchClick(View view) {

    }

    public void OnVidRetakeClick(View view) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, recVideo);
        ft.commit();

        recoder_container.setVisibility(View.VISIBLE);
        video_cancel.setVisibility(View.VISIBLE);
        video_rec_timer.setVisibility(View.VISIBLE);
        snack_bar.setVisibility(View.GONE);
    }

    public void OnVidPlayPauseClick(View view) {
        if (isRecording) {
            recVideo.stopRecording();
            stopRecording();
        } else startRecording();
    }

    public void OnCancelClick(View view) {

//        fileName = "/videos/" + System.currentTimeMillis() + ".mp4";
//        OnUseVideoClick(null);

        discardVideo();
        finish();
    }

    public void OnUseVideoClick(View view) {
        try {
//            String eventID = Helper.EVENT_ID;
//            String bookID = member.getBookID();
//            int fromID = SharedPreferenceUtil.getInstance(this).getActiveMemberId();
//            int toID = member.getMemberID();
//            String localURL = getFilesDir() + fileName;
//            String videoPath = fileName;
//            String blocked = "N";
//            String created = Helper.getCurrentTime();
//            String updated = created;

//            Toast.makeText(this, "Video Page" + Helper.CURRENT_PAGE_ID, Toast.LENGTH_SHORT).show();
//            DebugLog.console("[VideoRecActivity]:Saving video to database");
//            SentVideo video = new SentVideo(eventID, bookID, pageId, fromID, toID, localURL, videoPath, blocked, created, updated);
//            Fcdb.getInstance().insertSentVideoSets(video);

            //Add video icon
//            StickersHandler.getInstance().refreshNotificationIcons();
            finish();
        } catch (Exception ex) {
//            DebugLog.console(ex, "[VideoRecActivity]:Exception inside OnUseVideoClick");
        }
    }

    @Override
    public void onBackPressed() {
        discardVideo();
        super.onBackPressed();
    }

    private void discardVideo() {
    }
}