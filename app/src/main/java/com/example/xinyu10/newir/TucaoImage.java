package com.example.xinyu10.newir;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Bitmap.createBitmap;

/**
 * Created by xinyu10 on 2015/8/24.
 */
public class TucaoImage extends Fragment {

    private View thisView;
    private static final int CLICKRANGE = 5;
    private int startX = 0;
    private int startY = 0;
    private int startTouchViewLeft = 0;
    private int startTouchViewTop = 0;
    private int reviewFlag = 0;
    private View touchView,clickView,tucaoView;

    private List<Map<String,Object>> tagViews;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.img_tag, container, false);
        tucaoView = thisView.findViewById(R.id.img_tag);
        tagViews = new ArrayList<>();
        initTucaoImage();
        return thisView;
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        destroyViews();
    }

    public Bitmap getViewImage(){
        tucaoView.buildDrawingCache();
        return createBitmap(tucaoView.getDrawingCache());
    }

    public void clearTags(){
        tagViews.clear();
    }

    public void destroyViews() {
        ((ViewGroup)tucaoView).removeAllViews();
        ((ViewGroup)thisView).removeAllViews();
    }

    public void initTucaoImage(){
        tucaoView.setBackgroundResource(TucaoImages.getSelected_img());
        tucaoView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchView = null;
                        if (clickView != null) {
                            ((PictureTagView) clickView).setStatus(PictureTagView.Status.Normal);
                            clickView = null;
                        }
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        if (hasView(startX, startY)) {
                            startTouchViewLeft = touchView.getLeft();
                            startTouchViewTop = touchView.getTop();
                        } else {
                            addItem(startX, startY);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveView((int) event.getX(),
                                (int) event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        int endX = (int) event.getX();
                        int endY = (int) event.getY();
                        if (touchView != null && Math.abs(endX - startX) < CLICKRANGE && Math.abs(endY - startY) < CLICKRANGE) {
                            ((PictureTagView) touchView).setStatus(PictureTagView.Status.Edit);
                            clickView = touchView;
                        }
                        touchView = null;
                        break;
                }
                return true;
            }
        });
    }

    private boolean hasView(int x,int y){
        for(int index = 0; index < ((ViewGroup)tucaoView).getChildCount(); index ++){
            View view = ((ViewGroup)tucaoView).getChildAt(index);
                int left = (int) view.getX();
                int top = (int) view.getY();
                int right = view.getRight();
                int bottom = view.getBottom();
                Rect rect = new Rect(left, top, right, bottom);
                boolean contains = rect.contains(x, y);
                if(contains){
                    touchView = view;
                    touchView.bringToFront();
                    return true;
                }
        }
        touchView = null;
        return false;
    }

    private void addItem(int x,int y){
        View view = null;
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(x>tucaoView.getWidth()*0.5){
            params.leftMargin = x - PictureTagView.getViewWidth();
            view = new PictureTagView(getActivity(), PictureTagView.Direction.Right);
        }
        else{
            params.leftMargin = x;

            view = new PictureTagView(getActivity(), PictureTagView.Direction.Left);
        }

        params.topMargin = y;
        if(params.topMargin<0)params.topMargin =0;
        else if((params.topMargin+PictureTagView.getViewHeight())>tucaoView.getHeight())params.topMargin = tucaoView.getHeight() - PictureTagView.getViewHeight();

        Map<String,Object> view_info = new HashMap<>();
        view_info.put("view",view);
        view_info.put("param",params);
        tagViews.add(view_info);
        ((ViewGroup) tucaoView).addView(view, params);
    }

    public List<Map<String,Object>> getTagsInfo(){
        return tagViews;
    }

    private void moveView(int x,int y){
        if(touchView == null) return;
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = x - startX + startTouchViewLeft;
        params.topMargin = y - startY + startTouchViewTop;
        if(params.leftMargin<0||(params.leftMargin+touchView.getWidth())>tucaoView.getWidth())params.leftMargin = touchView.getLeft();
        if(params.topMargin<0||(params.topMargin+touchView.getHeight())>tucaoView.getHeight())params.topMargin = touchView.getTop();
        touchView.setLayoutParams(params);
    }

    public void setReviewFlag(){
        reviewFlag = 1;
    }

    public boolean isFromReview(){
        return reviewFlag == 1;
    }

    public void resetReviewFlag(){
        reviewFlag = 0;
    }
}
