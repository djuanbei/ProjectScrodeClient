package com.example.win10.myapplication;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zhaikun68 on 2018/3/5.
 *
 * <p>
 * <p>
 * ListView演示Demo
 */

public class ListViewDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private String teamnum, result, userid;
    private ListView testLv;//ListView组件
    private Button updateDataBtn;//动态加载数据组件
    private String returnteamname;
    private int returnteamnum, returnteamvote;
    private List<String> dataList = new ArrayList<>();//存储数据
    private VoteInfo voteInfo = new VoteInfo();
    private JSONObject jsonObject;
    private ListViewDemoAdapter listViewDemoAdapter;//ListView的数据适配器
    private int defaultChooses[] = new int[2];


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_demo);
        defaultChooses[0] = -1;
        defaultChooses[1] = -1;
        initView();//初始化组件
        Thread value = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                teamnum = intent.getStringExtra("teamnum");
                userid = intent.getStringExtra("userid");
                voteInfo.setUser(userid);

                try {
                    result = VoteList(Integer.parseInt(teamnum));
                    lastVote(Integer.parseInt(teamnum));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("wxhlbybz", result);
            }
        });
        value.start();
        try {
            value.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化组件
     */

    private void initView() {

        testLv = (ListView) findViewById(R.id.test_lv);

        updateDataBtn = (Button) findViewById(R.id.update_data_btn);


        updateDataBtn.setOnClickListener(this);
        testLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewDemoAdapter.ViewHolder viewHolder = (ListViewDemoAdapter.ViewHolder) view.getTag();
                voteInfo.setUser(userid);
                if (viewHolder.checkBox.isChecked()) {
                    if (voteInfo.getV2teamnumber() != "")
                        voteInfo.setV2teamnumber("");
                    else
                        voteInfo.setV1teamnumber("");
                    viewHolder.checkBox.setChecked(false);
                } else if (voteInfo.getV1teamnumber() != "" && voteInfo.getV2teamnumber() != "") {
                    Toast.makeText(ListViewDemoActivity.this, "您已经投了两票，你乱投，信不信我锤你,请取消一票", Toast.LENGTH_LONG).show();
                    viewHolder.checkBox.setChecked(false);
                } else {
                    viewHolder.checkBox.setChecked(true);
                    Log.e("点击了", viewHolder.contentTv.getText().toString() + " " + voteInfo.getUser());
                    if (voteInfo.getV1teamnumber() == "") {
                        voteInfo.setV1teamnumber(viewHolder.contentTv.getText().toString());
                    } else voteInfo.setV2teamnumber(viewHolder.contentTv.getText().toString());
                }

            }
        });

    }


    /**
     * 初始化数据
     */

    private void initData() throws JSONException {


        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsontemp = jsonArray.getJSONObject(i);
            returnteamname = jsontemp.getString("teamname");
            returnteamnum = jsontemp.getInt("teamnumber");
            returnteamvote = jsontemp.getInt("teamvote");
            dataList.add("第" + returnteamnum + "个队伍，队伍名：" + " " + returnteamname + ",当前票数为" + returnteamvote);
        }


        //设置ListView的适配器

        listViewDemoAdapter = new ListViewDemoAdapter(this, dataList);

        testLv.setAdapter(listViewDemoAdapter);
        testLv.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < defaultChooses.length; i++) {
                    if (defaultChooses[i] > -1) {
                        View view = testLv.getChildAt(defaultChooses[i]-1);
                        ListViewDemoAdapter.ViewHolder viewHolder = (ListViewDemoAdapter.ViewHolder) view.getTag();
                        voteInfo.setUser(userid);
                        if (viewHolder.checkBox.isChecked()) {
                            if (voteInfo.getV2teamnumber() != "")
                                voteInfo.setV2teamnumber("");
                            else
                                voteInfo.setV1teamnumber("");
                            viewHolder.checkBox.setChecked(false);
                        } else if (voteInfo.getV1teamnumber() != "" && voteInfo.getV2teamnumber() != "") {
                            Toast.makeText(ListViewDemoActivity.this, "您已经投了两票，你乱投，信不信我锤你,请取消一票", Toast.LENGTH_LONG).show();
                            viewHolder.checkBox.setChecked(false);
                        } else {
                            viewHolder.checkBox.setChecked(true);
                            Log.e("点击了", viewHolder.contentTv.getText().toString() + " " + voteInfo.getUser());
                            if (voteInfo.getV1teamnumber() == "") {
                                voteInfo.setV1teamnumber(viewHolder.contentTv.getText().toString());
                            } else voteInfo.setV2teamnumber(viewHolder.contentTv.getText().toString());
                        }
                    }

                }

            }
        });


        testLv.setSelection(4);

    }


    @Override

    public void onClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String voteteamnum1 = voteInfo.getV1teamnumber();
                String voteteamnum2 = voteInfo.getV2teamnumber();
                String voteteamname1 = voteInfo.getV1teamnumber();
                String voteteamname2 = voteInfo.getV2teamnumber();
                //这里记得加判断（第二个是否为空）！
                if (voteteamnum1 == "") {
                    int result = 0;
                    try {
                        result = IfDone("", "", voteInfo.getUser());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (result == 1) {
                        Log.e("vote team num1", voteteamnum1);
                        Log.e("vote team num2", voteteamnum2);
                        Log.e("vote team name1", voteteamname1);
                        Log.e("vote team name2", voteteamname2);
                        //跳转到成功页面
                        Intent intent = new Intent(ListViewDemoActivity.this, Finish.class);
                        intent.putExtra("state", "0");
                        startActivity(intent);
                    } else {
                        //跳转到失败页面
                        Intent intent = new Intent(ListViewDemoActivity.this, FailVoted.class);
                        startActivity(intent);
                    }
                } else if (voteteamnum2 == "") {
                    voteteamnum1 = splitData(voteInfo.getV1teamnumber(), "第", "个");
                    voteteamname1 = splitData(voteInfo.getV1teamnumber(), " ", ",");
                    try {
                        int result = IfDone(voteteamnum1, "", voteInfo.getUser());
                        if (result == 1) {

                            //跳转到成功页面
                            Intent intent = new Intent(ListViewDemoActivity.this, Finish.class);
                            intent.putExtra("state", "1");
                            intent.putExtra("voteteamnum1", voteteamnum1);
                            intent.putExtra("voteteamname1", voteteamname1);
                            startActivity(intent);
                        } else {
                            //跳转到失败页面
                            Intent intent = new Intent(ListViewDemoActivity.this, FailVoted.class);
                            startActivity(intent);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        voteteamnum1 = splitData(voteInfo.getV1teamnumber(), "第", "个");
                        voteteamnum2 = splitData(voteInfo.getV2teamnumber(), "第", "个");
                        voteteamname1 = splitData(voteInfo.getV1teamnumber(), " ", ",");
                        voteteamname2 = splitData(voteInfo.getV2teamnumber(), " ", ",");
                        int result = IfDone(voteteamnum1, voteteamnum2, voteInfo.getUser());
                        if (result == 1) {
                            Log.e("vote team num1", voteteamnum1);
                            Log.e("vote team num2", voteteamnum2);
                            Log.e("vote team name1", voteteamname1);
                            Log.e("vote team name2", voteteamname2);
                            //跳转到成功页面
                            Intent intent = new Intent(ListViewDemoActivity.this, Finish.class);
                            intent.putExtra("state", "2");
                            intent.putExtra("voteteamnum1", voteteamnum1);
                            intent.putExtra("voteteamname1", voteteamname1);
                            intent.putExtra("voteteamnum2", voteteamnum2);
                            intent.putExtra("voteteamname2", voteteamname2);
                            startActivity(intent);
                        } else {
                            //跳转到失败页面
                            Intent intent = new Intent(ListViewDemoActivity.this, FailVoted.class);
                            startActivity(intent);
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }).start();
    }


    public String splitData(String str, String strStart, String strEnd) {

        String tempStr;

        tempStr = str.substring(str.indexOf(strStart) + 1, str.lastIndexOf(strEnd));

        return tempStr;

    }

    public int IfDone(String teamnum1, String teamnum2, String userid) throws IOException {
        //提交到指定的php页面
        String urlstr = MainActivity.urlstr + "updateteam.php";
        URL url = new URL(urlstr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        String params = "voteteamnum1=" + teamnum1 + '&' + "voteteamnum2=" + teamnum2 + '&' + "userid=" + userid;
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        OutputStream out = http.getOutputStream();
        out.write(params.getBytes());
        out.flush();
        out.close();
        //接受服务端发来的JSON数据
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));//获得输入流
        String line = "";
        StringBuilder sb = new StringBuilder();//建立输入缓冲区
        while (null != (line = bufferedReader.readLine())) {//结束会读入一个null值
            sb.append(line);//写缓冲区
        }
        String result = sb.toString();
        Log.e("Exception", result);
        int returnResult = 0;
        try {
            /*获取服务器返回的数据*/
            jsonObject = new JSONObject(result);
            returnResult = jsonObject.getInt("status");
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "the Error parsing data " + e.toString());
        }
        return returnResult;
    }

    private String VoteList(int num) throws IOException {
        //提交到指定的php页面
        String urlstr = MainActivity.urlstr + "test.php";
        URL url = new URL(urlstr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        String params = "teamnum=" + num + "&" + "userid=" + userid;
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        OutputStream out = http.getOutputStream();
        out.write(params.getBytes());
        out.flush();
        out.close();
        //接受服务端发来的JSON数据
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));//获得输入流
        String line = "";
        StringBuilder sb = new StringBuilder();//建立输入缓冲区
        while (null != (line = bufferedReader.readLine())) {//结束会读入一个null值
            sb.append(line);//写缓冲区
        }
        String result = sb.toString();
        return result;
    }

    private void lastVote(int num) throws Exception {

        JSONObject jsonObject = new JSONObject(result);

        JSONArray jsonArray = jsonObject.getJSONArray("data");

        Map<Integer, Integer> idsMap = new HashMap<Integer, Integer>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsontemp = jsonArray.getJSONObject(i);
            idsMap.put(jsontemp.getInt("teamnumber"), i);

        }


        //提交到指定的php页面
        String urlstr = MainActivity.urlstr + "lastvote.php";
        URL url = new URL(urlstr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        String params = "teamnum=" + num + "&" + "userid=" + userid;
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        OutputStream out = http.getOutputStream();
        out.write(params.getBytes());
        out.flush();
        out.close();
        //接受服务端发来的JSON数据
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));//获得输入流
        String line = "";
        StringBuilder sb = new StringBuilder();//建立输入缓冲区
        while (null != (line = bufferedReader.readLine())) {//结束会读入一个null值
            sb.append(line);//写缓冲区
        }
        String lastResult = sb.toString();
        try {

            JSONObject jsonObject1 = new JSONObject(lastResult);
            JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
            for (int i = 0; i < jsonArray1.length(); i++) {

                JSONObject jsontemp = jsonArray1.getJSONObject(i);

                int vote1 = -1;
                int vote2 = -1;


                try {

                    vote1 = jsontemp.getInt("vote1");
                } catch (Exception e) {
                    vote1 = -1;

                }

                try {
                    vote2 = jsontemp.getInt("vote2");
                } catch (Exception e) {
                    vote2 = -1;

                }


                if (idsMap.containsKey(vote1)) {
                    defaultChooses[0] = idsMap.get(vote1);
                }
                if (idsMap.containsKey(vote2)) {
                    defaultChooses[1] = idsMap.get(vote2);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)

            return true;//不执行父类点击事件

        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件

    }

}
