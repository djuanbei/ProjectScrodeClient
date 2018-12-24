package com.example.win10.myapplication;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private JSONObject jsonObject1, jsonObject2;
    private int flag;
    private String vote1, vote2, teamnum, userid;
    private EditText et, pwd;
    public static final String urlstr = "http://47.95.215.87:8080/project/junbabe/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et = findViewById(R.id.email);
        pwd = findViewById(R.id.password);
        final Button login = findViewById(R.id.sign_in_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int result = Login();
                            if (result == 1) {
                                flag = jsonObject2.getInt("flag");
                                userid = jsonObject2.getString("userid");
                                vote1 = jsonObject2.getString("vote1");
                                vote2 = jsonObject2.getString("vote2");
                                teamnum = jsonObject2.getString("teamnum");
                                if (flag == 0) {
                                    Log.e("log_tag", "登陆成功");
                                    Intent intent = new Intent(MainActivity.this, ListViewDemoActivity.class);
                                    intent.putExtra("teamnum", teamnum);
                                    intent.putExtra("userid", userid);
                                    startActivity(intent);
                                    Looper.prepare();
                                    ;
                                    Toast.makeText(MainActivity.this, "登陆成功,欢迎你呀用户：" + userid + "," + "希望你能投吴俊庭大宝贝一票哟", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } else {
                                    Looper.prepare();
                                    ;
                                    Toast.makeText(MainActivity.this, "我记得你已经投过票了啾咪,你投的是" + vote1 + "队，" + vote2 + "队，现在撤销了，你可以重新投", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(MainActivity.this, ListViewDemoActivity.class);
                                    intent.putExtra("teamnum", teamnum);
                                    intent.putExtra("userid", userid);
                                    startActivity(intent);
                                    Looper.loop();
                                }

                            } else if (result == -2) {
                                Log.e("log_tag", "密码错误");
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else if (result == -1) {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "用户名错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (IOException | JSONException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                }).start();
            }
        });
    }

    private int Login() throws IOException {
        int returnResult = 0;
        String user_id = et.getText().toString();
        String input_pwd = pwd.getText().toString();
        if (user_id == null || user_id.length() <= 0) {
            Looper.prepare();
            Toast.makeText(MainActivity.this, "请输入账号", Toast.LENGTH_LONG).show();
            Looper.loop();
            return 0;
        }
        if (input_pwd == null || input_pwd.length() <= 0) {
            Looper.prepare();
            Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
            Looper.loop();
            return 0;
        }
        //提交到指定的php页面
        String urlstr = this.urlstr + "login.php";
        URL url = new URL(urlstr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        String params = "uid=" + user_id + '&' + "pwd=" + input_pwd;
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
        String result = sb.toString();//返回结果
        Log.e("log_tag", result);
        try {
            /*获取服务器返回的数据*/
            jsonObject2 = new JSONObject(result);
            returnResult = jsonObject2.getInt("status");
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "the Error parsing data " + e.toString());
        }
        return returnResult;
    }
}
