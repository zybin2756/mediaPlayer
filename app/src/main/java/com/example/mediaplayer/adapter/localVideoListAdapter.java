package com.example.mediaplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.mListener.mVideoItemClickListener;

import java.util.Formatter;
import java.util.List;

import domain.MediaBean;
import utils.ToolUtils;

import static android.text.format.Formatter.formatFileSize;

/**
 * Created by Administrator on 2017/5/11.
 */

public class LocalVideoListAdapter extends RecyclerView.Adapter<LocalVideoListAdapter.ViewHoler>{

    public mVideoItemClickListener itemClickListener;
    private List<MediaBean> mediaList;
    private Context context;
    public LocalVideoListAdapter(Context context ,List<MediaBean> mediaList,mVideoItemClickListener itemClickListener) {
        super();
        this.mediaList = mediaList;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.localvideoitem,parent,false);
        ViewHoler holder = new ViewHoler(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHoler holder, int position) {
        MediaBean bean = mediaList.get(position);
        holder.tv_video_name.setText(bean.getName());
        holder.tv_video_duration.setText(ToolUtils.timeToString(bean.getDuration()));
        holder.tv_video_size.setText(formatFileSize(this.context,bean.getSize()));

    }

    @Override
    public int getItemCount() {
        return mediaList == null ? 0 : mediaList.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder {
        public TextView tv_video_name;
        public TextView tv_video_duration;
        public TextView tv_video_size;
        public ViewHoler(View itemView) {
            super(itemView);
            tv_video_name = (TextView) itemView.findViewById(R.id.tv_video_name);
            tv_video_duration = (TextView) itemView.findViewById(R.id.tv_video_duration);
            tv_video_size = (TextView) itemView.findViewById(R.id.tv_video_size);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null){
                        itemClickListener.onClick(v,getAdapterPosition());
                    }
                }
            });
        }
    }
}
