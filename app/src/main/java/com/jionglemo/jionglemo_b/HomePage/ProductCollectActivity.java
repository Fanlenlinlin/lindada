package com.jionglemo.jionglemo_b.HomePage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jionglemo.jionglemo_b.R;

public class ProductCollectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_collect);
    }

    //返回
    public void back(View view){
        finish();
    }
}
