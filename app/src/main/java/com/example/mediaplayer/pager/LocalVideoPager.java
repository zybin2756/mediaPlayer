package com.example.mediaplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.base.BasePager;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import domain.MediaBean;

/**
 * Created by Administrator on 2017/5/7.
 * 本地视频
 */

public class LocalVideoPager  extends BasePager{

    private RecyclerView video_list; //列表
    private TextView tv_nomedia; //没有视频
    private ProgressBar pb_loading;
    private List<MediaBean> mediaList;
    public LocalVideoPager(Context context) {
        super(context);
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaList != null && mediaList.size() > 0){

            }
        }
    };
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.pager_loaclvideo,null);
        video_list = (RecyclerView) view.findViewById(R.id.video_list);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaList = new ArrayList<MediaBean>();
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] obj = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频名称
                        MediaStore.Video.Media.DURATION,//时长
                        MediaStore.Video.Media.SIZE,//大小
                        MediaStore.Video.Media.DATA,//地址
                        MediaStore.Video.Media.ARTIST//艺术家
                };
                Cursor cursor = contentResolver.query(uri,obj,null,null,null);
                if(cursor != null){
                    while(cursor.moveToNext()){
                        MediaBean bean = new MediaBean();
                        mediaList.add(bean);

                        String name = cursor.getString(0);
                        bean.setName(name);

                        long duration = cursor.getLong(1);
                        bean.setDuration(duration);

                        long size = cursor.getLong(2);
                        bean.setSize(size);

                        String data = cursor.getString(3);
                        bean.setData(data);

                        String artist = cursor.getString(4);
                        bean.setArtist(artist);
                    }
                }
            }
        }).start();



    }
}
