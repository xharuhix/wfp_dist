package com.ulap_research.weatherforecasterproject.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ulap_research.weatherforecasterproject.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomCropAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<String> cropName;
    private ArrayList<String> cropType;
    private ArrayList<String> cropDesc;
    private ArrayList<Integer> cropRarity;
    private ArrayList<Integer> cropPrice;
    private int[] cropImageId;

    public CustomCropAdapter(Context mContext, ArrayList<String> cropName, ArrayList<String> cropType,
                             ArrayList<String> cropDesc, ArrayList<Integer> cropRarity,
                             ArrayList<Integer> cropPrice, int[] cropImageId) {
        this.mContext = mContext;
        this.cropName = cropName;
        this.cropType = cropType;
        this.cropDesc = cropDesc;
        this.cropRarity = cropRarity;
        this.cropPrice = cropPrice;
        this.cropImageId = cropImageId;
    }

    @Override
    public int getCount() {
        return cropImageId.length;
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

        View row = mInflater.inflate(R.layout.custom_list_crop, parent, false);

        ImageView ivCropImage = (ImageView) row.findViewById(R.id.cropImageView);
        ivCropImage.setBackgroundResource(cropImageId[position]);

        TextView tvCropName = (TextView) row.findViewById(R.id.cropName);
        tvCropName.setText(cropName.get(position) + " (" + cropType.get(position) + ")");

        TextView tvCropDesc = (TextView) row.findViewById(R.id.cropDesc);
        tvCropDesc.setText(cropDesc.get(position));

        TextView tvCropRarity = (TextView) row.findViewById(R.id.cropRarity);
        tvCropRarity.setText(mContext.getString(R.string.crop_shop_rarity) + " " +cropRarity.get(position)
                + " " + mContext.getString(R.string.crop_shop_rarity_unit));

        TextView tvCropPrice = (TextView) row.findViewById(R.id.cropPrice);
        tvCropPrice.setText(mContext.getString(R.string.crop_shop_price) + " " + cropPrice.get(position) + " " + mContext.getString(R.string.crop_shop_cloud_points));

        return row;
    }
}
