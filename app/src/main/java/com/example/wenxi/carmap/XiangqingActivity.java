package com.example.wenxi.carmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.example.wenxi.carmap.Fingerprint.BaseActivity;
import com.example.wenxi.carmap.Fingerprint.DialogFragment_lollipop;
import com.example.wenxi.carmap.Utils.MapUitls;
import com.example.wenxi.carmap.Utils.PoiDetailResultBeen;
import com.example.wenxi.carmap.Utils.TransitionHelper;
import com.example.wenxi.carmap.Utils.ViewUtils;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by wenxi on 16/4/2.
 */
public class XiangqingActivity extends BaseActivity implements View.OnClickListener {
    boolean showFAB = true;
    private  MapView mapView;
    private View bottomSheet;
    private Animation growAnimation,shrinkAnimation;
    private static PoiDetailResultBeen mPoiDetailResultBeen;
    private  Activity activity;
    private DialogFragment_lollipop dialogFragment_lollipop;
    private  MapUitls uitls=new MapUitls();
    public  Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mapView.setVisibility(View.VISIBLE);
                mapView.startAnimation(growAnimation);
                mapView.showZoomControls(false);
                uitls.setOverlay(mPoiDetailResultBeen.getLocation(),true,mapView);
            }
            super.handleMessage(msg);
        };
    };

    public static Handler Datehandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                mPoiDetailResultBeen=(PoiDetailResultBeen)msg.obj;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        activity=this;
        mapView=(MapView)findViewById(R.id.bmapView1);
        init();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                // 需要做的事:发送消息
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task,2000);
        setdate(mPoiDetailResultBeen);

        initImagbotton();
    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.gmail_toolbar);
        setSupportActionBar(toolbar);
        WindowManager wm = this.getWindowManager();
        int height=wm.getDefaultDisplay().getHeight();
        growAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        shrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_shrink);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.gmail_fab);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(growAnimation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng end= mPoiDetailResultBeen.getLocation();
//                Bundle bundle=performSignInWithTransition(mapView);
                Bundle bundle1=new Bundle();
                bundle1.putDouble("latitude",end.latitude);
                bundle1.putDouble("longitude",end.longitude);
                Intent intent1=new Intent();
                intent1.putExtras(bundle1);
                intent1.setClass(getApplicationContext(),RoutePlanActivity.class);
                startActivity(intent1);
//                ActivityCompat.startActivity(XiangqingActivity.this,intent1,bundle);
            }
        });
        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.gmail_coordinator);
        bottomSheet = coordinatorLayout.findViewById(R.id.gmail_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(height-dip2px(this,380));
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {

                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (showFAB)
                            fab.startAnimation(shrinkAnimation);

                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        showFAB = true;
                        fab.setVisibility(View.VISIBLE);
                        fab.startAnimation(growAnimation);

                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        showFAB = false;
                        break;


                }

            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });


    }

    private void setdate(PoiDetailResultBeen been){
        TextView detailsTittle=(TextView) findViewById(R.id.detailsTittle);
        TextView RatingBartext=(TextView) findViewById(R.id.RatingBartext);
        TextView text_xiangqingaddr=(TextView) findViewById(R.id.text_xiangqingaddr);
        TextView textLocation=(TextView) findViewById(R.id.textLocation);
        TextView text_phone=(TextView) findViewById(R.id.text_phone);
        TextView text_BusinessHours=(TextView) findViewById(R.id.text_BusinessHours);
        TextView text_pice=(TextView) findViewById(R.id.text_pice);
        TextView RatingBar_HygieneRatingtext=(TextView) findViewById(R.id.RatingBar_HygieneRatingtext);
        RatingBar detailsRatingBar=(RatingBar)findViewById(R.id.detailsRatingBar);
        RatingBar detailsRatingBar_HygieneRating=(RatingBar)findViewById(R.id.detailsRatingBar_HygieneRating);
        detailsRatingBar.setNumStars(5);
        detailsRatingBar_HygieneRating.setNumStars(5);
        detailsRatingBar.setRating((float) been.getOverallRating());
        detailsRatingBar_HygieneRating.setRating((float) been.getHygieneRating());
        detailsTittle.setText(been.getName());
        RatingBartext.setText("店铺综合评价:"+String.valueOf((float) been.getOverallRating())+"/5");
        text_xiangqingaddr.setText("地址"+been.getAddress());
        textLocation.setText("地理坐标"+been.getLocation().toString());
        if (!TextUtils.isEmpty(been.getTelephone())) {
            text_phone.setText("电话" + been.getTelephone());
        }else{
            text_phone.setText("电话未知");
        }
        if (!TextUtils.isEmpty(been.getShopHours())) {
            text_BusinessHours.setText("营业时间" + been.getShopHours());
        }else{
            text_BusinessHours.setText("营业时间未知" );
        }
        text_pice.setText("价格"+String.valueOf(been.getPrice())+"元");
        RatingBar_HygieneRatingtext.setText("店铺健康指数:"+String.valueOf((float) been.getHygieneRating())+"/5");

    }

    private void initImagbotton(){
        ImageButton ImageButton_Location=(ImageButton) findViewById(R.id.ImageButton_Location);
        ImageButton ImageButton_call=(ImageButton) findViewById(R.id.ImageButton_call);
        ImageButton ImageButton_buy=(ImageButton) findViewById(R.id.ImageButton_buy);
        ImageButton ImageButton_link=(ImageButton) findViewById(R.id.ImageButton_link);
        ImageButton_Location.setOnClickListener(this);
        ImageButton_call.setOnClickListener(this);
        ImageButton_buy.setOnClickListener(this);
        ImageButton_link.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ImageButton_Location:{

                break;
            }
            case R.id.ImageButton_call:{
                if(!TextUtils.isEmpty(mPoiDetailResultBeen.getTelephone())) {
                    Uri telUri = Uri.parse("tel:" + mPoiDetailResultBeen.getTelephone());
                    Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
                    startActivity(intent);
                }
                break;
            }
            case R.id.ImageButton_buy:{
//                  showFingerprint();
                Snackbar.make(bottomSheet,"在线购物系统暂未开放",Snackbar.LENGTH_LONG).show();
                break;
            }
            case R.id.ImageButton_link:{
                try {
                    Uri uri=Uri.parse(mPoiDetailResultBeen.getDetailUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private  void showFingerprint(){

        if (Build.VERSION.SDK_INT>=23) {
            showFingerprintAuthenticationDialogFragment();
        }else{
            dialogFragment_lollipop=new DialogFragment_lollipop();
            SharedPreferences Fist_SharedPreferences_lollipop=getSharedPreferences("Fist_SharedPreferences_lollipop",MODE_PRIVATE);
            boolean isFist=Fist_SharedPreferences_lollipop.getBoolean("Fist",true);
            if (isFist){
                dialogFragment_lollipop.setStage(DialogFragment_lollipop.Stage.FISIST);
                dialogFragment_lollipop.show(getFragmentManager(),"ABC");
            }else {
                dialogFragment_lollipop.setStage(DialogFragment_lollipop.Stage.FINGERPRINTLOLLIPOP);
                dialogFragment_lollipop.show(getFragmentManager(),"ABC");
            }
        }

    }

    //dp转px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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

    @Override
    public void onPurchased(byte[] signature) {
        Snackbar.make(bottomSheet,"指纹支付成功",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPurchaseFailed() {
        Snackbar.make(bottomSheet,"指纹支付失败",Snackbar.LENGTH_LONG).show();
    }
}
