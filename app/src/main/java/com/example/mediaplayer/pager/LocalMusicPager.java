package com.example.mediaplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activity.AudioPlayActivity;
import com.example.mediaplayer.activity.VideoPlayActivity;
import com.example.mediaplayer.adapter.LocalVideoListAdapter;
import com.example.mediaplayer.base.BasePager;
import com.example.mediaplayer.mListener.mVideoItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import domain.MediaBean;

/**
 * Created by Administrator on 2017/5/7.
 * 本地视频
 */

public class LocalMusicPager extends BasePager implements mVideoItemClickListener {

    private RecyclerView video_list; //列表
    private TextView tv_nomedia; //没有视频
    private ProgressBar pb_loading;
    private List<MediaBean> mediaList;

    public LocalMusicPager(Context context) {
        super(context);
    }


    //视频获取成功后通知该handler处理
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaList != null && mediaList.size() > 0) {
                LocalVideoListAdapter adapter = new LocalVideoListAdapter(context, mediaList, LocalMusicPager.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                video_list.setLayoutManager(layoutManager);
                video_list.setAdapter(adapter);
                video_list.setVisibility(View.VISIBLE);
            } else {
                tv_nomedia.setVisibility(View.VISIBLE);
            }
            pb_loading.setVisibility(View.GONE);
        }
    };

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.pager_loaclvideo, null);
        video_list = (RecyclerView) view.findViewById(R.id.video_list);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        return view;
    }

    @Override
    public void initData() {
        Log.i("LocalMusicPager", "initData");
        super.initData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                pb_loading.setVisibility(View.VISIBLE);
                tv_nomedia.setVisibility(View.GONE);
                video_list.setVisibility(View.GONE);
                mediaList = new ArrayList<MediaBean>();
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] obj = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频名称
                        MediaStore.Audio.Media.DURATION,//时长
                        MediaStore.Audio.Media.SIZE,//大小
                        MediaStore.Audio.Media.DATA,//地址
                        MediaStore.Audio.Media.ARTIST//艺术家
                };
                Cursor cursor = contentResolver.query(uri, obj, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaBean bean = new MediaBean();
                        mediaList.add(bean);

                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        bean.setName(name);

                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        bean.setDuration(duration);

                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        bean.setSize(size);

                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        bean.setData(data);

                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST));
                        bean.setArtist(artist);

                        Log.i("zyb", bean.toString());
                    }
                }
                cursor.close();
                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    @Override
    public void onClick(View v, int position) {
        if (mediaList != null) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("mediaList", (Serializable) mediaList);
            bundle.putInt("position", position);

            Intent intent = new Intent(context, AudioPlayActivity.class);
            intent.putExtra("info", bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
//        }
    }
}

