package com.example.xinyu10.newir;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xinyu10 on 2015/8/13.
 */
public class Tucao extends Fragment {

    //private int[] tucao_imgs = new int[] {R.drawable.test_1,R.drawable.test_2,R.drawable.test_3,R.drawable.test_4,R.drawable.test_5,R.drawable.test_6,R.drawable.test_7,R.drawable.test_8,R.drawable.test_9};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tucao, container,false);

        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < TucaoImages.tucao_imgs.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("tucao_img", TucaoImages.tucao_imgs[i]);
            data.add(map);
        }
        ListView img_list = (ListView) view.findViewById(R.id.tucao_imgs);
        img_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                TucaoImages.setSelected_img(position);
                ((MainActivity)getActivity()).selectTucaoImage();
            }
        });
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
                R.layout.tucao_img, new String[]{"tucao_img"}, new int[]{R.id.tucao_img});
        img_list.setAdapter(adapter);

        return view;
    }


}
