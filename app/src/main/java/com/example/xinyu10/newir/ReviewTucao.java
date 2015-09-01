package com.example.xinyu10.newir;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
                        if ( endX - startX > MOVEDISTANCE && turnRight()) {
                            displayTags();
                        }else if(startX - endX > MOVEDISTANCE && turnLeft()){
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
    }

    private boolean turnLeft(){
        if(pageIndex > 0){
            pageIndex--;
            return true;
        }else{
            return false;
        }
    }

    private boolean turnRight(){
        if(pageIndex < TucaoImages.getOnShowSize(imageIndex)-1){
            pageIndex++;
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
        pageIndex = TucaoImages.getOnShowSize(tucao_index)-1;
    }

}
