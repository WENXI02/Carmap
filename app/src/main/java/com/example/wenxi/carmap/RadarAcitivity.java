package com.example.wenxi.carmap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.example.wenxi.carmap.Utils.leidaUtils;
import com.example.wenxi.carmap.VolleyUtils.QueryUserinfo;
import com.example.wenxi.carmap.VolleyUtils.VolleyPathUtils;
import com.example.wenxi.carmap.VolleyUtils.VolleyUtils;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by wenxi on 16/4/16.
 */
public class RadarAcitivity extends AppCompatActivity{
    private Context mContext;
    private MaterialListView mListView;
    private RadarNearbyResult radarNearbyResult;
    private List<RadarNearbyInfo>list;
    private VolleyPathUtils pathUtils=new VolleyPathUtils();
    private VolleyUtils volleyUtils;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    try {
                        JSONObject object = new JSONObject((String) msg.obj);
                        if (TextUtils.equals(object.getString("Prompt"), "User information are:")) {
                            QueryUserinfo userinfo = new QueryUserinfo();
                            userinfo.setUsername(object.getString("Username"));
                            userinfo.setPassword(object.getString("Password"));
                            userinfo.setTelephone(object.getString("Telephone"));
                            userinfo.setLicense_number(object.getString("License number"));
                            Uri telUri = Uri.parse("tel:"+object.getString("Telephone"));
                            Intent intent=new Intent(Intent.ACTION_CALL,telUri);
                            startActivity(intent);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radar_activity);
        volleyUtils=new VolleyUtils(this,handler);
        mContext = this;
        // Bind the MaterialListView to a variable
        mListView = (MaterialListView) findViewById(R.id.listview_radar);
        mListView.setItemAnimator(new SlideInLeftAnimator());
        mListView.getItemAnimator().setAddDuration(300);
        mListView.getItemAnimator().setRemoveDuration(300);
        radarNearbyResult=leidaUtils.getradalist();
        list=radarNearbyResult.infoList;
        fillArray();
    }
    private void fillArray() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            cards.add(getRandomCard(i,"用户ID:"+list.get(i).userID,"距离："+String.valueOf(list.get(i).distance)+"米"));
        }
        mListView.getAdapter().addAll(cards);
    }
    private Card getRandomCard(final int i, String title, String description) {
        final CardProvider provider = new Card.Builder(this)
                .setTag("BASIC_BUTTONS_CARD")
                .setDismissible()
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_buttons_card)
                .setTitle(title)
                .setDescription(description)
                .addAction(R.id.left_text_button,new TextViewAction(this)
                        .setText("联系车主")
                        .setTextResourceColor(R.color.accent_material_dark)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                try {
//                                    String s=URLEncoder.encode(list.get(i).comments,"utf-8");
//                                    String car_id=URLEncoder.encode(s,"utf-8");
                                    String car_id=list.get(i).comments;
                                    String path=pathUtils.getGetowner_licensepath(car_id);
                                    volleyUtils.getInfo(path,0);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }



                            }
                        })
                )
                .addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText("在地图上显示")
                        .setTextResourceColor(R.color.accent_material_dark)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Intent intent=new Intent();
                                intent.putExtra("latitude",list.get(i).pt.latitude);
                                intent.putExtra("longitude",list.get(i).pt.longitude);
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        }));

            provider.setDividerVisible(true);


        return provider.endConfig().build();

    }
}
