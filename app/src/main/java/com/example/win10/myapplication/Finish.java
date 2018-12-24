package com.example.win10.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class Finish extends AppCompatActivity {
    String state;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        //成功并展示
        Intent intent = getIntent();
        state = intent.getStringExtra("state");
        textView=findViewById(R.id.iv);
        switch (state)
        {
            case "0":
                textView.setText("你连投都没有投，你是在逗我咩,你凉了");
                break;
            case "1":
                textView.setText("你投了第"+intent.getStringExtra("voteteamnum1")+"号队——》"+intent.getStringExtra("voteteamname1")+"如果你投吴俊庭我感谢你，不是的话请滚");
                break;
            case "2":
                textView.setText("你投了第"+intent.getStringExtra("voteteamnum1")+"和"+intent.getStringExtra("voteteamnum2")+"号队——》"+intent.getStringExtra("voteteamname1")+"队和"+intent.getStringExtra("voteteamname2")+"队，如果你投吴俊庭我感谢你，不是的话请滚");;
                break;
        }
    }

      @Override

        public boolean onKeyDown(int keyCode,KeyEvent event){

        if(keyCode== KeyEvent.KEYCODE_BACK)

            return true;//不执行父类点击事件

        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件

    }

}
