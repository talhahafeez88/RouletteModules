package com.dhcollator.roulettemodules.send_video;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.afollestad.materialcamera.MaterialCamera;
import com.dhcollator.roulettemodules.R;

import java.io.File;
import java.text.DecimalFormat;

public class RecordVideoActivity extends AppCompatActivity {


    private final static int CAMERA_RQ = 6969;
    public static final int MAX_VIDEO_TIME = 10000; // 10 seconds
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    private static final int PERMISSION_REQUEST_CODE = 23;
    private int pageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
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

        String fileName = "/videos/" + System.currentTimeMillis() + ".mp4";
//        File saveDir = new File(fileName);
        File saveDir = new File(getFilesDir() + fileName);
        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .defaultToFrontFacing(true)
                .showPortraitWarning(false)
                .allowRetry(true)
                .autoSubmit(false)
                .labelConfirm(R.string.use_video)
                .countdownMillis(MAX_VIDEO_TIME);
              //  .iconRecord(R.drawable.camera_capture).iconRearCamera(R.drawable.video_rec).iconFrontCamera(R.drawable.video_rec);

        materialCamera.start(CAMERA_RQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Received recording or error from MaterialCamera
        if (requestCode == CAMERA_RQ) {
            if (resultCode == RESULT_OK) {
                final File file = new File(data.getData().getPath());
//                Toast.makeText(this, String.format("Saved to: %s, size: %s",
//                        file.getAbsolutePath(), fileSize(file)), Toast.LENGTH_LONG).show();
                try {
//                    String eventID = Helper.EVENT_ID;
//                    String bookID = member.getBookID();
//                    int fromID = SharedPreferenceUtil.getInstance(this).getActiveMemberId();
//                    int toID = member.getMemberID();
//                    String localURL = file.getAbsolutePath();
//                    String videoPath = file.getName();
//                    String blocked = "N";
//                    String created = Helper.getCurrentTime();
//                    String updated = created;
//                    SentVideo video = new SentVideo(eventID, bookID, pageId, fromID, toID, localURL, videoPath, blocked, created, updated);
//                    Fcdb.getInstance().insertSentVideoSets(video);
//                    startActivity(new Intent(RecordVideoActivity.this, ConfirmSendVideoDialog.class).putExtra("Fname", member.getFirst()).putExtra("Lname", member.getLast()));
//                    //Add video icon
//                    StickersHandler.getInstance().refreshNotificationIcons();
                    finish();

                } catch (Exception ex) {
                }
            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        finish();
    }

    private String fileSize(File file) {
        return readableFileSize(file.length());
    }

    private String readableFileSize(long size) {
        if (size <= 0) return size + " B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


}
