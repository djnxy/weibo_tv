package com.example.xinyu10.newir;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.ircontrol.IrControl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xinyu10 on 2015/7/28.
 */
public class Feed extends ListFragment {

    private String TAG = Feed.class.getName();
    private ListView listView;
    //private ArrayList<String> list = new ArrayList<String>(){{add("快乐大本营晚上有八卦");}};
    private List<Map<String, Object>> listData;
    private SimpleAdapter adapter;

    private int firstVisibleItem = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private long midMax = 0;
    private long midMin = 0;
    private String uid = "1971400745";
    //private String uid = "1401798345";


    private ProgressDialog progressDialog = null;

    OkHttpClient client = new OkHttpClient();

    public void setOnPullToRefreshListener() {

        listView.setOnScrollListener(new ListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int arg0, int arg1, int arg2) {
                firstVisibleItem = arg0;
                visibleItemCount = arg1;
                totalItemCount = arg2;
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });

        final GestureDetector mGestureDetector = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        if (velocityY > 0) {
                            if (firstVisibleItem == 0) {
                                onTop();
                            }
                        }

                        if (velocityY < 0) {
                            int cnt = firstVisibleItem + visibleItemCount;
                            if (cnt == totalItemCount) {
                                onBottom();
                            }
                        }

                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    public void onBottom() {
        onRefresh(true);
        new Thread(runBottom).start();
        adapter.notifyDataSetChanged();

        onRefresh(false);
    }

    public void onTop() {
        onRefresh(true);
        new Thread(runTop).start();
        onRefresh(false);
    }

    public void onRefresh(boolean refreshing) {
        if (refreshing)
            showProgress();
        else
            closeProgress();
    }

    private void showProgress() {
        progressDialog = ProgressDialog.show(getActivity(), "PhilListView",
                "加载中,请稍候...", true, true);
    }

    private void closeProgress() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed, container,false);
        listView = (ListView) view.findViewById(android.R.id.list);
//        Log.i(TAG, "--------onCreateView");
        setOnPullToRefreshListener();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i(TAG, "--------onCreate");
        listData = new ArrayList<Map<String,Object>>();
        //addListData(getFeed());
        adapter = new SimpleAdapter(getActivity(), listData, R.layout.feed_item, new String[]{"title","visibility"}, new int[]{R.id.item_feed,R.id.item_button});
        adapter.setViewBinder(binder);
        setListAdapter(adapter);
        new Thread(runTop).start();

    }
    SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.equals((ImageView) view.findViewById(R.id.item_button))) {
                ImageView item_button = (ImageView)view.findViewById(R.id.item_button);
                if(data != null && data.equals("gone")){
                    item_button.setVisibility(View.GONE);
                }else{
                    item_button.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        }
    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String[] nums = FeedToTV.getTVNum(listData.get(position).get("title").toString());
        if(nums.length > 0){
            if (!IrControl.hasIr()) {
                Toast.makeText(getActivity(), "No IR Emitter found", Toast.LENGTH_LONG).show();
            }else{
                IrControl.sendSerieCode(nums);
            }
        }


//        System.out.println(l.getChildAt(position));
//        HashMap<String, Object> view= (HashMap<String, Object>) l.getItemAtPosition(position);
//        System.out.println(view.get("title").toString()+"+++++++++title");




//        Toast.makeText(getActivity(), TAG + l.getItemIdAtPosition(position), Toast.LENGTH_LONG).show();
//        System.out.println(v);

//        System.out.println(position);


    }




    private List<Map<String, Object>> getData(ArrayList<String> strs) {
        List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();

        for (int i = 0; i < strs.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", strs.get(i));
            list.add(map);

        }

        return list;
    }

    private long getMid(String jsonstr){

        long mid = 0;

        try {
            JSONObject feed = new JSONObject(jsonstr);
                mid = Long.valueOf(feed.getJSONArray("statuses").getJSONObject(0).getString("mid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mid;
    }

    private String getFeed(String jsonstr){

        String feed_content = "";

            try {
                JSONObject feed = new JSONObject(jsonstr);
                    feed_content = feed.getJSONArray("statuses").getJSONObject(0).getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return feed_content;
    }

    Handler handlerTop=new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String feed = getFeed(msg.obj.toString());
            if(!feed.isEmpty()){
                midMax = getMid(msg.obj.toString());
                addListData(0,feed);
                adapter.notifyDataSetChanged();
            }
        }
    };

    Handler handlerBottom=new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String feed = getFeed(msg.obj.toString());
            if(!feed.isEmpty()){
                midMin = getMid(msg.obj.toString()) - 1;
                addListData(feed);
                adapter.notifyDataSetChanged();
            }
        }
    };

    Runnable runTop = new Runnable(){
        @Override
        public void run() {
            Message message=new Message();
            String url = "http://i2.api.weibo.com/2/statuses/user_timeline.json?source=704786492&uid="+uid+"&count=1";
            if(midMax != 0){
                url += "&since_id="+Long.toString(midMax);
            }
            message.obj = getFeedJSON(url);
            handlerTop.sendMessage(message);
        }
    };

    Runnable runBottom = new Runnable(){
        @Override
        public void run() {
            Message message=new Message();
            String url = "http://i2.api.weibo.com/2/statuses/user_timeline.json?source=704786492&uid="+uid+"&count=1";
            if(midMin == 0){
                midMin = midMax-1;
            }
            url += "&max_id="+Long.toString(midMin);
            message.obj = getFeedJSON(url);
            handlerBottom.sendMessage(message);
        }
    };

    public String getFeedJSON(String url){
        String result = "";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            result = response.body().string();
        }catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }

    private void addListData(String str) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", str);
        if(FeedToTV.getTVNum(str).length == 0){
            map.put("visibility","gone");
        }
        listData.add(map);
    }

    private void addListData(int index,String str) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", str);
        if(FeedToTV.getTVNum(str).length == 0){
            map.put("visibility","gone");
        }
        listData.add(index,map);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Log.i(TAG, "--------onActivityCreated");

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        Log.i(TAG, "----------onAttach");
    }
}
