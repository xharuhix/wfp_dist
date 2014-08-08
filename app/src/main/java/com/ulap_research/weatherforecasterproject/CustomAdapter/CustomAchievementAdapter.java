package com.ulap_research.weatherforecasterproject.CustomAdapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ulap_research.weatherforecasterproject.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Soranut on 8/4/2014.
 */
public class CustomAchievementAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> achievementName;
    private ArrayList<String> achievementDesc;
    private ArrayList<Integer> achievementExp;
    private ArrayList<Boolean> isUserAchieved;

    public CustomAchievementAdapter(Context context, ArrayList<String> achievementName
            , ArrayList<String> achievementDesc, ArrayList<Integer> achievementExp, ArrayList<Boolean> isUserAchieved) {

        this.mContext= context;
        this.achievementName = achievementName;
        this.achievementDesc = achievementDesc;
        this.achievementExp = achievementExp;
        this.isUserAchieved = isUserAchieved;
    }

    @Override
    public int getCount() {
        return achievementName.size();
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
        View row = mInflater.inflate(R.layout.custom_list_achievement, parent, false);

        TextView tvAchievementName = (TextView) row.findViewById(R.id.achievement_name);
        TextView tvAchievementDesc = (TextView) row.findViewById(R.id.achievement_desc);
        TextView tvAchievementExp = (TextView) row.findViewById(R.id.achievement_exp);
        TextView tvAchievementStatus = (TextView) row.findViewById(R.id.achievement_status);

        tvAchievementName.setText(achievementName.get(position));
        tvAchievementDesc.setText(achievementDesc.get(position));
        tvAchievementExp.setText("(Gain " + achievementExp.get(position) + " Exp.)");
        if(isUserAchieved.get(position)) {
            tvAchievementStatus.setText(mContext.getString(R.string.completed_achievement));
        }
        else {
            tvAchievementStatus.setText(mContext.getString(R.string.uncompleted_achievement));
            tvAchievementStatus.setTextColor(Color.GRAY);
            tvAchievementName.setTextColor(Color.GRAY);
            tvAchievementExp.setTextColor(Color.GRAY);
            tvAchievementDesc.setTextColor(Color.GRAY);
        }
        return row;
    }
}
