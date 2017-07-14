package com.mohitkishore.www.weighme.Adaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mohitkishore.www.weighme.Model.Weight;
import com.mohitkishore.www.weighme.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gkmohit on 14/07/17.
 */

public class WeightListAdaptor extends ArrayAdapter<Weight> {

    private ArrayList<Weight> mWeights;
    private Context mContext;

    //View lookup cache
    private static class ViewHolder {
        TextView date;
        TextView month;
        TextView weight;
    }

    public WeightListAdaptor(@NonNull Context context, ArrayList<Weight> weights) {
        super(context, R.layout.weight_list_item, weights);
        mWeights = weights;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mWeights.size();
    }

    @Nullable
    @Override
    public Weight getItem(int position) {
        return mWeights.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Weight weight = getItem(position);

        WeightListAdaptor.ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new WeightListAdaptor.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.weight_list_item, parent, false);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.month = (TextView) convertView.findViewById(R.id.month);
            viewHolder.weight = (TextView) convertView.findViewById(R.id.weight);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (WeightListAdaptor.ViewHolder) convertView.getTag();
        }

        viewHolder.weight.setText(weight.getWeight());
        viewHolder.date.setText(weight.getDate());
        viewHolder.month.setText(weight.getMonth());

        return convertView;
    }
}
