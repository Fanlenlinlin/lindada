package com.jionglemo.jionglemo_b.Chat;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.jionglemo.jionglemo_b.R;

import io.rong.imkit.RongContext;
import io.rong.imkit.widget.provider.InputProvider;

/**
 * Created by Mike on 2016/7/22.
 */
public class EnmotionGifProvider extends InputProvider.ExtendProvider {

    private PopupWindow popupWindow;
    private LinearLayout mIndicator;
    private ViewPager mViewPager;
    private View enmotionView;
    private Context context;

    public EnmotionGifProvider(RongContext context) {
        super(context);
    }

    /**
     * 设置展示的图标
     * @param context
     * @return
     */
    @Override
    public Drawable obtainPluginDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.jiong_enmotion_gif);
    }

    /**
     * 设置图标下的title
     * @param context
     * @return
     */
    @Override
    public CharSequence obtainPluginTitle(Context context) {
        return "表情";
    }

    /**
     * click 事件
     *  @param view
     */

    @Override
    public void onPluginClick(View view) {
        showEnmotion(view);
    }

    public void showEnmotion(View view){
        if (popupWindow == null) {
            context = view.getContext();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            enmotionView = layoutInflater.inflate(R.layout.rc_input_pager_layout, null);
            mViewPager = (ViewPager)enmotionView.findViewById(io.rong.imkit.R.id.rc_view_pager);
            mIndicator = (LinearLayout)enmotionView.findViewById(io.rong.imkit.R.id.rc_indicator);
            int mPageCount = 1;
            mViewPager.setAdapter(new EmoticonViewPagerAdapter());
        /*    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    RongEmojiPager.this.onIndicatorChanged(RongEmojiPager.this.mSelectedPage, position);
                    RongEmojiPager.this.mSelectedPage = position;
                }

                public void onPageScrollStateChanged(int state) {
                }
            });*/
            mViewPager.setCurrentItem(0, false);
            mViewPager.setOffscreenPageLimit(1);
            initIndicator(mPageCount, mIndicator);
            onIndicatorChanged(-1, 0);
            // 创建一个PopuWidow对象
            popupWindow = new PopupWindow(enmotionView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        }
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
    }

    private class EmoticonViewPagerAdapter extends PagerAdapter {
        private EmoticonViewPagerAdapter() {
        }

        public Object instantiateItem(ViewGroup container, int position) {
            mIndicator.setVisibility(View.VISIBLE);
            RecyclerView recyclerView= (RecyclerView) LayoutInflater.from(container.getContext()).inflate(R.layout.enmotion_recycleview,null);
            GridLayoutManager hotsellgoodGM=new GridLayoutManager(container.getContext(),4);
            recyclerView.setLayoutManager(hotsellgoodGM);
            recyclerView.setHasFixedSize(true);
            EnmojiGifAdapter enmojiGifAdapter=new EnmojiGifAdapter(context,getCurrentConversation().getTargetId(), getCurrentConversation().getConversationType());
            recyclerView.setAdapter(enmojiGifAdapter);
            container.addView(recyclerView);
            return recyclerView;
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public int getCount() {
            return 1;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            View layout = (View)object;
            container.removeView(layout);
        }
    }

    private void initIndicator(int pages, LinearLayout indicator) {
        for(int i = 0; i < pages; ++i) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(16, 16);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(0, 0, 20, 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(io.rong.imkit.R.drawable.rc_indicator);
            indicator.addView(imageView);
        }

    }

    private void onIndicatorChanged(int pre, int cur) {
        int count = this.mIndicator.getChildCount();
        if(count > 0 && pre < count && cur < count) {
            ImageView curView;
            if(pre >= 0) {
                curView = (ImageView)this.mIndicator.getChildAt(pre);
                curView.setImageResource(io.rong.imkit.R.drawable.rc_indicator);
            }

            if(cur >= 0) {
                curView = (ImageView)this.mIndicator.getChildAt(cur);
                curView.setImageResource(io.rong.imkit.R.drawable.rc_indicator_hover);
            }
        }

    }

}