package com.example.wenxi.carmap.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.wenxi.carmap.R;
import com.example.wenxi.carmap.Utils.PoiDetailResultBeen;

import java.util.List;

/**
 * Created by wenxi on 16/4/2.
 */
public class Periphery_adapter extends RecyclerView.Adapter<Periphery_adapter.MyViewHolder> {

    private MyItemClickListener mItemClickListener;
    private  List<PoiDetailResultBeen> mPoiDetailResultBeen;
    public Periphery_adapter(List<PoiDetailResultBeen> PoiDetailResultBeen){
        this.mPoiDetailResultBeen=PoiDetailResultBeen;
    }


    @Override
    public int getItemCount() {
        return mPoiDetailResultBeen.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
     holder.Periphery_title.setText(mPoiDetailResultBeen.get(position).getName());
     holder.Periphery_RatingBar.setNumStars(5);
     holder.Periphery_RatingBar.setRating((float) mPoiDetailResultBeen.get(position).getOverallRating());
     holder.text_addr.setText(mPoiDetailResultBeen.get(position).getAddress());
     holder.RatingBar_text.setText("店铺综合评价:"+ String.valueOf((float) mPoiDetailResultBeen.get(position).getOverallRating())+"/5");

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.periphery_details
                         ,parent,false);
        MyViewHolder viewHolder=new MyViewHolder(view,this.mItemClickListener);

        return viewHolder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private MyItemClickListener mListener;
        TextView Periphery_title,RatingBar_text,text_addr;
        RatingBar Periphery_RatingBar;
        public MyViewHolder(View view,MyItemClickListener listener)
        {
            super(view);
            view.setOnClickListener(this);
            mListener=listener;
            Periphery_title=(TextView) view.findViewById(R.id.Periphery_title);
            RatingBar_text=(TextView) view.findViewById(R.id.RatingBar_text);
            text_addr=(TextView) view.findViewById(R.id.text_addr);
            Periphery_RatingBar=(RatingBar) view.findViewById(R.id.Periphery_RatingBar);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v,getPosition());
        }
    }
    public interface MyItemClickListener {
        public void onItemClick(View view,int postion);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void update(List<PoiDetailResultBeen> PoiDetailResultBeen){
        this.mPoiDetailResultBeen=PoiDetailResultBeen;
    }
}
