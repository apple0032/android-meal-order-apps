package net.simplifiedcoding.bottomnavigationexample;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class WaitAdapter implements ListAdapter {
    ArrayList<WaitData> arrayList;
    Context context;
    //ListView myPurchase;
    //ImageView img;

    public WaitAdapter(Context context, ArrayList<WaitData> arrayList) {
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
        final WaitData WaitData = arrayList.get(position);


        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.wait_row, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(),"Clicked "+WaitData.Id, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context,PurchaseDetailsActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("id", WaitData.Id);
                    context.startActivity(intent);
                }
            });

            //myPurchase = convertView.findViewById(R.id.plist);
            //img = convertView.findViewById(R.id.imageView4);

            TextView title = convertView.findViewById(R.id.title);
            title.setText(WaitData.Date);

            TextView qty = convertView.findViewById(R.id.quantity);
            qty.setText(Integer.toString(WaitData.Qty));

            TextView price = convertView.findViewById(R.id.itemprice);
            price.setText("$ "+Integer.toString(WaitData.Total));

            if(WaitData.Status.equals("deliver")) {
                ImageView listimage = convertView.findViewById(R.id.list_image);
                listimage.setImageResource(R.drawable.success2);
            }

            if(WaitData.Status.equals("done")) {
                ImageView listimage = convertView.findViewById(R.id.list_image);
                listimage.setImageResource(R.drawable.finish);
            }

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