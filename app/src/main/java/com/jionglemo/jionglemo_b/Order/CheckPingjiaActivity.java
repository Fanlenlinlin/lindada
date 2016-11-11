package com.jionglemo.jionglemo_b.Order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.CommonView.Picker.MyDatePicker;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CheckPingjiaActivity extends AppCompatActivity {

    public static int p;//当前页数
    public static int total_page;//总页数
    private int lastPosition;
    private boolean lock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住
    private RecyclerView pingjiaRV;
    private LinearLayoutManager linearLayoutManager;
    private List<Pingjia> pingjiaList=new ArrayList<>();
    private PingjiaRVadapter pingjiaRVadapter;
    private String star=null;
    private ImageView searchDateIV;
    private LinearLayout searchDateLL;

    private int userYear;
    private int userMonth;
    private int userDay;
    private TextView startTimeTV;
    private TextView endTimeTV;
    private String start_time=null;
    private String end_time=null;
    private LinearLayout kong_pingjiaLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_pingjia);
        RadioGroup pingjiaRG= (RadioGroup) findViewById(R.id.pingjiaRG);
        pingjiaRG.check(R.id.allPingjiaRB);
        final RadioButton allPingjiaRB= (RadioButton) findViewById(R.id.allPingjiaRB);
        final RadioButton goodPingjiaRB= (RadioButton) findViewById(R.id.goodPingjiaRB);
        final RadioButton middlePingjiaRB= (RadioButton) findViewById(R.id.middlePingjiaRB);
        final RadioButton lowPingjiaRB= (RadioButton) findViewById(R.id.lowPingjiaRB);

        searchDateIV = (ImageView) findViewById(R.id.searchDateIV);
        searchDateLL = (LinearLayout) findViewById(R.id.searchDateLL);
        startTimeTV = (TextView) findViewById(R.id.startTimeTV);
        endTimeTV = (TextView) findViewById(R.id.endTimeTV);
        // 取得系统日期:
        Calendar c = Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH)+1;
        int day=c.get(Calendar.DAY_OF_MONTH);
        String date;
        if (month < 10 && day < 10) {
            date=year + "-0" + month + "-0" + day;
        } else if (month >= 10 && day < 10) {
            date=year + "-" + month + "-0" + day;
        } else if (month < 10 && day >= 10) {
            date=year + "-0" + month + "-" + day;
        } else {
            date=year + "-" + month + "-" + day;
        }
        startTimeTV.setText(date);
        endTimeTV.setText(date);

        //获取评价的数量
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    int praise=response.getInt("praise");
                    int middle_evaluate=response.getInt("middle_evaluate");
                    int negative_comment=response.getInt("negative_comment");
                    allPingjiaRB.setText("全部("+(praise+middle_evaluate+negative_comment)+")");
                    goodPingjiaRB.setText("好评("+praise+")");
                    middlePingjiaRB.setText("中评("+middle_evaluate+")");
                    lowPingjiaRB.setText("差评("+negative_comment+")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        getNetData.getData(null,CommonValue.getEvaluateCount);

        pingjiaRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.allPingjiaRB:
                        star=null;
                        getPingjiaData();
                        break;
                    case R.id.goodPingjiaRB:
                        star="5";
                        getPingjiaData();
                        break;
                    case R.id.middlePingjiaRB:
                        star="3,4";
                        getPingjiaData();
                        break;
                    case R.id.lowPingjiaRB:
                        star="1,2";
                        getPingjiaData();
                        break;
                }
            }
        });
        pingjiaRV = (RecyclerView) findViewById(R.id.pingjiaRV);
        linearLayoutManager = new LinearLayoutManager(this);
        pingjiaRV.setLayoutManager(linearLayoutManager);
        kong_pingjiaLL = (LinearLayout) findViewById(R.id.kong_pingjiaLL);
        getPingjiaData();
    }

    //获取评价的数据
    private void getPingjiaData(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<Pingjia> list = JSON.parseArray(response.getJSONArray("data_list").toString(),Pingjia.class);
                    if(list.size()==0){
                        kong_pingjiaLL.setVisibility(View.VISIBLE);
                        pingjiaRV.setVisibility(View.GONE);
                    }else {
                        kong_pingjiaLL.setVisibility(View.GONE);
                        pingjiaRV.setVisibility(View.VISIBLE);
                        if(pingjiaList.size()!=0)
                            pingjiaList.clear();
                        pingjiaList.addAll(list);
                        JSONObject pageJSON=response.getJSONObject("page");
                        p = pageJSON.getInt("p");
                        total_page = pageJSON.getInt("total_page");
                        pingjiaRVadapter = new PingjiaRVadapter(CheckPingjiaActivity.this, pingjiaList, "评价"){
                            @Override
                            public void loadAgain() {
                                super.loadAgain();
                                getMorePingjiaData();
                            }
                        };
                        pingjiaRV.setAdapter(pingjiaRVadapter);
                        pingjiaRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                //滑到最后一条再向下滑动时重新获取数据，加载完毕后就不进行加载
                                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition == pingjiaList.size() && p<total_page) {
                                    if(lock){
                                        lock=false;//锁住
                                        getMorePingjiaData();
                                    }
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("star",star);
        params.put("start_time",start_time);
        params.put("end_time",end_time);
        getNetData.getData(params, CommonValue.getEvaluateList);
    }

    //获取更多评价的数据
    private void getMorePingjiaData(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<Pingjia> list= JSON.parseArray(response.getJSONArray("data_list").toString(),Pingjia.class);
                    pingjiaList.addAll(list);
                    pingjiaRVadapter.notifyDataSetChanged();

                    JSONObject pageJSON=response.getJSONObject("page");
                    p = pageJSON.getInt("p");
                    total_page = pageJSON.getInt("total_page");

                    lock=true;//解锁
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getDataFailure() {
                super.getDataFailure();
                if(pingjiaRVadapter!=null){
                    pingjiaRVadapter.loadError = true;
                    pingjiaRVadapter.notifyItemChanged(pingjiaRVadapter.getItemCount() - 1);//更新最后一条数据的状态
                    lock=true;//解锁
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("star",star);
        params.put("start_time",start_time);
        params.put("end_time",end_time);
        params.put("p",p+1);
        getNetData.getData(params, CommonValue.getEvaluateList);
    }

    //日期搜索
    public void searchDate(View view){
        if(searchDateLL.getVisibility()==View.GONE){
            searchDateLL.setVisibility(View.VISIBLE);
            searchDateIV.setImageResource(R.drawable.direction_up);
        }else {
            searchDateLL.setVisibility(View.GONE);
            searchDateIV.setImageResource(R.drawable.direction_down);
        }
    }

    //起始日期选择
    public void startTime(View view){
        showDateDialog(startTimeTV);
    }

    //结束日期选择
    public void endTime(View view){
        showDateDialog(endTimeTV);
    }

    private void showDateDialog(final TextView textView){
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.date_picker);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        MyDatePicker datePicker = (MyDatePicker) dialog.findViewById(R.id.datePicker);
        final TextView dateTV= (TextView) dialog.findViewById(R.id.dateTV);

        datePicker.setOnChangeListener(new MyDatePicker.OnChangeListener() {
            @Override
            public void onChange(int year, int month, int day, int day_of_week) {
                userYear = year;
                userMonth=month;
                userDay=day;
                if (month < 10 && day < 10) {
                    dateTV.setText(year + "年0" + month + "月0" + day+ "日 星期" + MyDatePicker.getDayOfWeekCN(day_of_week));
                } else if (month >= 10 && day < 10) {
                    dateTV.setText(year + "年" + month + "月0" + day+ "日 星期" + MyDatePicker.getDayOfWeekCN(day_of_week));
                } else if (month < 10 && day >= 10) {
                    dateTV.setText(year + "年0" + month + "月" + day+ "日 星期" + MyDatePicker.getDayOfWeekCN(day_of_week));
                } else {
                    dateTV.setText(year + "年" + month + "月" + day+ "日 星期" + MyDatePicker.getDayOfWeekCN(day_of_week));
                }
            }
        });
        String[] dateArray=textView.getText().toString().split("-");
        datePicker.setYear(Integer.valueOf(dateArray[0]));
        datePicker.setMonth(Integer.valueOf(dateArray[1]));
        datePicker.setDay(Integer.valueOf(dateArray[2]));
        TextView saveTV= (TextView) window.findViewById(R.id.saveTV);
        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date;
                if (userMonth < 10 && userDay < 10) {
                    date=userYear + "-0" + userMonth + "-0" + userDay;
                } else if (userMonth >= 10 && userDay < 10) {
                    date=userYear + "-" + userMonth + "-0" + userDay;
                } else if (userMonth < 10 && userDay >= 10) {
                    date=userYear + "-0" + userMonth + "-" + userDay;
                } else {
                    date=userYear + "-" + userMonth + "-" + userDay;
                }
                textView.setText(date);
                dialog.dismiss();
            }
        });
    }

    //开始日期搜索
    public void searchDateNow(View view){
        searchDateLL.setVisibility(View.GONE);
        searchDateIV.setImageResource(R.drawable.direction_down);
        start_time= startTimeTV.getText().toString()+" 00:00:00";
        end_time= endTimeTV.getText().toString()+" 24:00:00";
        getPingjiaData();
    }

    //搜索
    public void search(View view){
        Intent intent=new Intent(this,SearchPingjiaActivity.class);
        startActivity(intent);
    }

    //返回
    public void back(View view){
        finish();
    }
}
