package example.firoz.notepadofflineonlinesyncreal2.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import example.firoz.notepadofflineonlinesyncreal2.R;
import example.firoz.notepadofflineonlinesyncreal2.model.Colors;

public class ListPopupAdapter extends BaseAdapter {

    ArrayList<Colors> colorsArrayList;

    public ListPopupAdapter(ArrayList<Colors> colorsArrayList) {
        this.colorsArrayList = colorsArrayList;
    }

    @Override
    public int getCount() {
        return colorsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ColorBaseHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_layout, parent, false);
            holder = new ColorBaseHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ColorBaseHolder) convertView.getTag();
        }
        holder.txt_title_color.setText(colorsArrayList.get(position).getTitle());
        Log.d("Colors Value", colorsArrayList.get(position).getTitle());
        Log.d("Colors Value", String.valueOf(colorsArrayList.get(position).getIconColor()));

        if (colorsArrayList.get(position).getIconColor() == 10)
        {
            holder.img_icon_color.setImageResource(R.drawable.more_colors);
        }
        else
        {
            holder.img_icon_color.setImageResource(0);
            holder.img_icon_color.setBackgroundColor(colorsArrayList.get(position).getIconColor());
        }
        return convertView;
    }

    class ColorBaseHolder{

        TextView txt_title_color;
        ImageView img_icon_color;

        public ColorBaseHolder(View itemView) {
            txt_title_color = (TextView) itemView.findViewById(R.id.txt_title_color);
            img_icon_color = (ImageView) itemView.findViewById(R.id.img_icon_color);
        }
    }
}
