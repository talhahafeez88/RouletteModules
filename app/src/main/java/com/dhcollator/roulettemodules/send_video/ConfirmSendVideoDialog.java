package com.dhcollator.roulettemodules.send_video;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dhcollator.roulettemodules.R;


public class ConfirmSendVideoDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_popup);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_message = (TextView) findViewById(R.id.tv_alert_msg);
        Button btnOK = (Button) findViewById(R.id.btn_ok);
        tv_message.setText("Sent Video to " + getIntent().getStringExtra("Fname") + " " + getIntent().getStringExtra("Lname"));
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Window window = getWindow();
        float pixel = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_IN, Helper.SMALL_POP_UP_SIZE, getResources().getDisplayMetrics());
        window.setLayout((int) pixel, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getWindow();
        float pixel = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_IN, Helper.SMALL_POP_UP_SIZE, getResources().getDisplayMetrics());
        window.setLayout((int) pixel, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
