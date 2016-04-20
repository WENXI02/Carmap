package com.example.wenxi.carmap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radar_activity);
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
                                Uri telUri = Uri.parse("tel:"+list.get(i).comments);
                                Intent intent=new Intent(Intent.ACTION_CALL,telUri);
                                startActivity(intent);

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
