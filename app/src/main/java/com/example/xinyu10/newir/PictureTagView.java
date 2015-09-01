package com.example.xinyu10.newir;

import android.content.Context;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by xinyu10 on 2015/8/25.
 */
public class PictureTagView extends RelativeLayout implements TextView.OnEditorActionListener {

    private Context context;
    private TextView tvPictureTagLabel;
    private EditText etPictureTagLabel;
    private View loTag;
    public enum Status{Normal,Edit}
    public enum Direction{Left,Right}
    private Direction direction = Direction.Left;
    private InputMethodManager imm;
    private static final int ViewWidth = 80;
    private static final int ViewHeight = 50;

    public PictureTagView(Context context,Direction direction) {
        super(context);
        this.context = context;
        this.direction = direction;
        initViews();
        init();
        initEvents();
    }

    public Direction getDirection(){
        return this.direction;
    }

    public Editable getText(){
        return this.etPictureTagLabel.getText();
    }

    /** 初始化视图 **/
    protected void initViews(){
        LayoutInflater.from(context).inflate(R.layout.picturetagview, this,true);
        tvPictureTagLabel = (TextView) findViewById(R.id.tvPictureTagLabel);
        etPictureTagLabel = (EditText) findViewById(R.id.etPictureTagLabel);
        loTag = findViewById(R.id.loTag);
    }
    /** 初始化 **/
    protected void init(){
        imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        directionChange();
    }

    /** 初始化事件 **/
    protected void initEvents(){
        etPictureTagLabel.setOnEditorActionListener(this);
    }

    public void setShow(Editable text){
        tvPictureTagLabel.setVisibility(View.VISIBLE);
        etPictureTagLabel.clearFocus();
        tvPictureTagLabel.setText(text);
        etPictureTagLabel.setVisibility(View.GONE);
    }

    public void setStatus(Status status){
        switch(status){
            case Normal:
                tvPictureTagLabel.setVisibility(View.VISIBLE);
                etPictureTagLabel.clearFocus();
                tvPictureTagLabel.setText(etPictureTagLabel.getText());
                etPictureTagLabel.setVisibility(View.GONE);
                //隐藏键盘
                imm.hideSoftInputFromWindow(etPictureTagLabel.getWindowToken() , 0);
                break;
            case Edit:
                tvPictureTagLabel.setVisibility(View.GONE);
                etPictureTagLabel.setVisibility(View.VISIBLE);
                etPictureTagLabel.requestFocus();
                //弹出键盘
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                break;
        }
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        setStatus(Status.Normal);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View parent = (View) getParent();
        int halfParentW = (int) (parent.getWidth()*0.5);
        int center = (int) (l + (this.getWidth()*0.5));
        if(center<=halfParentW){
            direction = Direction.Left;
        }
        else{
            direction = Direction.Right;
        }
        directionChange();
    }
    private void directionChange(){
        //            android:drawableLeft="@drawable/tagview_left"
        switch(direction){
            case Left:
                tvPictureTagLabel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tagview_left_head,0,R.drawable.tagview_left_tail,0);
                etPictureTagLabel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tagview_left_head,0,R.drawable.tagview_left_tail,0);
                //loTag.setBackgroundResource(R.drawable.tagview_left);
                break;
            case Right:
                tvPictureTagLabel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tagview_right_tail,0,R.drawable.tagview_right_head,0);
                etPictureTagLabel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tagview_right_tail,0,R.drawable.tagview_right_head,0);
                //loTag.setBackgroundResource(R.drawable.tagview_right);
                break;
        }
    }
    public static int getViewWidth(){
        return ViewWidth;
    }
    public static int getViewHeight(){
        return ViewHeight;
    }

}
