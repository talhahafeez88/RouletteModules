package com.dhcollator.roulettemodules.send_video;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.dhcollator.roulettemodules.R;

import java.io.File;
import java.io.IOException;

public class RecordVideoFragment extends Fragment implements SurfaceHolder.Callback, MediaRecorder.OnInfoListener {

    private int mCameraContainerWidth = 0;
    private SurfaceView mSurfaceView = null;

    private SurfaceHolder mSurfaceHolder = null;
    private Camera mCamera = null;
    private boolean mIsRecording = false;

    private int mPreviewHeight;
    private int mPreviewWidth;
    private boolean isTrue = false;
    private View view;

    private MediaRecorder mRecorder;
    private IFragmentInteractionInterface mMainActivity;

    public RecordVideoFragment() {
    }

    public void setInteractionInterface(IFragmentInteractionInterface pInterface) {
        mMainActivity = pInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record_video, container, false);


        //camera preview
        mSurfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCamera = getCamera();
        mCamera.setDisplayOrientation(90);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCameraContainerWidth = mSurfaceView.getLayoutParams().width;
    }

    public void releaseMemory() {
        releaseMediaRecorder();
        releaseCamera();
    }

    private void startRecording() {
        // Camera is available and unlocked, MediaRecorder is prepared,
        // now you can start recording
        try {
//            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.start();
            // inform the user that recording has started
            Toast.makeText(getActivity(), "Started recording", Toast.LENGTH_SHORT).show();
            mIsRecording = true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error Occurred!", Toast.LENGTH_SHORT).show();
        }

    }

    public void stopRecording() {
        mRecorder.stop();  // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object
        mCamera.lock();         // take camera access back from MediaRecorder

        // inform the user that recording has stopped
        Toast.makeText(getActivity(), "Recording complete", Toast.LENGTH_SHORT).show();
        mIsRecording = false;
    }

    public void onRecClick(String fileName) {
        if (mIsRecording) {
            stopRecording();
        } else {
            // initialize video camera
            if (prepareVideoRecorder(fileName)) {
                startRecording();
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
        mMainActivity.refreshUI(mIsRecording);
    }

    private Camera getCamera() {

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    return mCamera = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("cameras", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return null;
    }

    private Camera.Size getBestPreviewSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width < size.height) continue; //we are only interested in landscape variants

            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea > resultArea) {
                    result = size;
                }
            }
        }

        return (result);
    }

    private boolean prepareVideoRecorder(String fileName) {

        // Step 1: Unlock and set camera to MediaRecorder
        mRecorder = new MediaRecorder();
        try {
//            mCamera.setPreviewDisplay(null); // TODO: 2/14/2017 by these 2 lines, it starts recording.  but couldnt play and surface hangs.
//            mCamera.stopPreview();
            mCamera.unlock();//.release causes it to hang
        } catch (Exception e) {
            e.printStackTrace();
            mCamera = getCamera();
            mPreviewHeight = mCamera.getParameters().getPreviewSize().height;
            mPreviewWidth = mCamera.getParameters().getPreviewSize().width;

            mCamera.stopPreview();
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            mCamera.startPreview();
        }
        mRecorder.setCamera(mCamera);

        // Step 2: Set sources
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        // Customise your profile based on a pre-existing profile
        CamcorderProfile profile = CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_FRONT, CamcorderProfile.QUALITY_HIGH);
        mRecorder.setProfile(profile);
//        setProfile(profile);

        // Step 4: Set output file
        try {
            File file = new File(getActivity().getFilesDir() + fileName);
            mRecorder.setOutputFile(getActivity().getFilesDir() + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            isTrue = true;
            return false;
        }
        mRecorder.setMaxDuration(VideoRecActivity.MAX_VIDEO_TIME);
        mRecorder.setOnInfoListener(this);

        if (mPreviewWidth > 0 && mPreviewHeight > 0)
            mRecorder.setVideoSize(mPreviewWidth, mPreviewHeight);

        // Step 5: Set the preview output
        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mRecorder.setOrientationHint(270);
            boolean isValid = mSurfaceHolder.getSurface().isValid();
            if (isValid) {
                isTrue = false;
                mRecorder.prepare();
//                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            } else {
                isTrue = true;
                return false;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
            releaseMediaRecorder();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    public void setProfile(CamcorderProfile profile) {
//        mRecorder.setOutputFormat(profile.fileFormat);
//        mRecorder.setVideoFrameRate(profile.videoFrameRate);
//        mRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
//        mRecorder.setVideoEncodingBitRate(profile.videoBitRate);
//        mRecorder.setVideoEncoder(profile.videoCodec);
//        if (profile.quality >= CamcorderProfile.QUALITY_TIME_LAPSE_LOW &&
//                profile.quality <= CamcorderProfile.QUALITY_TIME_LAPSE_QVGA) {
//            // Nothing needs to be done. Call to setCaptureRate() enables
//            // time lapse video recording.
//        } else {
//            mRecorder.setAudioEncodingBitRate(profile.audioBitRate);
//            mRecorder.setAudioChannels(profile.audioChannels);
//            mRecorder.setAudioSamplingRate(profile.audioSampleRate);
//            mRecorder.setAudioEncoder(profile.audioCodec);
//        }
//    }

    private void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.reset();   // clear recorder configuration
            mRecorder.release(); // release the recorder object
            mRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRecordingHint(true);
        Camera.Size size = getBestPreviewSize(parameters);
        mCamera.setParameters(parameters);

        //resize the view to the specified surface view width in layout
        int newHeight = size.height / (size.width / mCameraContainerWidth);
        mSurfaceView.getLayoutParams().height = newHeight;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mPreviewHeight = mCamera.getParameters().getPreviewSize().height;
        mPreviewWidth = mCamera.getParameters().getPreviewSize().width;

        mCamera.stopPreview();
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
//        if (isTrue)
//            onRecClick();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mIsRecording) {
            stopRecording();
        }

        releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int what, int i1) {
//        toString();
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            stopRecording();
            mMainActivity.refreshUI(mIsRecording);
            new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.videoMaxLengthReachedTitle))
                    .setMessage(getString(R.string.videoMaxLengthReachedMessage))
                    .setPositiveButton(getString(R.string.ok), null).show();
        }
    }

    public interface IFragmentInteractionInterface {
        void refreshUI(boolean isRecording);
    }
}
