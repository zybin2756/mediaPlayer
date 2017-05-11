package com.example.mediaplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import domain.MediaBean;

/**
 * Created by Administrator on 2017/5/11.
 */

public class localVideoListAdapter extends RecyclerView.Adapter<localVideoListAdapter.ViewHoler> {

    private List<MediaBean> mediaList;
    public localVideoListAdapter(List<MediaBean> mediaList) {
        super();
        this.mediaList = mediaList;
    }

    @Override
    public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHoler holder, int position) {
        
    }

    @Override
    public int getItemCount() {
        return mediaList == null ? 0 : mediaList.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder {

        public ViewHoler(View itemView) {
            super(itemView);
        }
    }
}
