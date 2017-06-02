package com.example.mediaplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mediaplayer.R;
import com.example.mediaplayer.mListener.mVideoItemClickListener;

import org.xutils.x;

import java.util.List;

import domain.NetMediaBean;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class NetVideoListAdapter  extends RecyclerView.Adapter<NetVideoListAdapter.ViewHolder>{

    private Context context;
    private List<NetMediaBean> netMediaList;
    public mVideoItemClickListener itemClickListener;
    public NetVideoListAdapter(Context context, List<NetMediaBean> netMediaList,mVideoItemClickListener itemClickListener)  {
        super();
        this.context = context;
        this.netMediaList = netMediaList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.netvideoitem,parent,false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NetMediaBean netMediaBean = netMediaList.get(position);
        //holder.imgCover.setImageBitmap(netMediaBean.getCoverImg());
        holder.tv_movie_title.setText(netMediaBean.getVideoTitle());
        holder.tv_movie_summary.setText(netMediaBean.getSummary());

        //1.xUtils加载图片
        //x.image().bind(holder.imgCover,netMediaBean.getCoverImg());

        //2.Glide加载图片
        Glide.with(context)
                .load(netMediaBean.getCoverImg())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgCover);
    }

    @Override
    public int getItemCount() {
        return netMediaList==null?0:netMediaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgCover;
        private TextView tv_movie_title;
        private TextView tv_movie_summary;
        public ViewHolder(View itemView) {
            super(itemView);
            imgCover = (ImageView) itemView.findViewById(R.id.img_cover);
            tv_movie_title = (TextView) itemView.findViewById(R.id.tv_movie_title);
            tv_movie_summary = (TextView) itemView.findViewById(R.id.tv_movie_summary);
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
