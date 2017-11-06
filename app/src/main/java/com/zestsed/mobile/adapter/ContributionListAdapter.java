package com.zestsed.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zestsed.mobile.R;
import com.zestsed.mobile.data.Contribution;

import java.util.List;

/**
 * Created by mdugah on 1/18/2017.
 */

public class ContributionListAdapter extends ArrayAdapter<Contribution> {
    private final Context context;
    private final List<Contribution> values;

    public ContributionListAdapter(Context context, List<Contribution> values) {
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
        amount.setText(values.get(position).getAmount()+"");
        date.setText(values.get(position).getDate()+"");
        imageView.setImageResource(R.mipmap.blue);

        return rowView;
    }
}
