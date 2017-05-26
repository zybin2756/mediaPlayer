package com.example.mediaplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediaplayer.R;
import com.example.mediaplayer.application.mediaApplication;
import com.squareup.leakcanary.RefWatcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import domain.MediaBean;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import utils.Constants;
import utils.ToolUtils;

/**
 * Created by Administrator on 2017/5/13 0013.
 * 播放视频界面
 */

public class VitamioVideoPlayActivity extends Activity implements MediaPlayer.OnPreparedListener,
                                                    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
                                                    View.OnClickListener, SeekBar.OnSeekBarChangeListener,
                                                    MediaPlayer.OnInfoListener
{

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
    private RelativeLayout rl_controller_bar;
    private LinearLayout ll_buffer;
    private LinearLayout ll_loading;
    private TextView tv_speed;
    private TextView tv_loading_speed;

    private mReceiver receiver;
    private GestureDetector gestureDetector;
    private Uri uri; //播放uri
    private List<MediaBean> mediaList; //播放列表
    private int position; //当前播放文件在列表中位置
    private boolean isMediaControllerVisible; //控制面板是否显示

    private AudioManager audioManager; //音量控制

    private float   startX; //按下X坐标
    private float   startY; //按下Y坐标
    private float   endX;
    private float   endY;
    private int     maxVol;
    private int     curVol; //滑动前音量
    private float   touchRang; //屏幕竖直距离
    private int curBright; //当前亮度

    private int screenWidth;
    private int screenHeight;

    private boolean isNetUri; //是否网络链接

    private int prePosition = 0; //上一次进度 用于判断是否卡顿

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.UPDATE_SPEED:
                    String speed = ToolUtils.getNetSpeed(VitamioVideoPlayActivity.this);
                    if(ll_loading.getVisibility() == View.VISIBLE ){
                        tv_loading_speed.setText("玩命加载中..."+speed);
                    }else if(ll_buffer.getVisibility() == View.VISIBLE){
                        tv_speed.setText("缓冲中..."+speed);
                    }

                    removeMessages(Constants.UPDATE_SPEED);
                    sendEmptyMessageDelayed(Constants.UPDATE_SPEED, 1000);
                    break;
                case Constants.HIDE_MEDIA_CONTROLLER:{
                    hideController();
                    handler.removeMessages(Constants.HIDE_MEDIA_CONTROLLER);
                    break;
                }
                case Constants.UPDATE_SEEKBAR: {
                    if (videoView != null) {
                        int curPosition = (int) videoView.getCurrentPosition();
                        tvCurTime.setText(ToolUtils.timeToString(curPosition));
                        mediaSeekbar.setProgress(curPosition);
                        //更新系统时间
                        videoSystemTime.setText(getSystemTime());

                        if(isNetUri){
                            int buffer = videoView.getBufferPercentage();
                            int secondProgess = buffer * mediaSeekbar.getMax()/100;
                            mediaSeekbar.setSecondaryProgress(secondProgess);
                        }else{
                            mediaSeekbar.setSecondaryProgress(0);
                        }

                        //自定义判断卡顿方法 判断两次进度差小于500则卡
                        if(!isUseSystem){
                            if(videoView.isPlaying()) {
                                if (curPosition - prePosition < 500) {
                                    ll_buffer.setVisibility(View.VISIBLE);
                                } else {
                                    ll_buffer.setVisibility(View.GONE);
                                }
                            }else{
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }
                        //移除消息 防止重复 一秒更新一次
                        removeMessages(Constants.UPDATE_SEEKBAR);
                        sendEmptyMessageDelayed(Constants.UPDATE_SEEKBAR, 1000);
                        break;
                    }
                }
            }
        }
    };
    private boolean isUseSystem = true; //使用系统监听卡顿 还是 自定义


    private void findViews() {
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_vitamio_video_play);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        videoName = (TextView) findViewById(R.id.video_name);
        videoBattery = (TextView) findViewById(R.id.video_battery);
        videoSystemTime = (TextView) findViewById(R.id.video_system_time);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurTime = (TextView) findViewById(R.id.tv_cur_time);
        mediaSeekbar = (SeekBar) findViewById(R.id.media_seekbar);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (ImageButton) findViewById(R.id.btn_exit);
        btnPre = (ImageButton) findViewById(R.id.btn_pre);
        btnPlayOrPause = (ImageButton) findViewById(R.id.btn_play_or_pause);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnFullOrNormal = (ImageButton) findViewById(R.id.btn_full_or_normal);
        videoView = (VideoView) findViewById(R.id.vitamio_video_view);
        rl_controller_bar = (RelativeLayout) findViewById(R.id.rl_controller_bar);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_speed  = (TextView) findViewById(R.id.tv_speed);
        tv_loading_speed  = (TextView) findViewById(R.id.tv_loading_speed);

        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnPlayOrPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnFullOrNormal.setOnClickListener(this);

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
        registerReceiver(receiver, filter);


        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        if(isUseSystem){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(this);
            }
        }

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                btnFullOrNormal.performClick();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    if (isMediaControllerVisible) {
                        handler.sendEmptyMessage(Constants.HIDE_MEDIA_CONTROLLER);
                    } else {
                        showController();
                        handler.sendEmptyMessageDelayed(Constants.HIDE_MEDIA_CONTROLLER, 5000);
                    }
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


        //获取屏幕大小
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        //播放
        getDataFromBundle();
        playVideo();

        btnFullOrNormal.performClick();
        handler.sendEmptyMessage(Constants.UPDATE_SPEED);
    }


    //根据获取的数据源进行视频播放
    private void playVideo() {
        ll_loading.setVisibility(View.VISIBLE);

        btnPlayOrPause.setBackgroundResource(R.drawable.control_pause_selector);
        if (uri != null) {
            isNetUri = ToolUtils.isNetUri(uri.toString());
            videoView.setVideoURI(uri);
            videoName.setText(uri.toString());
            unableButton();
        } else if (mediaList != null && mediaList.size() > 0) {
            isNetUri = ToolUtils.isNetUri(mediaList.get(position).getData());
            videoView.setVideoPath(mediaList.get(position).getData());
            videoName.setText(mediaList.get(position).getName());
            enableButton();
        } else {
            Toast.makeText(VitamioVideoPlayActivity.this, "没有视频可播放", Toast.LENGTH_SHORT);
        }
    }


    //第三方播放时候禁用上一首和下一首
    public void unableButton() {
        btnPre.setEnabled(false);
        btnNext.setEnabled(false);
    }

    public void enableButton() {
        btnPre.setEnabled(true);
        btnNext.setEnabled(true);
    }

    //获取播放的音频数据
    private void getDataFromBundle() {

        //第三方调用
        uri = getIntent().getData();

        //自身调用
        Bundle bundle = getIntent().getBundleExtra("info");
        if(bundle != null) {
            position = bundle.getInt("position", 0);
            mediaList = (List<MediaBean>) bundle.getSerializable("mediaList");
        }
    }

    //上一首
    public void playNext() {
        position += 1;
        if (position >= mediaList.size()) {
            position = 0;
        }
        playVideo();
    }

    //下一首
    public void playPre() {
        position -= 1;
        if (position < 0) {
            position = mediaList.size() - 1;
        }
        playVideo();
    }

    //获取系统时间
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        return format.format(new Date());
    }

    /*
    *
    * 隐藏控制栏
    * */
    private void hideController(){
        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            isMediaControllerVisible = false;
            rl_controller_bar.setVisibility(View.GONE);
        }
    }
     /*
    *
    * 显示控制栏
    * */
     private void showController(){
         isMediaControllerVisible = true;
         rl_controller_bar.setVisibility(View.VISIBLE);
     }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaApplication.fixInputMethodManagerLeak(this);
        handler.removeCallbacksAndMessages(null);

        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

    }


    //底层准备好播放视频后回调
    @Override
    public void onPrepared(MediaPlayer mp) {
        prePosition = 0;
        videoView.start();

        mediaSeekbar.setMax((int) videoView.getDuration());

        //更新时长
        tvDuration.setText(ToolUtils.timeToString(videoView.getDuration()));

        //更新进度条
        handler.sendEmptyMessage(Constants.UPDATE_SEEKBAR);

        ll_loading.setVisibility(View.GONE);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaList != null && mediaList.size() > 0) {
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Toast.makeText(VitamioVideoPlayActivity.this, "出错啦", Toast.LENGTH_SHORT);
        showErrorDia();
        return true;
    }

    private void showErrorDia() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("播放错误");
        builder.setTitle("抱歉,无法播放该视频,请检查视频是否存在!");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        if (v == btnExit) {
            // 如果全屏 就退出全屏 否则退出播放
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                btnFullOrNormal.performClick();
            } else {
                finish();
            }
        } else if (v == btnPre) {
            //上一首
            playPre();
        } else if (v == btnPlayOrPause) {
            //暂停或播放
            if (videoView.isPlaying()) {
                videoView.pause();
                btnPlayOrPause.setBackgroundResource(R.drawable.control_play_selector);
            } else {
                videoView.start();
                btnPlayOrPause.setBackgroundResource(R.drawable.control_pause_selector);
            }
        } else if (v == btnNext) {
            //下一首
            playNext();
        } else if (v == btnFullOrNormal) {
            // 全屏或正常
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                btnFullOrNormal.setBackgroundResource(R.drawable.control_default_selector);
            } else {
                showController();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                btnFullOrNormal.setBackgroundResource(R.drawable.control_full_selector);
            }
        }
        handler.removeMessages(Constants.HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(Constants.HIDE_MEDIA_CONTROLLER,5000);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            videoView.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeMessages(Constants.HIDE_MEDIA_CONTROLLER);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.sendEmptyMessageDelayed(Constants.HIDE_MEDIA_CONTROLLER,5000);
    }

    @Override
    public void onBackPressed() {
        btnExit.performClick();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what){
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                ll_buffer.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                ll_buffer.setVisibility(View.GONE);
                break;
        }
        return false;
    }

    class mReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int battery = intent.getIntExtra("level", 0);
            videoBattery.setText(String.valueOf(battery));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    startX = event.getX();
                    startY = event.getY();
                    curVol = audioManager.getStreamVolume(audioManager.STREAM_MUSIC);
                    try {
                        curBright = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    endX = event.getX();
                    endY = event.getY();

                    float distanceY = startY - endY;
                    float distanceX = endX - startX;

                    if(Math.abs(distanceX) > Math.abs(distanceY)){
                        if(Math.abs(distanceX) < 10) break;
                        touchRang = screenHeight;
                        float delta =  (distanceX / touchRang)/100;
                        delta*=videoView.getDuration();
                        int progress = (int)Math.min(Math.max(videoView.getCurrentPosition()+delta,0),videoView.getDuration());
                        updateProgress(progress);
                    }else{
                        if(Math.abs(distanceY) < 10) break;
                        touchRang = screenWidth;
                        float delta =  (distanceY / touchRang);
                        if (startX > screenWidth / 2) {
                            delta *= maxVol;
                            int voice = (int) Math.min(Math.max(curVol+delta,0),100);
                            updateVolumn(voice);
                        } else if (startX < screenWidth / 2) {
                            delta *= 255;
                            int bright = (int) Math.min(Math.max(curBright+delta,1),255);
                            updateBrightness(bright);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    //快进
    private void updateProgress(int progress) {
        showController();
        mediaSeekbar.setProgress(progress);
        videoView.seekTo(progress);
        tvCurTime.setText(ToolUtils.timeToString(progress));
        handler.removeMessages(Constants.HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(Constants.HIDE_MEDIA_CONTROLLER,5000);
    }


    //设置音量
    private void updateVolumn(int newVol) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,newVol,AudioManager.FLAG_SHOW_UI);
    }

    //设置屏幕亮度
    private void updateBrightness(int newBright){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = newBright/255f;
        getWindow().setAttributes(lp);
    }
}