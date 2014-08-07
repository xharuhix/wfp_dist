package com.ulap_research.weatherforecasterproject.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ulap_research.weatherforecasterproject.R;


public class CustomCropMenuAdapter extends BaseAdapter {
    private Context mContext;
    private int[] iconImage;
    private String[] menuText;

    public CustomCropMenuAdapter(Context mContext, int[] iconImage, String[] menuText) {
        this.mContext = mContext;
        this.iconImage = iconImage;
        this.menuText = menuText;
    }

    @Override
    public int getCount() {
        return iconImage.length;
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
        View row = mInflater.inflate(R.layout.custom_list_crop_menu, parent, false);

        ImageView ivCropImage = (ImageView) row.findViewById(R.id.menuIconImage);
        ivCropImage.setBackgroundResource(iconImage[position]);

        TextView tvMenuText = (TextView) row.findViewById(R.id.menuText);
        tvMenuText.setText(menuText[position]);

        return row;
    }
}
