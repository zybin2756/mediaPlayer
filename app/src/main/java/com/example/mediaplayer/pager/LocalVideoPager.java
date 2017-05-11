package com.example.mediaplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.base.BasePager;

/**
 * Created by Administrator on 2017/5/7.
 * 本地视频
 */

public class LocalVideoPager  extends BasePager{


    public LocalVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.pager_loaclvideo,null);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
    }
}
