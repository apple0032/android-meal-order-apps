package net.simplifiedcoding.bottomnavigationexample;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class CartAdapter implements ListAdapter {
    ArrayList<CartData> arrayList;
    Context context;
    public CartAdapter(Context context, ArrayList<CartData> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
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
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CartData CartData = arrayList.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.cart_row, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(),"Hello " + (CartData.Id), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context,DetailActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("id", CartData.Id);
                    context.startActivity(intent);

                }
            });

            ImageButton search = (ImageButton) convertView.findViewById(R.id.deletefood);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(),"Clicked"+(CartData.Id), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context,CartActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("id", CartData.Id);
                    context.startActivity(intent);
                }
            });

            TextView title = convertView.findViewById(R.id.title);
            ImageView image = convertView.findViewById(R.id.list_image);
            title.setText(CartData.SubjectName);

            TextView price = convertView.findViewById(R.id.itemprice);
            String intprice = Integer.toString(CartData.Price);
            price.setText("$ "+intprice);


            TextView qty = convertView.findViewById(R.id.quantity);
            String intqty = Integer.toString(CartData.Qty);
            qty.setText(intqty);

            Picasso.with(context)
                    .load(CartData.Image)
                    .into(image);
        }
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
}