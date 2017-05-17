package com.example.mediaplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.application.mediaApplication;
import com.squareup.leakcanary.RefWatcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import domain.MediaBean;
import utils.Constants;
import utils.ToolUtils;

/**
 * Created by Administrator on 2017/5/13 0013.
 * 播放视频界面
 */

public class VideoPlayActivity extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, View.OnClickListener ,SeekBar.OnSeekBarChangeListener{

    private VideoView videoView;
    private LinearLayout llTop;
    private TextView videoName;
    private TextView videoBattery;
    private TextView videoSystemTime;
    private LinearLayout llBottom;
    private TextView tvCurTime;
    private SeekBar mediaSeekbar;
    private TextView tvDuration;
    private ImageButton btnExit;
    private ImageButton btnPre;
    private ImageButton btnPlayOrPause;
    private ImageButton btnNext;
    private ImageButton btnFullOrNormal;


    private mReceiver receiver;
    private Uri uri;
    private List<MediaBean> mediaList;
    private int position;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.UPDATE_SEEKBAR:{
                    if(videoView != null) {
                        int curPosition = videoView.getCurrentPosition();
                        tvCurTime.setText(ToolUtils.timeToString(curPosition));
                        mediaSeekbar.setProgress(curPosition);
                        //更新系统时间
                        videoSystemTime.setText(getSystemTime());
                        //移除消息 防止重复 一秒更新一次
                        removeMessages(Constants.UPDATE_SEEKBAR);
                        sendEmptyMessageDelayed(Constants.UPDATE_SEEKBAR, 1000);
                        break;
                    }
                }
            }
        }
    };



    private void findViews() {
        setContentView(R.layout.activity_video_play);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        videoName = (TextView)findViewById( R.id.video_name );
        videoBattery = (TextView)findViewById( R.id.video_battery );
        videoSystemTime = (TextView)findViewById( R.id.video_system_time );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurTime = (TextView)findViewById( R.id.tv_cur_time );
        mediaSeekbar = (SeekBar)findViewById( R.id.media_seekbar );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (ImageButton)findViewById( R.id.btn_exit );
        btnPre = (ImageButton)findViewById( R.id.btn_pre );
        btnPlayOrPause = (ImageButton)findViewById( R.id.btn_play_or_pause );
        btnNext = (ImageButton)findViewById( R.id.btn_next );
        btnFullOrNormal = (ImageButton)findViewById( R.id.btn_full_or_normal );
        videoView = (VideoView) findViewById(R.id.video_view);

        btnExit.setOnClickListener( this );
        btnPre.setOnClickListener( this );
        btnPlayOrPause.setOnClickListener( this );
        btnNext.setOnClickListener( this );
        btnFullOrNormal.setOnClickListener( this );

        mediaSeekbar.setOnSeekBarChangeListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RefWatcher refWatcher = mediaApplication.getRefWatcher(getApplicationContext());
        refWatcher.watch(this);

        findViews();


        //注册电量变化广播
        receiver = new mReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver,filter);


        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);

        getDataFromBundle();
        playVideo();
    }


    //根据获取的数据源进行视频播放
    private void playVideo() {

        btnPlayOrPause.setBackgroundResource(R.drawable.control_pause_selector);
        if(uri != null){
            videoView.setVideoURI(uri);
            videoName.setText(uri.toString());
            unableButton();
        }else if(mediaList != null && mediaList.size() > 0){
            videoView.setVideoPath(mediaList.get(position).getData());
            videoName.setText(mediaList.get(position).getName());
            enableButton();
        }else{
            Toast.makeText(VideoPlayActivity.this,"没有视频可播放",Toast.LENGTH_SHORT);
        }
    }


    //第三方播放时候禁用上一首和下一首
    public void unableButton(){
        btnPre.setEnabled(false);
        btnNext.setEnabled(false);
    }
    public void enableButton(){
        btnPre.setEnabled(true);
        btnNext.setEnabled(true);
    }

    //获取播放的音频数据
    private void getDataFromBundle() {

        //第三方调用
        uri = getIntent().getData();

        //自身调用
        Bundle bundle = getIntent().getBundleExtra("info");
        position = bundle.getInt("position",0);
        mediaList = (List<MediaBean>) bundle.getSerializable("mediaList");
    }

    //上一首
    public void playNext(){
        position+=1;
        if(position >= mediaList.size()){
            position = 0;
        }
        playVideo();
    }

    //下一首
    public void playPre(){
        position-=1;
        if(position < 0){
            position = mediaList.size()-1;
        }
        playVideo();
    }

    //获取系统时间
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        return format.format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaApplication.fixInputMethodManagerLeak(this);
        handler.removeCallbacksAndMessages(null);

        if(videoView != null){
            videoView.stopPlayback();
            videoView = null;
        }
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }

    }


    //底层准备好播放视频后回调
    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.start();

        mediaSeekbar.setMax(videoView.getDuration());

        //更新时长
        tvDuration.setText(ToolUtils.timeToString(videoView.getDuration()));

        //更新进度条
        handler.sendEmptyMessage(Constants.UPDATE_SEEKBAR);


    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mediaList != null && mediaList.size() > 0){
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(VideoPlayActivity.this,"出错啦",Toast.LENGTH_SHORT);
        return false;
    }

    @Override
    public void onClick(View v) {
        if ( v == btnExit ) {
            // Handle clicks for btnExit
            if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                btnFullOrNormal.performClick();
            }else{
                finish();
            }
        } else if ( v == btnPre ) {
            playPre();
        } else if ( v == btnPlayOrPause ) {
            if(videoView.isPlaying()){
                videoView.pause();
                btnPlayOrPause.setBackgroundResource(R.drawable.control_play_selector);
            }else{
                videoView.start();
                btnPlayOrPause.setBackgroundResource(R.drawable.control_pause_selector);
            }
        } else if ( v == btnNext ) {
            playNext();
        } else if ( v == btnFullOrNormal ) {
            // Handle clicks for btnFullOrNormal
            if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                btnFullOrNormal.setBackgroundResource(R.drawable.control_default_selector);
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                btnFullOrNormal.setBackgroundResource(R.drawable.control_full_selector);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            videoView.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onBackPressed() {
        btnExit.performClick();
    }

    class mReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int battery = intent.getIntExtra("level",0);
            videoBattery.setText(String.valueOf(battery));
        }
    }
}