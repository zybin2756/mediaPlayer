package com.example.mediaplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mediaplayer.base.BasePager;

/**
 * Created by Administrator on 2017/5/9.
 */

public class mFragment extends Fragment {

    BasePager basePager;

    public mFragment() {
        super();
    }

    public static mFragment newInstance(BasePager basePager){
        mFragment fragment = new mFragment();
        fragment.setBasePager(basePager);
        return fragment;
    }

    public void setBasePager(BasePager basePager){
        this.basePager = basePager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this.basePager != null){
            return this.basePager.rootView;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        this.basePager = null;
        super.onDestroy();
    }
}
