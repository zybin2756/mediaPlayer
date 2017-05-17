package com.example.mediaplayer.base;

import android.content.Context;
import android.view.View;

import com.example.mediaplayer.application.mediaApplication;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/7.
 *  BasePager 页面基类
 */

public abstract class BasePager implements Serializable{
    public Context context;

    public boolean isInitData = false;
    public View rootView;
    public BasePager(Context context) {
        super();
        this.context = mediaApplication.getContext();
        rootView  = initView();
    }

    //初始化view  抽象方法用于
    public abstract View initView();

    public void initData(){

    }
}
