package com.example.mediaplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.example.mediaplayer.base.BasePager;

/**
 * Created by Administrator on 2017/5/7.
 * 本地音乐
 */

public class LocalMusicPager extends BasePager{

    private TextView textView;
    public LocalMusicPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(this.context);
        textView.setTextColor(Color.BLUE);
        textView.setTextSize(25);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        textView.setText("本地音乐");
    }
}
