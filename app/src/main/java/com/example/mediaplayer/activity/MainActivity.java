package com.example.mediaplayer.activity;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.mediaplayer.R;
import com.example.mediaplayer.base.BasePager;
import com.example.mediaplayer.fragment.mFragment;
import com.example.mediaplayer.pager.LocalMusicPager;
import com.example.mediaplayer.pager.LocalVideoPager;
import com.example.mediaplayer.pager.netMusicPager;
import com.example.mediaplayer.pager.netVideoPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioButton rb_localvideo;
    private RadioButton rb_localmusic;
    private RadioButton rb_netvideo;
    private RadioButton rb_netmusic;
    private RadioGroup  rb_bottom_group;
    private List<BasePager> basePagers; //存放pager列表
    private int Pos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rb_localvideo = (RadioButton) findViewById(R.id.rb_localvideo);
        rb_localmusic = (RadioButton) findViewById(R.id.rb_localmusic);
        rb_netvideo = (RadioButton) findViewById(R.id.rb_netvideo);
        rb_netmusic = (RadioButton) findViewById(R.id.rb_netmusic);
        rb_bottom_group = (RadioGroup) findViewById(R.id.rb_bottom_group);

        Drawable drawableVideo = getResources().getDrawable(R.drawable.bottom_rb_localvideo_selector);
        drawableVideo.setBounds(0, 0, 80, 80);
        rb_localvideo.setCompoundDrawables(null, drawableVideo, null, null);

        Drawable drawableMusic = getResources().getDrawable(R.drawable.bottom_rb_localmusic_selector);
        drawableMusic.setBounds(0, 0, 80, 80);
        rb_localmusic.setCompoundDrawables(null, drawableMusic, null, null);

        Drawable drawablenVideo = getResources().getDrawable(R.drawable.bottom_rb_netvideo_selector);
        drawablenVideo.setBounds(0, 0, 80, 80);
        rb_netvideo.setCompoundDrawables(null, drawablenVideo, null, null);

        Drawable drawablenMusic = getResources().getDrawable(R.drawable.bottom_rb_netmusic_selector);
        drawablenMusic.setBounds(0, 0, 80, 80);
        rb_netmusic.setCompoundDrawables(null, drawablenMusic, null, null);


        basePagers = new ArrayList<>();
        basePagers.add(new LocalVideoPager(this));
        basePagers.add(new LocalMusicPager(this));
        basePagers.add(new netMusicPager(this));
        basePagers.add(new netVideoPager(this));

        rb_bottom_group.setOnCheckedChangeListener(this);
        rb_bottom_group.check(0);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_localvideo:
                Pos = 0;
                break;
            case R.id.rb_localmusic:
                Pos = 1;
                break;
            case R.id.rb_netvideo:
                Pos = 2;
                break;
            case R.id.rb_netmusic:
                Pos = 3;
                break;
        }
        setFragment();
    }

    /**
     * 用于替换fragment
     */
    private void setFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft =  fragmentManager.beginTransaction();
        ft.replace(R.id.fl_main,new mFragment(getBasePager()));
        ft.commit();
    }

    //从获取相应的pager
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(Pos);

        if(basePager != null && !basePager.isInitData){ //数据没有加载就加载数据。
            basePager.initData();
            basePager.isInitData = true;
        }
        return basePager;
    }


}
