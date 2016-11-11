package com.jionglemo.jionglemo_b.BasePacket;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.Chat.MessageFragment;
import com.jionglemo.jionglemo_b.HomePage.HomePageFragment;
import com.jionglemo.jionglemo_b.Order.OrderFragment;
import com.jionglemo.jionglemo_b.ProductManager.ProductManagerFragment;
import com.jionglemo.jionglemo_b.R;
import com.jionglemo.jionglemo_b.UpdateAPK.NewVersionDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;//使用一个公用的静态的activity实例，用于后续可关闭该activity

    private ViewPager viewPager;
    private List<Fragment> fragmentsList;
    private RadioGroup mainRG;
    private ImageView homePageIV;
    private ImageView messageIV;
    private ImageView orderIV;
    private ImageView productManagerIV;
    private TextView homePageTV;
    private TextView messageTV;
    private TextView orderTV;
    private TextView productManagerTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity=this;

        mainRG = (RadioGroup) findViewById(R.id.mainRG);
        homePageIV = (ImageView) findViewById(R.id.homePageIV);
        messageIV = (ImageView) findViewById(R.id.messageIV);
        orderIV = (ImageView) findViewById(R.id.orderIV);
        productManagerIV = (ImageView) findViewById(R.id.productManagerIV);
        homePageTV = (TextView) findViewById(R.id.homePageTV);
        messageTV = (TextView) findViewById(R.id.messageTV);
        orderTV = (TextView) findViewById(R.id.orderTV);
        productManagerTV = (TextView) findViewById(R.id.productManagerTV);
        mainRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                homePageIV.setEnabled(true);
                messageIV.setEnabled(true);
                orderIV.setEnabled(true);
                productManagerIV.setEnabled(true);
                homePageTV.setEnabled(true);
                messageTV.setEnabled(true);
                orderTV.setEnabled(true);
                productManagerTV.setEnabled(true);
                switch (checkedId){
                    case R.id.homePageRB:
                        homePageIV.setEnabled(false);
                        homePageTV.setEnabled(false);
                        viewPager.setCurrentItem(0,false);
                        break;
                    case R.id.messageRB:
                        messageIV.setEnabled(false);
                        messageTV.setEnabled(false);
                        viewPager.setCurrentItem(1,false);
                        break;
                    case R.id.orderRB:
                        orderIV.setEnabled(false);
                        orderTV.setEnabled(false);
                        viewPager.setCurrentItem(2,false);
                        break;
                    case R.id.productManagerRB:
                        productManagerIV.setEnabled(false);
                        productManagerTV.setEnabled(false);
                        viewPager.setCurrentItem(3,false);
                        break;
                }
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentsList = new ArrayList<>();
        fragmentsList.add(new HomePageFragment());
        fragmentsList.add(new MessageFragment());
        fragmentsList.add(new OrderFragment());
        fragmentsList.add(new ProductManagerFragment());
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        //viewPager.setOffscreenPageLimit(3);//设置预加载3页
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        mainRG.check(R.id.homePageRB);
                        break;
                    case 1:
                        mainRG.check(R.id.messageRB);
                        break;
                    case 2:
                        mainRG.check(R.id.orderRB);
                        break;
                    case 3:
                        mainRG.check(R.id.productManagerRB);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mainRG.check(R.id.homePageRB);//默认选择首页
        //检查版本更新
        NewVersionDialog.checkUpdateAPK(this);
    }
}
