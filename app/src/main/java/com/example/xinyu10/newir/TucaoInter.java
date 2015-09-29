package com.example.xinyu10.newir;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xinyu10 on 2015/8/26.
 */
public class TucaoInter extends Fragment {

    SimpleAdapter adapter;
    private List<Map<String, Object>> listData;
    private ListView listView;
    private View thisView;
    private Spinner spinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.tucao_inter, container, false);
        listView = (ListView) thisView.findViewById(R.id.tucao_list);
        spinner = (Spinner) thisView.findViewById(R.id.show_selector);
        ArrayAdapter<String> show_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,new String[]{"爸爸去哪儿","中国好声音"});
        show_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(show_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TucaoImages.setImagesPool(((CheckedTextView) view).getText().toString());
                resetItems();
                updateItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listData = new ArrayList<Map<String, Object>>();
        adapter = new SimpleAdapter(getActivity(), listData,
                R.layout.tucao_item, new String[]{"tucao_item"}, new int[]{R.id.tucao_item});
        adapter.setViewBinder(binder);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) getActivity()).reviewTucao((int) listData.get(position).get("tucao_item"));
                //Toast.makeText(getActivity(),String.valueOf(TucaoImages.getAllOnShow((int) listData.get(position).get("tucao_item")).size()), Toast.LENGTH_SHORT).show();
            }
        });

        thisView.findViewById(R.id.tucao_change_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).callTVSender(FeedToTV.getTVNum(spinner.getSelectedItem().toString()));
            }
        });

        return thisView;
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
                    ((ViewGroup)view).addView(new_view, new_params);
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

    public void updateItems(){
        Set items = TucaoImages.getOnShowImages();
        for (Object image: items) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("tucao_item", (int)image);
            listData.add(map);
        }
    }

    public void resetItems(){
        listData.clear();
        adapter.notifyDataSetChanged();
    }
}