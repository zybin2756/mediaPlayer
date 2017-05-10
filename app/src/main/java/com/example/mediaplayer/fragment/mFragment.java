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
    public mFragment(BasePager basePager) {
        super();
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
}
