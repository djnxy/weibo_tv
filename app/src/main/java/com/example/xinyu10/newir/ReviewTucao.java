package com.example.xinyu10.newir;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Map;

/**
 * Created by xinyu10 on 2015/9/1.
 */
public class ReviewTucao extends Fragment {
    private View thisView,imageView;
    private int imageIndex;
    private int pageIndex;

    int startX = 0;
    private static final int MOVEDISTANCE = 5;
    private static final int ANIMATIONDURATION = 150;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.review_tucao, container, false);
        imageView = thisView.findViewById(R.id.review_tucao);
        return thisView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        imageView.setBackgroundResource(imageIndex);
        displayTags();
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        int endX = (int) event.getX();
                        if ( endX - startX > MOVEDISTANCE && turnLeft()) {
                            displayTags();
                        }else if(startX - endX > MOVEDISTANCE && turnRight()){
                            displayTags();
                        }
                        break;
                }
                return true;
            }
        });
        //Toast.makeText(getActivity(), String.valueOf(TucaoImages.getAllOnShow(imageIndex).size()), Toast.LENGTH_SHORT).show();
    }

    private void displayTags(){
        ((ViewGroup)imageView).removeAllViews();
        List<Map<String,Object>> tags = TucaoImages.getShowByIndex(imageIndex, pageIndex);
        for(int i = 0;i<tags.size();i++){
            PictureTagView old_view = (PictureTagView) tags.get(i).get("view");
            RelativeLayout.LayoutParams new_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            new_params.leftMargin = ((RelativeLayout.LayoutParams)old_view.getLayoutParams()).leftMargin;
            new_params.topMargin = ((RelativeLayout.LayoutParams)old_view.getLayoutParams()).topMargin;
            PictureTagView new_view = new PictureTagView(getActivity(), old_view.getDirection());
            new_view.setShow(old_view.getText());
            ((ViewGroup)imageView).addView(new_view,new_params);
        }
        if(TucaoImages.getVoiceByIndex(imageIndex, pageIndex) != ""){
            thisView.findViewById(R.id.tucao_play_voice).setVisibility(View.VISIBLE);
            thisView.findViewById(R.id.tucao_play_voice).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VoiceRecorder.play(TucaoImages.getVoiceByIndex(imageIndex, pageIndex));
                    //Toast.makeText(getActivity(), TucaoImages.getVoiceByIndex(imageIndex, pageIndex), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            thisView.findViewById(R.id.tucao_play_voice).setVisibility(View.INVISIBLE);
        }
    }

    private boolean turnLeft(){
        if(pageIndex > 0){
            pageIndex--;
            Animation translateAnimation;
            for(int i=0;i<((ViewGroup)imageView).getChildCount();i++){
                View tag = ((ViewGroup)imageView).getChildAt(i);
                translateAnimation = new TranslateAnimation(0,imageView.getWidth(),0,0);
                translateAnimation.setDuration(ANIMATIONDURATION);
                tag.startAnimation(translateAnimation);
            }
            return true;
        }else{
            return false;
        }
    }

    private boolean turnRight(){
        if(pageIndex < TucaoImages.getOnShowSize(imageIndex)-1){
            pageIndex++;
            Animation translateAnimation;
            for(int i=0;i<((ViewGroup)imageView).getChildCount();i++){
                View tag = ((ViewGroup)imageView).getChildAt(i);
                translateAnimation = new TranslateAnimation(0,-imageView.getWidth(),0,0);
                translateAnimation.setDuration(ANIMATIONDURATION);
                tag.startAnimation(translateAnimation);
            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        destroyViews();
    }

    public void destroyViews() {
        ((ViewGroup)imageView).removeAllViews();
        ((ViewGroup)thisView).removeAllViews();
    }

    public void initTucao(int tucao_index){
        imageIndex = tucao_index;
        pageIndex = 0;
    }

    public void setlatestTucao(){
        pageIndex = TucaoImages.getOnShowSize(imageIndex)-1;
    }

    public int getCurentimage(){
        return imageIndex;
    }

}
