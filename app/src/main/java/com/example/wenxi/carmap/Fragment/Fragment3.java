package com.example.wenxi.carmap.Fragment;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.wear.AndroidWearDatabase;
import com.android.wear.AndroidWearManager;
import com.android.wear.ViewEvent;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.example.bluetooth.library.Bluetoothinit;
import com.example.wenxi.carmap.Fingerprint.DialogFragment_lollipop;
import com.example.wenxi.carmap.MainActivity;
import com.example.wenxi.carmap.Periphery_Activity;
import com.example.wenxi.carmap.R;
import com.example.wenxi.carmap.VolleyUtils.Carinfo;
import com.example.wenxi.carmap.VolleyUtils.VolleyPathUtils;
import com.example.wenxi.carmap.VolleyUtils.VolleyUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


/**
 * Created by wenxi on 16/3/23.
 */
public class Fragment3 extends Fragment {
    private MaterialListView listView;
    private ArrayList<Card> cards=new ArrayList<>();
    private Bluetoothinit bluetoothinit;
    private Handler mhandler;
    public static int receive_data=2;
    public static boolean isuser=false;
    private VolleyUtils volleyUtils;
    private VolleyPathUtils pathUtils=new VolleyPathUtils();
    private boolean isopenandroidwear=false;
    private Handler Volleyhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    Carinfo carinfo=new Carinfo();
                    try{
                        JSONObject object=new JSONObject((String) msg.obj);

                        carinfo.setTemperature(object.getString("temperature"));
                        carinfo.setHumidity(object.getString("humidity"));
                        carinfo.setOilmass(object.getString("oil mass"));
                        String info=carinfo.toString();
                        Toast.makeText(getActivity(),info,Toast.LENGTH_SHORT).show();
                        int c=object.getInt("oil mass");
                        if (c<=24){
                            Log.e("TAG",String.valueOf(c));
                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                            builder.setTitle("当前汽车油量不足")
                                    .setMessage("是否前往附近的加油站？")
                                    .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent();
                                            intent.putExtra("TAG","加油站");
                                            intent.setClass(getActivity(), Periphery_Activity.class);
                                            startActivity(intent);
                                            dialog.dismiss();
                                        }
                                    }).setPositiveButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                            Log.e("TAG",String.valueOf(isopenandroidwear));
                            if (isopenandroidwear) {
                                Intent intent=new Intent();
                                intent.putExtra("TAG","加油站");
                                intent.setClass(getActivity(), Periphery_Activity.class);
                                PendingIntent pendingIntent=PendingIntent.getActivity(getActivity(),0,intent,0);
                                AndroidWearDatabase database = new AndroidWearDatabase();
                                database.setContentTitle("当前汽车油量不足");
                                database.setContentText("是否前往附近的加油站？");
                                database.setDismissAction("前往");
                                database.setImage(R.drawable.car);
                                database.setColor(getResources().getColor(R.color.light_blue));
                                AndroidWearManager androidWearManager= AndroidWearManager.getAndroidWearManager();
                                androidWearManager.upNotification(intent,pendingIntent,getActivity(),database);
                            }
                        }
                    }catch ( Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
                case 1:{
                    try {
                        JSONObject object=new JSONObject((String)msg.obj);
                        String Prompt=object.getString("Prompt");
                        if (TextUtils.equals(Prompt,"Commands sent successfully")){
                            Toast.makeText(getActivity(),"操作汽车成功",Toast.LENGTH_SHORT).show();
                            if (isopenandroidwear) {
                                Intent intent=new Intent();
                                intent.putExtra("TAG","加油站");
                                intent.setClass(getActivity(), ViewEvent.class);
                                PendingIntent pendingIntent=PendingIntent.getActivity(getActivity(),0,intent,0);
                                AndroidWearDatabase database = new AndroidWearDatabase();
                                database.setContentTitle("请注意");
                                database.setContentText("您对您的汽车作了操作");
                                database.setDismissAction("已经阅读");
                                database.setImage(R.drawable.car);
                                database.setColor(getResources().getColor(R.color.light_blue));
                                AndroidWearManager androidWearManager= AndroidWearManager.getAndroidWearManager();
                                androidWearManager.upNotification(intent,pendingIntent,getActivity(),database);
                            }
                        }else {
                            handler.sendMessage(message);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };
    private Message message=new Message();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                String a=(String) msg.obj;
                Log.e("TAG",a);
                bluetoothinit.send(a);
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=(View) inflater.inflate(R.layout.fragment_3,container,false);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("OPENWEAR",getActivity().MODE_PRIVATE);
        isopenandroidwear=sharedPreferences.getBoolean("isopen",false);
        bluetoothinit=isconnet();
        mhandler=bluetoothinit.getMhandler();
        listView=(MaterialListView) v.findViewById(R.id.material_listview_cat);
        listView.setItemAnimator( new SlideInUpAnimator());
        listView.getItemAnimator().setAddDuration(1000);
        listView.getItemAnimator().setRemoveDuration(1000);
        initcard(cards);
        listView.getAdapter().addAll(cards);
        volleyUtils=new VolleyUtils(getActivity(),Volleyhandler);
        return v;
    }

    private List<Card> initcard(List<Card> card){
        card.add(getcard("电源","电源控制指令","关闭电源","开启电源",5));
        card.add(getcard("汽车信息","获取汽车详情信息","获取信息",null,6));
        card.add(getcard("车窗","车窗控制指令","关闭车窗","开启车窗",0));
        card.add(getcard("车门","车门控制指令","关闭车门","开启车门",2));
        card.add(getcard("空调","空调控制指令","关闭空调","开启空调",1));
        card.add(getcard("振动","振动控制指令","关闭振动","开启振动",4));
        card.add(getcard("拍照","拍照控制指令","关闭拍照","开启拍照",3));
        return card;
    }
    private Card getcard(String title, String description, String leftButton, String rightbutton, final int nubmer){
        final CardProvider provider = new Card.Builder(getActivity())
                .setTag("BASIC_BUTTONS_CARD")
                .setDismissible()
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_buttons_card)
                .setTitle(title)
                .setDescription(description);
                if(!TextUtils.isEmpty(leftButton)) {
                    provider.addAction(R.id.left_text_button, new TextViewAction(getActivity())
                            .setText(leftButton)
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    if (isuser) {
                                        switch (nubmer) {

                                            case 0: {
//                                                bluetoothinit.send("CW");
                                                message.what=0;
                                                message.obj="CW";
                                                String path=pathUtils.getSave_path("window","off");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 1: {
                                                //bluetoothinit.send("CA");
                                                message.what=0;
                                                message.obj="CA";
                                                String path=pathUtils.getSave_path("air","off");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 2: {
                                                //bluetoothinit.send("CD");
                                                message.what=0;
                                                message.obj="CD";
                                                String path=pathUtils.getSave_path("door","off");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 3: {
                                                ((MainActivity)(getActivity())).setreceive_code("OC");
                                                //bluetoothinit.send("CC");
                                                message.what=0;
                                                message.obj="CC";
                                                String path=pathUtils.getSave_path("video","off");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 4: {
                                                //bluetoothinit.send("CS");
                                                message.what=0;
                                                message.obj="CS";
                                                String path=pathUtils.getSave_path("alarm","off");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 5: {
                                                //bluetoothinit.send("CP");
                                                message.what=0;
                                                message.obj="CP";
                                                String path=pathUtils.getSave_path("power","off");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 6: {
                                                ((MainActivity)(getActivity())).setreceive_code("OT");
                                                //bluetoothinit.send("OT");
                                                message.what=0;
                                                message.obj="OT";
                                                String path=pathUtils.getGetinfo_path();
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,0);
                                                break;
                                            }
                                        }
                                    }else{
                                        show();
                                    }

                                }
                            }));

                }
                if(!TextUtils.isEmpty(leftButton)) {
                    provider.addAction(R.id.right_text_button, new TextViewAction(getActivity())
                            .setText(rightbutton)
                            .setTextResourceColor(R.color.accent_material_dark)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    if (isuser) {
                                        switch (nubmer) {
                                            case 0: {
                                                //bluetoothinit.send("OW");
                                                message.what=0;
                                                message.obj="OW";
                                                String path=pathUtils.getSave_path("window","on");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 1: {
                                                //bluetoothinit.send("OA");
                                                message.what=0;
                                                message.obj="OA";
                                                String path=pathUtils.getSave_path("air","on");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 2: {
                                                //bluetoothinit.send("OD");
                                                message.what=0;
                                                message.obj="OD";
                                                String path=pathUtils.getSave_path("door","on");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 3: {
                                                //bluetoothinit.send("OC");
                                                message.what=0;
                                                message.obj="OC";
                                                String path=pathUtils.getSave_path("video","on");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 4: {
                                                //bluetoothinit.send("OS");
                                                message.what=0;
                                                message.obj="OS";
                                                String path=pathUtils.getSave_path("alarm","on");
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                            case 5: {
                                                //bluetoothinit.send("OP");
                                                message.what=0;
                                                message.obj="OP";
                                                String path=pathUtils.getSave_path("power","on");
                                                Log.e("TAG",path);
                                                volleyUtils.getInfo(path,1);
                                                break;
                                            }
                                        }
                                    }else{
                                        show();
                                    }
                                }
                            }));
                }

        provider.setDividerVisible(true);
        return provider.endConfig().build();
    }

    /**
     * 用户授权
     */
    public  void show(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("读取或操控汽车需要用户授权").setPositiveButton("授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT>=23) {
                    ((MainActivity) (getActivity())).showFingerprintAuthenticationDialogFragment();
                }else{
                    DialogFragment_lollipop dialogFragment_lollipop=new DialogFragment_lollipop();
                    SharedPreferences Fist_SharedPreferences_lollipop=getActivity().getSharedPreferences("Fist_SharedPreferences_lollipop",getActivity().MODE_PRIVATE);
                    boolean isFist=Fist_SharedPreferences_lollipop.getBoolean("Fist",true);
                    if (isFist){
                        Toast.makeText(getActivity(),"请先到侧面版的指纹设置您的初始密码",Toast.LENGTH_SHORT).show();
                    }else {
                        dialogFragment_lollipop.setisFistconnet(false);
                        dialogFragment_lollipop.setStage(DialogFragment_lollipop.Stage.FINGERPRINTLOLLIPOP);
                        dialogFragment_lollipop.show(getFragmentManager(),"ABC");
                    }
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private Bluetoothinit isconnet(){
       return MainActivity.bluetoothinit;
    }
}
