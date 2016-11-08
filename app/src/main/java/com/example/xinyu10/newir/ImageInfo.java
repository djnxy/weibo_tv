package com.example.xinyu10.newir;

import java.util.List;
import java.util.Map;

/**
 * Created by xinyu10 on 2016/10/24.
 */
public class ImageInfo {

    private List<Map<String,Object>> imageList;
    private String voicePath;

    public ImageInfo(List<Map<String,Object>> list){
        imageList = list;
        voicePath = "";
    }

    public ImageInfo(List<Map<String,Object>> list,String path){
        imageList = list;
        voicePath = path;
    }

    public List<Map<String,Object>> getImageList(){
        return imageList;
    }

    public String getVoicePath(){
        return voicePath;
    }
}
