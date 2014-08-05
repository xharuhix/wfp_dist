package com.ulap_research.weatherforecasterproject.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ulap_research.weatherforecasterproject.R;

public class CustomRainAdapter extends BaseAdapter{

    private Context mContext;
    private double[] rainAmount;
    private int[] rainPrice;

    public CustomRainAdapter(Context mContext, double[] rainAmount, int[] rainPrice) {
        this.mContext = mContext;
        this.rainAmount = rainAmount;
        this.rainPrice = rainPrice;
    }

    @Override
    public int getCount() {
        return rainAmount.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
        View row = mInflater.inflate(R.layout.custom_list_rain, parent, false);

        TextView tvRainText = (TextView) row.findViewById(R.id.rainText);
        TextView tvRainPrice = (TextView) row.findViewById(R.id.rainPrice);

        tvRainText.setText(rainAmount[position] + " " + mContext.getString(R.string.rain_unit_desc));
        tvRainPrice.setText(rainPrice[position] + " " + mContext.getString(R.string.rain_cloud_points));

        return row;
    }
}
