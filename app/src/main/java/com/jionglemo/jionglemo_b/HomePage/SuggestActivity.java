package com.jionglemo.jionglemo_b.HomePage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.R;

public class SuggestActivity extends AppCompatActivity {

    private EditText suggestET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        suggestET = (EditText) findViewById(R.id.suggestET);
    }

    //确定
    public void confirm(View view){
        String suggestStr=suggestET.getText().toString().trim();
        if(suggestStr.length()==0){
            Toast.makeText(this,"请输入内容",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"提交成功，感谢您的宝贵建议和意见",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //返回
    public void back(View view){
        finish();
    }
}
