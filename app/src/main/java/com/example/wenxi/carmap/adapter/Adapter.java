package com.example.wenxi.carmap.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wenxi.carmap.R;

import java.util.List;

/**
 * Created by wenxi on 16/3/27.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHodler>{

    @Override
    public void onBindViewHolder(ViewHodler holder, int position) {
        holder.textView.setText("OK"+position);
        setcolor(((BitmapDrawable)(holder.imageView.getDrawable())).getBitmap(),holder.textView);
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.peripheralitem,
                parent,false);
        ViewHodler viewHodler=new ViewHodler(view);
        return viewHodler;
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public class ViewHodler extends RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        public ViewHodler(View view){
            super(view);
            imageView=(ImageView) view.findViewById(R.id.peripheral_image);
             textView=(TextView) view.findViewById(R.id.keyword);
        }
    }

    private void setcolor(Bitmap bitmap, final TextView textView){
        Palette.Builder builder= Palette.from(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                textView.setBackgroundColor(palette.getLightVibrantColor(palette.getLightMutedSwatch().getRgb()));
                textView.setTextColor(palette.getVibrantSwatch().getTitleTextColor());
            }
        });
    }
}
