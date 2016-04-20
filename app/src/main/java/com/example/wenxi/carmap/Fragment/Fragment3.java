package com.example.wenxi.carmap.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.example.bluetooth.library.Bluetoothinit;
import com.example.wenxi.carmap.Fingerprint.DialogFragment_lollipop;
import com.example.wenxi.carmap.MainActivity;
import com.example.wenxi.carmap.R;

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=(View) inflater.inflate(R.layout.fragment_3,container,false);
        bluetoothinit=isconnet();
        mhandler=bluetoothinit.getMhandler();
        listView=(MaterialListView) v.findViewById(R.id.material_listview_cat);
        listView.setItemAnimator( new SlideInUpAnimator());
        listView.getItemAnimator().setAddDuration(1000);
        listView.getItemAnimator().setRemoveDuration(1000);
        initcard(cards);
        listView.getAdapter().addAll(cards);
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
                                                bluetoothinit.send("CW");
                                                break;
                                            }
                                            case 1: {
                                                bluetoothinit.send("CA");
                                                break;
                                            }
                                            case 2: {
                                                bluetoothinit.send("CD");
                                                break;
                                            }
                                            case 3: {
                                                ((MainActivity)(getActivity())).setreceive_code("OC");
                                                bluetoothinit.send("CC");

                                                break;
                                            }
                                            case 4: {
                                                bluetoothinit.send("CS");
                                                break;
                                            }
                                            case 5: {
                                                bluetoothinit.send("CP");
                                                break;
                                            }
                                            case 6: {
                                                ((MainActivity)(getActivity())).setreceive_code("OT");
                                                bluetoothinit.send("OT");

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
                                                bluetoothinit.send("OW");
                                                break;
                                            }
                                            case 1: {
                                                bluetoothinit.send("OA");
                                                break;
                                            }
                                            case 2: {
                                                bluetoothinit.send("OD");
                                                break;
                                            }
                                            case 3: {
                                                bluetoothinit.send("OC");
                                                break;
                                            }
                                            case 4: {
                                                bluetoothinit.send("OS");
                                                break;
                                            }
                                            case 5: {
                                                bluetoothinit.send("OP");
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
