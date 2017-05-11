package com.example.mediaplayer.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.example.mediaplayer.R;

public class SplashActivity extends AppCompatActivity{

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        },2000);
    }


    @Override
    protected void onDestroy() {
        //移除消息
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void startMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //触摸跳过启动页
        startMainActivity();
        return super.onTouchEvent(event);
    }
}
