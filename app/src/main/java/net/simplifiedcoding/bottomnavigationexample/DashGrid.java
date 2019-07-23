package net.simplifiedcoding.bottomnavigationexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DashGrid extends BaseAdapter {
    private Context context;
    private final String[] text;
    private final String[] imageId;

    public DashGrid(Context context, String[] text, String[] imageId) {
        this.context = context;
        this.text = text;
        this.imageId = imageId;
    }

    @Override
    public int getCount() {
        return text.length;
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
        View grid;
        // Context 動態放入mainActivity
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = new View(context);
            // 將grid_single 動態載入(image+text)
            grid = layoutInflater.inflate(R.layout.dashgrid, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
            textView.setText(text[position]);
            //imageView.setImageResource(imageId[position]);
            Picasso.with(context)
                    .load(imageId[position])
                    .into(imageView);
        } else {
            grid = (View) convertView;
        }

        //Handle if it is null
        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
        textView.setText(text[position]);
        //imageView.setImageResource(imageId[position]);
        Picasso.with(context)
                .load(imageId[position])
                .into(imageView);

        return grid;
    }
}