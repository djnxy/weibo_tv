package com.example.xinyu10.newir;

/**
 * Created by xinyu10 on 2015/7/31.
 */
public class FeedToTV {
    private FeedToTV(){

    }
    static public String[] getTVNum(String feed){
        int index = feed.indexOf("快乐大本营");
        if(index >= 0){
            return new String[]{"6","3","1"};
        }
        index = feed.indexOf("爸爸去哪");
        if(index >= 0){
            return new String[]{"6","3","1"};
        }
        index = feed.indexOf("中国好声音");
        if(index >= 0){
            return new String[]{"6","3","3"};
        }
        return new String[]{};

    }
}
