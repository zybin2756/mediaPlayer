package com.example.mediaplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activity.VideoPlayActivity;
import com.example.mediaplayer.adapter.NetVideoListAdapter;
import com.example.mediaplayer.base.BasePager;
import com.example.mediaplayer.mListener.mVideoItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import domain.NetMediaBean;
import utils.Constants;

/**
 * Created by Administrator on 2017/5/7.
 * 网络视频
 */

@ContentView(R.layout.pager_netvideo)
public class netVideoPager extends BasePager implements mVideoItemClickListener {


    final String TAG = "netVideoPager";

    private List<NetMediaBean> netMediaList;

    @ViewInject(R.id.net_video_list)
    private RecyclerView net_video_list;

    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;

    public netVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.pager_netvideo, null);
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        RequestParams params = new RequestParams(Constants.NET_URL);

        showList(false);
        showProgressBar(true);
        showTip(false);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess");
                SharedPreferences sharedPreferences = context.getSharedPreferences("cache",Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("netVideo",result).commit();
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, ex.getMessage());
                showList(false);
                showProgressBar(false);
                showTip(true);

                SharedPreferences sharedPreferences = context.getSharedPreferences("cache",Context.MODE_PRIVATE);
                if(sharedPreferences != null) {
                    String result = sharedPreferences.getString("netVideo", "");
                    if (result != "")
                        processData(result);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, cex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.i(TAG, "onFinished");
            }
        });
    }

    private void processData(String result) {
        netMediaList = new ArrayList<NetMediaBean>();
        try {
            JSONObject r = new JSONObject(result);
            JSONArray array = r.getJSONArray("trailers");
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    NetMediaBean mediaBean = new NetMediaBean();
                    mediaBean.setId(object.getInt("id"));
                    mediaBean.setMovieID(object.getInt("movieId"));
                    mediaBean.setVideoLength(object.getInt("videoLength"));
                    mediaBean.setMovieName(object.getString("movieName"));
                    mediaBean.setCoverImg(object.getString("coverImg"));
                    mediaBean.setPlayUrl(object.getString("url"));
                    mediaBean.setHighPlayUrl(object.getString("hightUrl"));
                    mediaBean.setVideoTitle(object.getString("videoTitle"));
                    mediaBean.setSummary(object.getString("summary"));
                    netMediaList.add(mediaBean);
                    Log.i(TAG, mediaBean.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(netMediaList != null && netMediaList.size() > 0){

            NetVideoListAdapter adapter = new NetVideoListAdapter(context,netMediaList,this);
            net_video_list.setAdapter(adapter);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            net_video_list.setLayoutManager(layoutManager);


            showProgressBar(false);
            showList(true);
            showTip(false);
        }else{
            showProgressBar(false);
            showList(false);
            showTip(true);
        }
    }


    //显示列表
    public void showList(boolean isShow) {
        if (isShow) {
            net_video_list.setVisibility(View.VISIBLE);
        } else {
            net_video_list.setVisibility(View.GONE);
        }
    }

    //显示进度圈
    public void showProgressBar(boolean isShow) {
        if (isShow) {
            pb_loading.setVisibility(View.VISIBLE);
        } else {
            pb_loading.setVisibility(View.GONE);
        }
    }

    //显示网络问题
    public void showTip(boolean isShow) {
        if (isShow) {
            tv_nonet.setVisibility(View.VISIBLE);
        } else {
            tv_nonet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v, int position) {
        NetMediaBean bean = netMediaList.get(position);
        Intent intent = new Intent(context,VideoPlayActivity.class);
        Uri uri = Uri.parse(bean.getPlayUrl());
        intent.setDataAndType(uri,"video/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
