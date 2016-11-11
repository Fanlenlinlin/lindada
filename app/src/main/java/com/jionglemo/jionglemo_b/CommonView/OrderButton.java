package com.jionglemo.jionglemo_b.CommonView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.R;


/**
 * Created by Mike on 2016/7/12.
 */
public class OrderButton extends LinearLayout {

    private Context mContext;
    private String orderText;
    private ImageView orderIV;
    private int state=0;//排序的状态，0代表不排序，1代表升序，2代表降序
    private TextView orderTV;

    public OrderButton(Context context) {
        super(context);
        mContext=context;
        inflateLayout();
    }


    public OrderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        if(attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.OrderButton);
            orderText = array.getString(R.styleable.OrderButton_orderText);
            array.recycle();
        }
        inflateLayout();
    }


    private void inflateLayout(){
        this.addView(LayoutInflater.from(mContext).inflate(R.layout.order_button, null));
        orderIV = (ImageView)findViewById(R.id.orderIV);
        orderTV = (TextView) findViewById(R.id.orderTV);
        orderTV.setText(orderText);
    }

    //选择升序
    public void chooseUp(){
        state=1;
        orderIV.setImageResource(R.drawable.order_up_check);
        orderTV.setEnabled(false);
    }

    //选择降序
    public void chooseDown(){
        state=2;
        orderIV.setImageResource(R.drawable.order_down_check);
        orderTV.setEnabled(false);
    }

    //清除排序
    public void clearOrder(){
        state=0;
        orderIV.setImageResource(R.drawable.order_down_uncheck);
        orderTV.setEnabled(true);
    }

    //获取排序的状态
    public int getState(){
        return state;
    }
}
