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
import com.zestsed.mobile.data.InvestmentDetails;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by mdugah on 1/18/2017.
 */

public class InvestmentListAdapter extends ArrayAdapter<InvestmentDetails> {
    private final Context context;
    private final List<InvestmentDetails> values;

    public InvestmentListAdapter(Context context, List<InvestmentDetails> values) {
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
        amount.setText("Contribution of GH₵ " + values.get(position).getAmount() + " was invested on " + dateFormat(values.get(position).getDateOfInvestment()));
        date.setText("at an interest rate of " + values.get(position).getRate() + "% per annum");

        return rowView;
    }

    private String dateFormat(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEEEE dd-MMM-yyyy", Locale.getDefault());
        try {
            return sdf.format(Date.valueOf(date));
        } catch (Exception ex) {
            return date;
        }
    }
}
