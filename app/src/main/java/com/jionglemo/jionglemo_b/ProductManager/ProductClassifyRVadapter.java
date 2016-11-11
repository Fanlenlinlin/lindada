package com.jionglemo.jionglemo_b.ProductManager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.R;

import java.util.List;

/**
 * Created by Mike on 2016/6/22.
 */
public class ProductClassifyRVadapter extends RecyclerView.Adapter {

    private Context context;
    private List<ProductClassify> productClassifyList;

    public ProductClassifyRVadapter(Context mContext, List<ProductClassify> mProductClassifyList){
        context=mContext;
        productClassifyList=mProductClassifyList;
    }

    private class ProductClassifyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout itemLL;
        private TextView classifyTV;
        private TextView numberTV;

        public ProductClassifyViewHolder(View itemView) {
            super(itemView);
            itemLL= (LinearLayout) itemView.findViewById(R.id.itemLL);
            classifyTV= (TextView) itemView.findViewById(R.id.classifyTV);
            numberTV= (TextView) itemView.findViewById(R.id.numberTV);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_classify_item, parent,false);
         ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
         view.setLayoutParams(layoutParams);
        return new ProductClassifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ProductClassifyViewHolder productClassifyViewHolder= (ProductClassifyViewHolder) holder;
        productClassifyViewHolder.itemLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ProductClassifyActivity.class);
                intent.putExtra("classify_id",productClassifyList.get(position).getId());
                intent.putExtra("name",productClassifyList.get(position).getName());
                context.startActivity(intent);
            }
        });
        productClassifyViewHolder.classifyTV.setText(productClassifyList.get(position).getName());
        productClassifyViewHolder.numberTV.setText("共"+productClassifyList.get(position).getProduct_number()+"件商品");
    }

    @Override
    public int getItemCount() {
        return productClassifyList.size();
    }

}
