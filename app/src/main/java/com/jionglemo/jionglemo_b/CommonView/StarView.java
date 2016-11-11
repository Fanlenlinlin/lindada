package com.jionglemo.jionglemo_b.CommonView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jionglemo.jionglemo_b.R;


/**
 * Created by Mike on 2016/7/12.
 */
public class StarView extends LinearLayout {

    private Context mContext;
    private int starNum;
    private ImageView starIV1;
    private ImageView starIV2;
    private ImageView starIV3;
    private ImageView starIV4;
    private ImageView starIV5;

    public StarView(Context context) {
        super(context);
        mContext=context;
        inflateLayout();
    }


    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        if(attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.StarView);
            starNum = array.getInt(R.styleable.StarView_starNum,0);
            array.recycle();
        }
        inflateLayout();
    }


    private void inflateLayout(){
        this.addView(LayoutInflater.from(mContext).inflate(R.layout.star_view, null));
        starIV1 = (ImageView)findViewById(R.id.starIV1);
        starIV2 = (ImageView)findViewById(R.id.starIV2);
        starIV3 = (ImageView)findViewById(R.id.starIV3);
        starIV4 = (ImageView)findViewById(R.id.starIV4);
        starIV5 = (ImageView)findViewById(R.id.starIV5);
        setStarNum(starNum);
    }

    //设置星星的个数
    public void setStarNum(int num){
       switch (num){
           case 1:
               starIV2.setVisibility(GONE);
           case 2:
               starIV3.setVisibility(GONE);
           case 3:
               starIV4.setVisibility(GONE);
           case 4:
               starIV5.setVisibility(GONE);
           default:
               break;
       }
    }
}
