package com.zestsed.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zestsed.mobile.R;
import com.zestsed.mobile.data.Product;

/**
 * Created by mdugah on 3/15/2017.
 */

public class ProductGridAdapter extends BaseAdapter {
    private final Context context;
    private final Product[] products;

    public ProductGridAdapter(Context context, Product[] products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Product product = products[position];
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.product, null);
        }

        final ImageView imageView = (ImageView) view.findViewById(R.id.imageview_cover_art);
        final TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        final TextView descTextView = (TextView) view.findViewById(R.id.product_desc);

        imageView.setImageResource(product.getIcon());
        nameTextView.setText(product.getName());
        descTextView.setText(product.getDesc());

        return view;
    }
}
