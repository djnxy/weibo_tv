package com.example.xinyu10.newir;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ircontrol.IrControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MainActivity extends FragmentActivity implements OnClickListener {

    private Feed feed;
    private Radar radar;
    private RadarIndex radarIndex;
    private TucaoInter tucaoInter;
    private Tucao tucao;
    private TucaoImage tucaoImage;
    private ReviewTucao reviewTucao;

    private RelativeLayout feed_layout;
    private RelativeLayout radar_layout;
    private RelativeLayout tucao_layout;

    private TextView feed_text;
    private TextView radar_text;
    private TextView tucao_text;
    //定义要用的颜色值
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xFF7597B3;
    private int blue =0xFF0AB2FB;
    private int backgray =0xFFC0C0C0;
    //定义FragmentManager对象
    FragmentManager fManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fManager = getFragmentManager();
        initViews();

        IrControl.setInstance(this);
    }


    public void onSendClick_1(View view){
        String[] nums = {"6","3","1"};
        callTVSender(nums);
    }

    public void onTucaoChangeTV(View view){
        String[] nums = {"6","3","1"};
        callTVSender(nums);
    }

    public void onRadarIndexClick(View view){
        FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        if (radar == null) {
            radar = new Radar();
            transaction.add(R.id.content, radar);
        } else {
            transaction.show(radar);
        }
        transaction.commit();
    }

    public void reviewTucao(int tucao_index) {
        FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        if (reviewTucao == null) {
            reviewTucao = new ReviewTucao();
            transaction.add(R.id.content, reviewTucao);
        } else {
            transaction.show(reviewTucao);
        }
        transaction.commit();
        reviewTucao.initTucao(tucao_index);
    }

    public void backFromReviewTucao(View view){
        setChioceItem(2);
    }

    public void finishTucao(View view){
        int tucao_img = TucaoImages.getSelected_img();
        feed.pushFeedWithImage("爸爸去哪",tucaoImage.getViewImage());
        tucaoImage.destroyViews();
        List<Map<String,Object>> list = new ArrayList<>(tucaoImage.getTagsInfo().size());
        for (Map<String,Object> map:tucaoImage.getTagsInfo()){
            list.add(map);
        }
        tucaoImage.clearTags();
        if(TucaoImages.isOnShow(tucao_img)){
            TucaoImages.pushOnShow(tucao_img,list);
        }else{
            TucaoImages.pushOnShow(tucao_img,list);
            tucaoInter.addItem();
        }
        if(tucaoImage.isFromReview()){
            tucaoImage.resetReviewFlag();
            reviewTucao(tucao_img);
            reviewTucao.setlatestTucao();
        }else{
            setChioceItem(2);
        }
    }

    public void jumpToTucao(View view){
        setChioceItem(2);
    }

    public void onTucaoButtonClick(View view){
        FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        if (tucao == null) {
            tucao = new Tucao();
            transaction.add(R.id.content, tucao);
        } else {
            tucao.resetItems();
            transaction.show(tucao);
        }
        transaction.commit();
    }

    public void selectTucaoImage(){
        FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        if (tucaoImage == null) {
            tucaoImage = new TucaoImage();
            transaction.add(R.id.content, tucaoImage);
        } else {
            tucaoImage.initTucaoImage();
            transaction.show(tucaoImage);
        }
        transaction.commit();
    }

    public void tucaoReviewButton(View view){
        FragmentTransaction transaction = fManager.beginTransaction();
        TucaoImages.setSelected_img_by_value(reviewTucao.getCurentimage());
        hideFragments(transaction);
        if (tucaoImage == null) {
            tucaoImage = new TucaoImage();
            transaction.add(R.id.content, tucaoImage);
        } else {
            tucaoImage.initTucaoImage();
            transaction.show(tucaoImage);
        }
        tucaoImage.setReviewFlag();
        transaction.commit();
    }

    public void callTVSender(String[] nums){
        if (!IrControl.hasIr()) {
            Toast.makeText(this.getApplicationContext(), "No IR Emitter found", Toast.LENGTH_LONG).show();
        }else{
            IrControl.sendSerieCode(nums);
        }
    }

    protected void msgBox(String msg){
        Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    protected void msgBox(int[] array){
        Toast.makeText(this.getApplicationContext(), Arrays.toString(array), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //完成组件的初始化
    public void initViews()
    {
        feed_text = (TextView) findViewById(R.id.feed_text);
        radar_text = (TextView) findViewById(R.id.radar_text);
        tucao_text = (TextView) findViewById(R.id.tucao_text);
        feed_layout = (RelativeLayout) findViewById(R.id.feed_layout);
        radar_layout = (RelativeLayout) findViewById(R.id.radar_layout);
        tucao_layout = (RelativeLayout) findViewById(R.id.tucao_layout);
        feed_layout.setOnClickListener(this);
        radar_layout.setOnClickListener(this);
        tucao_layout.setOnClickListener(this);

        setChioceItem(0);
    }

    //重写onClick事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feed_layout:
                setChioceItem(0);
                break;
            case R.id.radar_layout:
                setChioceItem(1);
                break;
            case R.id.tucao_layout:
                setChioceItem(2);
                break;
            default:
                break;
        }

    }


    public void setChioceItem(int index)
    {
        FragmentTransaction transaction = fManager.beginTransaction();
        clearChioce();
        hideFragments(transaction);
        switch (index) {
            case 0:
                feed_text.setTextColor(blue);
                feed_layout.setBackgroundColor(whirt);
                if (feed == null) {
                    feed = new Feed();
                    transaction.add(R.id.content, feed);
                } else {
                    transaction.show(feed);
                }
                break;

            case 1:
                radar_text.setTextColor(blue);
                //radar_layout.setBackgroundResource(R.color.abc_background_cache_hint_selector_material_dark);
                radar_layout.setBackgroundColor(whirt);
                if (radarIndex == null) {
                    // 如果feed为空，则创建一个并添加到界面上
                    radarIndex = new RadarIndex();
                    transaction.add(R.id.content, radarIndex);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(radarIndex);
                }
                break;
            case 2:
                tucao_text.setTextColor(blue);
                tucao_layout.setBackgroundColor(whirt);
                if (tucaoInter == null) {
                    // 如果feed为空，则创建一个并添加到界面上
                    tucaoInter = new TucaoInter();
                    TucaoImages.resetImagesPool();
                    transaction.add(R.id.content, tucaoInter);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(tucaoInter);
                }
                break;
        }
        transaction.commit();
    }

    //隐藏所有的Fragment,避免fragment混乱
    private void hideFragments(FragmentTransaction transaction) {
        if (feed != null) {
            transaction.hide(feed);
        }
        if (radar != null) {
            transaction.hide(radar);
        }
        if (tucaoImage != null) {
            transaction.hide(tucaoImage);
        }
        if (radarIndex != null) {
            transaction.hide(radarIndex);
        }
        if (tucaoInter != null) {
            transaction.hide(tucaoInter);
        }
        if (tucao != null) {
            transaction.hide(tucao);
        }
        if(tucaoImage != null) {
            transaction.remove(tucaoImage);
            tucaoImage = null;
        }
        if(reviewTucao != null){
            transaction.remove(reviewTucao);
            reviewTucao = null;
        }
    }


    //定义一个重置所有选项的方法
    public void clearChioce() {
        feed_layout.setBackgroundColor(backgray);
        feed_text.setTextColor(gray);
        radar_layout.setBackgroundColor(backgray);
        radar_text.setTextColor(gray);
        tucao_layout.setBackgroundColor(backgray);
        tucao_text.setTextColor(gray);
    }
}
