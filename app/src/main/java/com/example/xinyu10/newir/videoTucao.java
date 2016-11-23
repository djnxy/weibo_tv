package com.example.xinyu10.newir;

import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Created by xinyu10 on 2016/11/14.
 */
public class videoTucao extends Fragment {

    private View thisView;

    private boolean mIsShowDanmaku;
    private DanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private String videoPath;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    public void initVideo(String video_name){
        switch (video_name) {
            case "爸爸去哪儿":
                videoPath = "http://gslb.miaopai.com/stream/ktHk3SUhg-RLW7ez09WD1w__.mp4?yx=&refer=weibo_app";
                break;
            case "中国好声音":
                videoPath = "http://gslb.miaopai.com/stream/XfUq0jk6E73mGVQ0bBEltw__.mp4?yx=&refer=weibo_app";
                break;
            default:
                videoPath = "";
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.video_tucao, container, false);
        VideoView videoView = (VideoView) thisView.findViewById(R.id.video_view);
        //videoView.setVideoPath(Environment.getExternalStorageDirectory() + "/xiaoxingyun.mp4");
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.start();

        mDanmakuView = (DanmakuView) thisView.findViewById(R.id.danmaku_view);
        mDanmakuView.enableDanmakuDrawingCache(true);
        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                mIsShowDanmaku = true;
                mDanmakuView.start();
                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });

        mDanmakuContext = DanmakuContext.create();
        mDanmakuView.prepare(parser, mDanmakuContext);
        return thisView;
    }

    /**
     * 向弹幕View中添加一条弹幕
     * @param content       弹幕的具体内容
     * @param  withBorder   弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(mDanmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        mDanmakuView.addDanmaku(danmaku);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mIsShowDanmaku) {
                    int time = new Random().nextInt(300);
//                    String content = "" + time + time;
                    String content = getRandomDanmu();
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public String getRandomDanmu(){
        String[] danmu = {"好萌~~~~~","可爱！！！","我喜欢~~~","爱上他了。。。。。","萌死了O(∩_∩)O~","收了他","~好羞射~ლ(￣ヘ￣ლ ) ","完全没有抵抗力~╮(╯▽╰)╭","好笑好笑(*≧▽≦)","2333~~~~~~","不能爱你更多~~~~"};
        return danmu[new Random().nextInt(6)];
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsShowDanmaku = false;
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }


}
