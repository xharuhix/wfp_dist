package com.ulap_research.weatherforecasterproject.CustomAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ulap_research.weatherforecasterproject.R;

import java.util.ArrayList;


public class CustomRankingAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<String> username;
    ArrayList<Integer> rankNum;
    ArrayList<Boolean> isUserRank;

    public CustomRankingAdapter(Context context, ArrayList<String> username
            , ArrayList<Integer> rankNum, ArrayList<Boolean> isUserRank) {
        this.mContext= context;
        this.username = username;
        this.rankNum = rankNum;
        this.isUserRank = isUserRank;
    }

    @Override
    public int getCount() {
        return username.size();
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

        View row = mInflater.inflate(R.layout.custom_list_global_rank, parent, false);

        TextView tvUsername = (TextView)row.findViewById(R.id.username);
        tvUsername.setText(username.get(position));

        TextView tvRank = (TextView)row.findViewById(R.id.rankNum);
        tvRank.setText(rankNum.get(position) + "");

        // change background color
        if(isUserRank.get(position)) {
            // check version
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                row.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_list_global_rank));
            } else {
                row.setBackground(mContext.getResources().getDrawable(R.drawable.bg_list_global_rank));
            }
            tvUsername.setTypeface(tvUsername.getTypeface(), Typeface.BOLD);
            tvRank.setTextColor(Color.BLACK);
            TextView tvRankText = (TextView)row.findViewById(R.id.rankText);
            tvRankText.setTextColor(Color.BLACK);
        }

        return row;
    }
}
