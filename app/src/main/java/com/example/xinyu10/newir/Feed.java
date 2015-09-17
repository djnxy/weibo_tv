package com.example.xinyu10.newir;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.ircontrol.IrControl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;


/**
 * Created by xinyu10 on 2015/7/28.
 */
public class Feed extends ListFragment {

    private String TAG = Feed.class.getName();
    private ListView listView;
    //private ArrayList<String> list = new ArrayList<String>(){{add("快乐大本营晚上有八卦");}};
    private List<Map<String, Object>> listData;
    private SimpleAdapter adapter;
    private EditText editText;

    private int firstVisibleItem = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private long midMax = 0;
    private long midMin = 0;
    private String uid = "5166215594";
    //private String uid = "1401798345";

    private RelativeLayout feed_pusher;
    private OAuthConsumer consumer;
    private OAuthProvider provider;
    private String pin = "";
    private WebView webView;


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
        editText = (EditText)view.findViewById(R.id.edit_feed);
        Button push_feed = (Button)view.findViewById(R.id.push_feed);
        push_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(runPushfeed).start();
            }
        });
//        webView = (WebView)view.findViewById(R.id.weibo_login);
//        feed_pusher = (RelativeLayout)view.findViewById(R.id.feed_pusher);
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setSaveFormData(false);
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(false);
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (url.startsWith("myurl://finish")){
//                    int start = url.indexOf("oauth_verifier");
//                    Log.i("testa",url);
//                    pin = url.substring(start + "oauth_verifier".length() + 1);
//                    try{
//                        new Thread(setAccessTokenThread).start();
//                    }catch (Exception e){
//                        Log.i("testa",e.getMessage());
//                    }
//                }
//                else{
//                    view.loadUrl(url);
//                }
//                return true;
//            }
//        });
//        new Thread(weibo_login_thread).start();
        return view;
    }

    Runnable setAccessTokenThread = new Runnable(){
        @Override
        public void run() {
            try{
                provider.setOAuth10a(true);
                provider.retrieveAccessToken(consumer, pin);
                consumer.setTokenWithSecret(consumer.getToken(), consumer.getTokenSecret());
                Message message=new Message();
                setAccessTokenHandler.sendMessage(message);
            }catch (Exception e){
                Log.i("testa",e.getMessage());
            }
        }
    };

    Handler setAccessTokenHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0));
            webView.setVisibility(View.INVISIBLE);
            feed_pusher.setVisibility(View.VISIBLE);
        }
    };

    Runnable weibo_login_thread = new Runnable(){
        @Override
        public void run() {
            try{
                consumer = new DefaultOAuthConsumer(
                        "704786492",
                        "0b27fa2afa9f3964c25d3990b94b24a7");

                provider = new DefaultOAuthProvider(
                        "http://api.t.sina.com.cn/oauth/request_token",
                        "http://api.t.sina.com.cn/oauth/access_token",
                        "http://api.t.sina.com.cn/oauth/authorize");

                String authUrl = provider.retrieveRequestToken(consumer, "myurl://finish");
                Message message=new Message();
                message.obj = authUrl;
                weibo_login_handler.sendMessage(message);

            } catch (Exception e){
                Log.i("testa",e.getMessage());
            }
        }
    };

    Handler weibo_login_handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            webView.loadUrl(msg.obj.toString());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i(TAG, "--------onCreate");
        listData = new ArrayList<Map<String,Object>>();
        //addListData(getFeed());
        adapter = new SimpleAdapter(getActivity(), listData, R.layout.feed_item, new String[]{"title","visibility","image"}, new int[]{R.id.item_feed,R.id.item_button,R.id.item_feed_image});
        adapter.setViewBinder(binder);
        setListAdapter(adapter);
        new Thread(runTop).start();

    }
    SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            boolean res;

            switch (view.getId()){
                case R.id.item_button:
                    View tucao_button = null;
                    ViewGroup item_view = (ViewGroup) view.getParent();
                    for (int i = 0; i < item_view.getChildCount(); i++) {
                        if(item_view.getChildAt(i).getId() == R.id.item_other){
                            tucao_button = item_view.getChildAt(i);
                        }
                    }
                    if(data != null && data.equals("gone")){
                        view.setVisibility(View.GONE);
                        tucao_button.setVisibility(View.GONE);
//                        RelativeLayout.LayoutParams footer_Params = (RelativeLayout.LayoutParams)footer.getLayoutParams();
//                        footer_Params.addRule(RelativeLayout.BELOW, R.id.item_feed_image);
//                        footer.setLayoutParams(footer_Params);
                    }else{
                        view.setVisibility(View.VISIBLE);
                        tucao_button.setVisibility(View.VISIBLE);
//                        RelativeLayout.LayoutParams footer_Params = (RelativeLayout.LayoutParams)footer.getLayoutParams();
//                        footer_Params.addRule(RelativeLayout.BELOW, R.id.item_button);
//                        footer.setLayoutParams(footer_Params);
                    }
                    res = true;
                    break;
                case R.id.item_feed_image:
                    if (data != null && !data.toString().isEmpty()){
                        new DownloadImageTask((ImageView)view)
                                .execute(data.toString());
                        RelativeLayout.LayoutParams  image_params = (RelativeLayout.LayoutParams)view.getLayoutParams();
                        image_params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        view.setLayoutParams(image_params);
                    }else{
                        ((ImageView)view).setImageResource(0);
                        RelativeLayout.LayoutParams  image_params = (RelativeLayout.LayoutParams)view.getLayoutParams();
                        image_params.height = 0;
                        view.setLayoutParams(image_params);
                    }
                    res = true;
                    break;
                default:
                    res = false;
                    break;
            }
            return res;
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

    private String getPic(String jsonstr){

        String pic = "";

        try {
            JSONObject feed = new JSONObject(jsonstr);
            pic = feed.getJSONArray("statuses").getJSONObject(0).getString("original_pic");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pic;
    }

    Handler pushfeed = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            editText.getText().clear();
            Log.i("testa", String.valueOf(msg.obj.toString()));
        }
    };

    Runnable runPushfeed = new Runnable(){
        @Override
        public void run() {
            Message message=new Message();
            postFeed(editText.getText().toString());
            message.obj = editText.getText().toString();
            pushfeed.sendMessage(message);
        }
    };

    public void pushFeedWithImage(String feed,Bitmap image){
        new Thread(new pushFeedWithImageTask(feed,image)).start();
    }

    Handler handlerTop=new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String feed = getFeed(msg.obj.toString());
            String image = getPic(msg.obj.toString());
            if(!feed.isEmpty()){
                midMax = getMid(msg.obj.toString());
                addListData(0,feed,image);
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
            String image = getPic(msg.obj.toString());
            if(!feed.isEmpty()){
                midMin = getMid(msg.obj.toString()) - 1;
                addListData(feed,image);
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

    public void postFeed(String feed){

//        try{
////            byte[] data = URLEncoder.encode("status="+feed,"UTF-8").getBytes();
////            URL url = new URL("https://api.weibo.com/2/statuses/update.json");
////            HttpURLConnection request = (HttpURLConnection) url.openConnection();
////            request.setConnectTimeout(3000);
////            request.setDoInput(true);
////            request.setDoOutput(true);
////            request.setRequestMethod("POST");
////            request.setUseCaches(false);
////            //设置请求体的类型是文本类型
////            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
////            //设置请求体的长度
////            request.setRequestProperty("Content-Length", String.valueOf(data.length));
////            //获得输出流，向服务器写入数据
////            OutputStream outputStream = request.getOutputStream();
////            outputStream.write(data);
//
//
//            URL url = new URL("https://api.weibo.com/2/statuses/user_timeline.json");
//            HttpURLConnection request = (HttpURLConnection) url.openConnection();
//            consumer.sign(request);
//            request.connect();
//
//            Log.i("testa", request.getResponseMessage());
//
//            return;
//        }catch (Exception e){
//        }




        String result = "";
        String url = "http://10.210.128.29:8080/zhishu/feed?uid="+uid;
//        byte[] sign = new byte[]{};
//        try{
//            String type = "HmacSHA1";
//            SecretKeySpec secret = new SecretKeySpec("d3b7fd5331802e0010a9".getBytes(), type);
//            Mac mac = Mac.getInstance(type);
//            mac.init(secret);
//            sign = mac.doFinal(("uid=" + uid).getBytes());
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//        String ip = "";
//        try{
//            WifiManager wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
//            InetAddress myaddr = InetAddress.getByAddress(BigInteger.valueOf(wm.getConnectionInfo().getIpAddress()).toByteArray());
//            ip = myaddr.getHostAddress();
//            Log.i("testa",ip);
//        }catch(Exception e){
//            e.printStackTrace();
//        }

        try {
//            Request request_a = new Request.Builder().url("http://i.api.weibo.com/tauth2/access_token.json?source=704786492"
//                        +"&app_secret=0b27fa2afa9f3964c25d3990b94b24a7&ips=10.75,10.210,10.73,103.58")
//                    .header("API-RemoteIP", ip)
//                    .build();
//            Request request_a = new Request.Builder().url("http://i2.api.weibo.com/2/statuses/public_timeline.json?source=704786492&count=1")
//                    .build();
//
//            Response response_a = client.newCall(request_a).execute();
//            result = response_a.body().string();
//            Log.i("testa",result);

            RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "status=" + URLEncoder.encode(feed, "UTF-8"));
//            Request request = new Request.Builder()
//                    .url(url)
//                    .header("Authorization", "TAuth2 token=\""
//                            + URLEncoder.encode("OXUNRUVTRWPXNXONYUS=ONYPON=ONYUQXORRPOROPNQXNXNX+c+b5nNi@f", "UTF-8")
//                            + "\",sign=\"" + URLEncoder.encode(Base64.encode(sign,Base64.DEFAULT).toString(),"UTF-8")
//                            +"\",param=\""+URLEncoder.encode("uid="+uid,"UTF-8")+"\"")
//                    .post(body)
//                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .header("HOST","tv.weibo.com")
                    .post(body)
                    .build();

//            client.setAuthenticator(new Authenticator() {
//                @Override
//                public Request authenticate(Proxy proxy, Response response) throws IOException {
//                    String credential = Credentials.basic("superxinyu@gmail.com", "1234qwer");
//                    return response.request().newBuilder().header("Authorization", credential).build();
//                }
//
//                @Override
//                public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
//                    return null;
//                }
//            });
            Response response = client.newCall(request).execute();
            result = response.body().string();
            Log.i("testa",result);
            new Thread(runTop).start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void postFeedWithImage(String feed,Bitmap image){
        String result = "";
        String url = "http://10.210.128.29:8080/zhishu/feed?uid="+uid;
        try {
            Bitmap photo = image;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] photodata = stream.toByteArray();
            String encodedImage = Base64.encodeToString(photodata, Base64.DEFAULT);
            RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "status=" + URLEncoder.encode(feed, "UTF-8")+"&pic="+URLEncoder.encode(encodedImage,"UTF-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .header("HOST","tv.weibo.com")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
            image.recycle();
            Log.i("testa",result);
            new Thread(runTop).start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

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

    private void addListData(String str,String image) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", str);
        if (!image.isEmpty()){
            map.put("image",image);
        }
        if(FeedToTV.getTVNum(str).length == 0){
            map.put("visibility","gone");
        }
        listData.add(map);
    }

    private void addListData(int index,String str,String image) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", str);
        if (!image.isEmpty()){
            map.put("image",image);
        }
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public class pushFeedWithImageTask implements Runnable
    {
        private final String text;
        private final Bitmap bitmap;

        public pushFeedWithImageTask(String feed,Bitmap image)
        {
            text = feed;
            bitmap = image;
        }

        @Override
        public void run() {
            postFeedWithImage(text,bitmap);
        }
    }
}
