package com.example.wenxi.carmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.wear.AndroidWearManager;
import com.example.wenxi.carmap.Utils.MapUitls;
import com.example.wenxi.carmap.Utils.PoiDetailResultBeen;
import com.example.wenxi.carmap.Utils.TransitionHelper;
import com.example.wenxi.carmap.adapter.Periphery_adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenxi on 16/3/28.
 */
public class Periphery_Activity extends AppCompatActivity {
    private String TAG;
    private  static List<PoiDetailResultBeen>mPoiDetailResultBeen;
    private MapUitls mapUitls;
    public static int OK=2;
    private static RecyclerView recyclerView;
    private static Periphery_adapter adapter;
    private static ProgressBar progressBar;
    private View view;
    public static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==OK){
                 mPoiDetailResultBeen=(List<PoiDetailResultBeen>)msg.obj;
                for (int i=0;i<mPoiDetailResultBeen.size();i++){
                    Log.e("TAG",mPoiDetailResultBeen.get(i).toString());
                }
                adapter.update(mPoiDetailResultBeen);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            };
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periphery);
        AndroidWearManager.getAndroidWearManager().close(this);
        final Intent intent=getIntent();
        TAG=intent.getStringExtra("TAG");
        mapUitls=new MapUitls();
        mapUitls.setKeyword(TAG,1);
        mPoiDetailResultBeen=mapUitls.getPoiDetailResultBeen();
        recyclerView=(RecyclerView) this.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new Periphery_adapter(mPoiDetailResultBeen);
        recyclerView.setAdapter(adapter);
        progressBar=(ProgressBar) findViewById(R.id.indeterminate_horizontal_progress);
        view=findViewById(R.id.linearLayout);

        adapter.setOnItemClickListener(new Periphery_adapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Log.e("TAG",String.valueOf(postion));
                PoiDetailResultBeen been=mPoiDetailResultBeen.get(postion);
                Message message=new Message();
                message.what=0;
                message.obj=been;
                XiangqingActivity.Datehandler.sendMessage(message);
                Bundle bundle=performSignInWithTransition(view);
                Intent intent1=new Intent();
                intent1.setClass(getApplicationContext(),XiangqingActivity.class);
                ActivityCompat.startActivity(Periphery_Activity.this,intent1,bundle);
            }
        });
    }
    private Bundle performSignInWithTransition(View v) {
        final Activity activity = this;

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, true,
                new Pair<>(v, "details"));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, pairs);
        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        return transitionBundle;
    }


}
