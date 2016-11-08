package com.example.xinyu10.newir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xinyu10 on 2015/8/24.
 */
public class TucaoImages {

    static public int[] tucao_imgs;
    static public int[] babaImages = new int[] {R.drawable.test_1,R.drawable.test_2,R.drawable.test_3,R.drawable.test_4,R.drawable.test_5,R.drawable.test_6,R.drawable.test_7,R.drawable.test_8,R.drawable.test_9};
    static public int[] voiceImages = new int[] {R.drawable.test_a,R.drawable.test_b,R.drawable.test_c,R.drawable.test_d,R.drawable.test_e,R.drawable.test_f,R.drawable.test_g,R.drawable.test_h,R.drawable.test_i};


    static public int selected_img = 0;

    static private Map<Integer,List<ImageInfo>> onShowImages;
    static private Map<Integer,List<ImageInfo>> babaShows = new HashMap<>();
    static private Map<Integer,List<ImageInfo>> voiceShows = new HashMap<>();

//    static private Map<Integer,List<List<Map<String,Object>>>> onShowImages;
//    static private Map<Integer,List<List<Map<String,Object>>>> babaShows = new HashMap<>();
//    static private Map<Integer,List<List<Map<String,Object>>>> voiceShows = new HashMap<>();

    static public void resetImagesPool(){
        tucao_imgs = babaImages;
        onShowImages = babaShows;
    }

    static public void setImagesPool(String tv_name){
        if(tv_name == "爸爸去哪儿"){
            tucao_imgs = babaImages;
            onShowImages = babaShows;
        }else if(tv_name == "中国好声音"){
            tucao_imgs = voiceImages;
            onShowImages = voiceShows;
        }else{
            tucao_imgs = babaImages;
            onShowImages = babaShows;
        }
    }

     static public void setSelected_img(int img_index){
          selected_img = img_index;
     }

    static public void setSelected_img_by_value(int value){
        for (int i=0;i<tucao_imgs.length;i++){
            if (tucao_imgs[i] == value){
                selected_img = i;
            }
        }
    }

     static public int getSelected_img(){
          return tucao_imgs[selected_img];
     }

     static public int resetTucaoImage(){
          int res = selected_img;
          selected_img = 0;
          return tucao_imgs[res];
     }

     static public void pushOnShow(int img_index,List<Map<String,Object>> tag_data,String voice_path){
          if(!onShowImages.containsKey(img_index)){
               onShowImages.put(img_index, new ArrayList<ImageInfo>());
          }
          onShowImages.get(img_index).add(new ImageInfo(tag_data,voice_path));
     }

//    static public void pushOnShow(int img_index,List<Map<String,Object>> tag_data){
//        if(!onShowImages.containsKey(img_index)){
//            onShowImages.put(img_index, new ArrayList<List<Map<String, Object>>>());
//        }
//        onShowImages.get(img_index).add(tag_data);
//    }

     static public boolean isOnShow(int img_index){
          return onShowImages.containsKey(img_index);
     }

     static public boolean hasOnShowEmpty(int img_index){
          return onShowImages.isEmpty();
     }

     static public List<Map<String,Object>> getLatestOnShow(int img_index){
        return onShowImages.get(img_index).get(onShowImages.get(img_index).size() - 1).getImageList();
     }

//    static public List<Map<String,Object>> getLatestOnShow(int img_index){
//        return onShowImages.get(img_index).get(onShowImages.get(img_index).size() - 1);
//    }

     static public List<ImageInfo> getAllOnShow(int img_index){
          return onShowImages.get(img_index);
     }

//    static public List<List<Map<String,Object>>> getAllOnShow(int img_index){
//        return onShowImages.get(img_index);
//    }

     static public List<Map<String,Object>> getShowByIndex(int img_index,int index){
          return onShowImages.get(img_index).get(index).getImageList();
     }

    static public String getVoiceByIndex(int img_index,int index){
        return onShowImages.get(img_index).get(index).getVoicePath();
    }

//    static public List<Map<String,Object>> getShowByIndex(int img_index,int index){
//        return onShowImages.get(img_index).get(index);
//    }

     static public int getOnShowSize(int img_index){
          return onShowImages.get(img_index).size();
     }

    static public Set getOnShowImages(){
        return onShowImages.keySet();
    }

}
