package com.jionglemo.jionglemo_b.HomePage;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.jionglemo.jionglemo_b.ZhiBo.ZhiboActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {

    private View rootView;//作为内部变量，用于与ViewPager结合优化内存
    private CircleImageView storeLogoCIV;
    private TextView storeNameTV;
    private TextView locationTV;
    private TextView today_order_moneyTV;
    private TextView today_order_quantityTV;
    private TextView today_visits_quantityTV;
    private TextView fans_quantityTV;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_home_page, container, false);
            //直播
            ImageView daogouzhiboIV= (ImageView) rootView.findViewById(R.id.daogouzhiboIV);
            daogouzhiboIV.setOnClickListener(new MyOnClickListener());
            //粉丝
            ImageView fensiIV= (ImageView) rootView.findViewById(R.id.fensiIV);
            fensiIV.setOnClickListener(new MyOnClickListener());
            //商品收藏
           /* ImageView product_collectIV= (ImageView) rootView.findViewById(R.id.product_collectIV);
            product_collectIV.setOnClickListener(new MyOnClickListener());*/
            //虚拟商铺
            ImageView vr_storeIV= (ImageView) rootView.findViewById(R.id.vr_storeIV);
            vr_storeIV.setOnClickListener(new MyOnClickListener());
            //服务
            ImageView serviceIV= (ImageView) rootView.findViewById(R.id.serviceIV);
            serviceIV.setOnClickListener(new MyOnClickListener());
            //设置
            ImageView settingIV= (ImageView) rootView.findViewById(R.id.settingIV);
            settingIV.setOnClickListener(new MyOnClickListener());
            //店铺二维码
            LinearLayout vr_storeLL= (LinearLayout) rootView.findViewById(R.id.vr_storeLL);
            vr_storeLL.setOnClickListener(new MyOnClickListener());

            storeLogoCIV = (CircleImageView) rootView.findViewById(R.id.storeLogoCIV);
            storeNameTV = (TextView) rootView.findViewById(R.id.storeNameTV);
            locationTV = (TextView) rootView.findViewById(R.id.locationTV);
            today_order_moneyTV = (TextView) rootView.findViewById(R.id.today_order_moneyTV);
            today_order_quantityTV = (TextView) rootView.findViewById(R.id.today_order_quantityTV);
            today_visits_quantityTV = (TextView) rootView.findViewById(R.id.today_visits_quantityTV);
            fans_quantityTV = (TextView) rootView.findViewById(R.id.fans_quantityTV);

            //全局初始化此配置
            imageLoader = ImageLoaderArgument.getInstance(getActivity());
            options = ImageLoaderArgument.getDisplayImageOptions(R.color.grey);

            GetNetData getNetData=new GetNetData(getActivity()){
                @Override
                public void getDataSuccess(JSONObject response) {
                    super.getDataSuccess(response);
                    try {
                        imageLoader.displayImage(CommonValue.serverBasePath+response.getString("logo"),storeLogoCIV, options);
                        storeNameTV.setText(response.getString("store_name"));
                        locationTV.setText(response.getString("province_name")+response.getString("city_name"));
                        today_order_moneyTV.setText("¥："+response.getString("today_order_money"));
                        today_order_quantityTV.setText(response.getString("today_order_quantity"));
                        today_visits_quantityTV.setText(response.getString("today_visits_quantity"));
                        fans_quantityTV.setText("("+response.getString("fans_quantity")+")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            getNetData.getData(null, CommonValue.storeHomepage);


        }
        return rootView;
    }


    public class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.daogouzhiboIV:
                    Intent intent1=new Intent(getActivity(), ZhiboActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.fensiIV:
                    Intent intent2=new Intent(getActivity(), FensiActivity.class);
                    startActivity(intent2);
                    break;
                /*case R.id.product_collectIV:
                    Intent intent3=new Intent(getActivity(), ProductCollectActivity.class);
                    startActivity(intent3);
                    break;*/
                case R.id.vr_storeIV:
                    Intent intent4=new Intent(getActivity(), VRActivity.class);
                    intent4.putExtra("store_name",storeNameTV.getText().toString());
                    startActivity(intent4);
                    break;
                case R.id.serviceIV:
                    Intent intent5=new Intent(getActivity(), BusinessServicesActivity.class);
                    startActivity(intent5);
                    break;
                case R.id.settingIV:
                    Intent intent6=new Intent(getActivity(), MySettingActivity.class);
                    startActivity(intent6);
                    break;
                case R.id.vr_storeLL:
                    Toast.makeText(getActivity(),"商家二维码暂未开放",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
