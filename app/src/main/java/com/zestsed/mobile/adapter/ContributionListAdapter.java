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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        amount.setText("Contributed GHC" + values.get(position).getContributionAmount() + " via " + values.get(position).getModeOfPayment());
        date.setText("on " + dateFormat(values.get(position).getDateOfContribution()));
        imageView.setImageResource(R.mipmap.blue);

        return rowView;
    }

    private String dateFormat(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",Locale.getDefault());
        try {
            return sdf.format(Date.valueOf(date));
        } catch (Exception ex) {
            return date;
        }
    }
}
