package com.example.xinyu10.newir;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinyu10 on 2015/8/26.
 */
public class TucaoInter extends Fragment {

    SimpleAdapter adapter;
    private List<Map<String, Object>> listData;
    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tucao_inter, container, false);
        listView = (ListView) view.findViewById(R.id.tucao_list);
        listData = new ArrayList<Map<String, Object>>();
        adapter = new SimpleAdapter(getActivity(), listData,
                R.layout.tucao_item, new String[]{"tucao_item"}, new int[]{R.id.tucao_item});
        adapter.setViewBinder(binder);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),String.valueOf(TucaoImages.getAllOnShow((int) listData.get(position).get("tucao_item")).size()), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.getId() == R.id.tucao_item) {
                view.setBackgroundResource((int) data);
                ((ViewGroup)view).removeAllViews();
                List<Map<String,Object>> tags = TucaoImages.getLatestOnShow((int) data);
                for(int i = 0;i<tags.size();i++){
                    //((RelativeLayout.LayoutParams)((PictureTagView)map.get("view")).getLayoutParams()).leftMargin;
                    PictureTagView old_view = (PictureTagView) tags.get(i).get("view");
                    RelativeLayout.LayoutParams new_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    new_params.leftMargin = ((RelativeLayout.LayoutParams)old_view.getLayoutParams()).leftMargin;
                    new_params.topMargin = ((RelativeLayout.LayoutParams)old_view.getLayoutParams()).topMargin;
                    PictureTagView new_view = new PictureTagView(getActivity(), old_view.getDirection());
                    new_view.setShow(old_view.getText());
                    ((ViewGroup)view).addView(new_view,new_params);
                }
            }
            return true;
        }
    };

    public void addItem() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("tucao_item", TucaoImages.resetTucaoImage());
        listData.add(0, map);
        adapter.notifyDataSetChanged();
    }

}