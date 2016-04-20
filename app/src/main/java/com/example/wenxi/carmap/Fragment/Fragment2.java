package com.example.wenxi.carmap.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.MapStatus;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.example.wenxi.carmap.Periphery_Activity;
import com.example.wenxi.carmap.R;
import com.example.wenxi.carmap.Utils.MapUitls;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by wenxi on 16/3/23.
 */
public class Fragment2 extends Fragment {
    private MaterialListView listView;
    private ArrayList<Card>cards=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=(View) inflater.inflate(R.layout.fragment_2,container,false);
        listView=(MaterialListView) v.findViewById(R.id.material_listview);
        listView.setItemAnimator( new SlideInUpAnimator());
        listView.getItemAnimator().setAddDuration(1000);
        listView.getItemAnimator().setRemoveDuration(1000);
        listView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        initcard(cards);
        listView.getAdapter().addAll(cards);
        listView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {
                Log.e("CARD_TYPE", card.getTag().toString());
                Intent intent=new Intent();
                intent.putExtra("TAG",card.getTag().toString());

                intent.setClass(getActivity(), Periphery_Activity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(@NonNull Card card, int position) {
                Log.e("CARD_TYPE", card.getTag().toString());

            }
        });
        return v;
    }

    private List<Card> initcard(List<Card> card){
        card.add(getcard(getActivity(),"美食","发现周边美食",R.drawable.food1,"美食"));
        card.add(getcard(getActivity(),"酒店","发现周边酒店",R.drawable.jiudian,"酒店"));
        card.add(getcard(getActivity(),"旅行","城市景点",R.drawable.luyou,"旅行"));
        card.add(getcard(getActivity(),"休闲娱乐","周边娱乐场所",R.drawable.yule,"休闲娱乐"));
        card.add(getcard(getActivity(),"医院","医院",R.drawable.food1,"医院"));
        card.add(getcard(getActivity(),"商业街","城市商业街",R.drawable.shangye,"商业街"));
        card.add(getcard(getActivity(),"银行","银行",R.drawable.yinhang,"银行"));
        return card;
    }



    private Card getcard(Context context,String title,String description,int photo,String tag){
        final CardProvider provider = new Card.Builder(context)
                .setTag(tag)//"BIG_IMAGE_BUTTONS_CARD"
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_image_with_buttons_card)
                .setTitle(title)
                .setTitleResourceColor(R.color.success_color )
                .setDescription(description)
                .setDrawable(photo);
            provider.setDividerVisible(false);


        return provider.endConfig().build();
    }

}
