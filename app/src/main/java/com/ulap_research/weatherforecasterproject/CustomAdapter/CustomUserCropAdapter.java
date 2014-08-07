package com.ulap_research.weatherforecasterproject.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ulap_research.weatherforecasterproject.R;

import java.util.ArrayList;

public class CustomUserCropAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Integer> cropId;
    private ArrayList<String> cropName;
    private ArrayList<String> cropType;
    private ArrayList<String> cropLevel;
    private ArrayList<String> cropLevelRequire;
    private ArrayList<Integer> cropRequireLevelUp;
    private int[] cropImageId;

    public CustomUserCropAdapter(Context mContext, ArrayList<Integer> cropId, ArrayList<String> cropName,
                                 ArrayList<String> cropType, ArrayList<String> cropLevel,
                                 ArrayList<String> cropLevelRequire, ArrayList<Integer> cropRequireLevelUp,
                                 int[] cropImageId) {
        this.mContext = mContext;
        this.cropId = cropId;
        this.cropName = cropName;
        this.cropType = cropType;
        this.cropLevel = cropLevel;
        this.cropLevelRequire = cropLevelRequire;
        this.cropRequireLevelUp = cropRequireLevelUp;
        this.cropImageId = cropImageId;
    }

    @Override
    public int getCount() {
        return cropName.size();
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

        View row = mInflater.inflate(R.layout.custom_list_user_crop, parent, false);

        ImageView ivCropImage = (ImageView) row.findViewById(R.id.cropImageView);
        ivCropImage.setBackgroundResource(cropImageId[cropId.get(position) - 1]);

        TextView tvCropName = (TextView) row.findViewById(R.id.cropName);
        tvCropName.setText(cropName.get(position) + " (" + cropType.get(position) + ")");

        TextView tvCropLevel = (TextView) row.findViewById(R.id.cropLevel);
        tvCropLevel.setText(mContext.getString(R.string.garden_crop_level) + " " + cropLevel.get(position));

        TextView tvCropLevelRequire = (TextView) row.findViewById(R.id.cropLevelRequire);

        ProgressBar pbLevel = (ProgressBar) row.findViewById(R.id.levelProgressBar);
        if(!cropLevelRequire.get(position).equalsIgnoreCase("max")) {
            int require = (int) (100*(((double) cropRequireLevelUp.get(position) - Double.parseDouble(cropLevelRequire.get(position)))
                    / (double) cropRequireLevelUp.get(position)));
            pbLevel.setProgress(require);
            tvCropLevelRequire.setText(cropLevelRequire.get(position) + " " + mContext.getString(R.string.garden_next_level_desc));
        }
        else {
            pbLevel.setProgress(100);
            tvCropLevelRequire.setText(mContext.getString(R.string.garden_crop_max_level));
        }

        return row;
    }
}
