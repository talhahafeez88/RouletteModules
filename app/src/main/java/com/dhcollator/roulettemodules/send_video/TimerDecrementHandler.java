package com.dhcollator.roulettemodules.send_video;

import android.os.Handler;
import android.widget.TextView;

/**
 * Created by KHAN on 1/8/2016.
 */

public abstract class TimerDecrementHandler extends Handler {
    int timeLeft;
    TextView textView;
    boolean continueTimer;

    public TimerDecrementHandler(int startFrom, TextView textView) {
        this.timeLeft = startFrom;
        this.textView = textView;
        continueTimer = true;
    }

    public void start() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (timeLeft >= 0)
                    textView.setText(--timeLeft + "");
                if (timeLeft <= 0) {
                    onTimeUp();
                } else if (continueTimer)
                    postDelayed(this, 1000);
            }
        }, 1000);
    }

    public abstract void onTimeUp();

    public void stopTimer() {
        continueTimer = false;
    }

    public void resetTimer(int startFrom) {
        this.timeLeft = startFrom;
        textView.setText(timeLeft);
        continueTimer = true;
    }
}
