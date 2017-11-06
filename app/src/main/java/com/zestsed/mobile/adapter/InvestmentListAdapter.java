package com.zestsed.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zestsed.mobile.R;
import com.zestsed.mobile.data.Investment;

import java.util.List;

/**
 * Created by mdugah on 1/18/2017.
 */

public class InvestmentListAdapter extends ArrayAdapter<Investment> {
    private final Context context;
    private final List<Investment> values;

    public InvestmentListAdapter(Context context, List<Investment> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_contribution, parent, false);
        TextView amount = (TextView) rowView.findViewById(R.id.firstLine);
        TextView date = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        amount.setText("GHC "+values.get(position).getTotalAmount()+"");
        date.setText(values.get(position).getRate()+"%");
        imageView.setImageResource(R.mipmap.blue);

        return rowView;
    }
}
