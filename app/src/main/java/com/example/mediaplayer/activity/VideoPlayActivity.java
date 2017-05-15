package com.example.mediaplayer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mediaplayer.R;

/**
 * Created by Administrator on 2017/5/13 0013.
 * 播放视频界面
 */

public class VideoPlayActivity extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private VideoView video_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        video_view = (VideoView) findViewById(R.id.video_view);

        Uri uri = getIntent().getData();

        //video_view.setMediaController(new MediaController(this));
        video_view.setOnPreparedListener(this);
        video_view.setOnCompletionListener(this);
        video_view.setOnErrorListener(this);

        if(uri != null){
            video_view.setVideoURI(uri);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        video_view.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(VideoPlayActivity.this,"出错啦",Toast.LENGTH_SHORT);
        return false;
    }
}