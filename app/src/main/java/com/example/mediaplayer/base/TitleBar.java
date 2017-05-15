package com.example.mediaplayer.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mediaplayer.R;

/**
 * Created by Administrator on 2017/5/10.
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {
    private Context context;

    private View tv_search;
    private View img_history;
    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_search = getChildAt(1);
        img_history = getChildAt(2);

        tv_search.setOnClickListener(this);
        img_history.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_history:
                Toast.makeText(context,"img_history",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_search:
                Toast.makeText(context,"tv_search",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
