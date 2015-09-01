package com.example.xinyu10.newir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinyu10 on 2015/8/24.
 */
public class TucaoImages {

     static public int[] tucao_imgs = new int[] {R.drawable.test_1,R.drawable.test_2,R.drawable.test_3,R.drawable.test_4,R.drawable.test_5,R.drawable.test_6,R.drawable.test_7,R.drawable.test_8,R.drawable.test_9};

     static public int selected_img = 0;

     static private Map<Integer,List<List<Map<String,Object>>>> onShowImages = new HashMap<>();

     static public void setSelected_img(int img_index){
          selected_img = img_index;
     }

     static public int getSelected_img(){
          return tucao_imgs[selected_img];
     }

     static public int resetTucaoImage(){
          int res = selected_img;
          selected_img = 0;
          return tucao_imgs[res];
     }

     static public void pushOnShow(int img_index,List<Map<String,Object>> tag_data){
          if(!onShowImages.containsKey(img_index)){
               onShowImages.put(img_index, new ArrayList<List<Map<String, Object>>>());
          }
          onShowImages.get(img_index).add(tag_data);
     }

     static public boolean isOnShow(int img_index){
          return onShowImages.containsKey(img_index);
     }

     static public boolean hasOnShowEmpty(int img_index){
          return onShowImages.isEmpty();
     }

     static public List<Map<String,Object>> getLatestOnShow(int img_index){
          return onShowImages.get(img_index).get(onShowImages.get(img_index).size() - 1);
     }

     static public List<List<Map<String,Object>>> getAllOnShow(int img_index){
          return onShowImages.get(img_index);
     }

     static public List<Map<String,Object>> getShowByIndex(int img_index,int index){
          return onShowImages.get(img_index).get(index);
     }

     static public int getOnShowSize(int img_index){
          return onShowImages.get(img_index).size();
     }

}
