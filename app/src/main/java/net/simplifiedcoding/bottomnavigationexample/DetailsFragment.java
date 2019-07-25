package net.simplifiedcoding.bottomnavigationexample;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Belal on 1/23/2018.
 */

public class DetailsFragment extends Fragment {

    Context context;
    NumberPicker np;
    TextView totalprice;
    TextView singleprice;
    ImageView foodimg;
    TextView foodname;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        int id = intent.getIntExtra("id", 0);
        final String food_uid = Integer.toString(id);

        DownloadTask task = new DownloadTask();
        task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/meal/"+food_uid+"/1");

        //Toast.makeText(getActivity(), "You picked the fucking"+food_uid, Toast.LENGTH_LONG).show();

        View view = inflater.inflate(R.layout.fragment_details, null);
        np = (NumberPicker) view.findViewById(R.id.np);
        totalprice = (TextView) view.findViewById(R.id.totalprice);
        singleprice = (TextView) view.findViewById(R.id.singleprice);
        foodname = (TextView) view.findViewById(R.id.foodname);
        foodimg = (ImageView) view.findViewById(R.id.imageView2);

        Button button = (Button) view.findViewById(R.id.bookmark);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),"Bookmarked "+food_uid, Toast.LENGTH_SHORT).show();
            }
        });

        Button button2 = (Button) view.findViewById(R.id.addCart);
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String total_price = totalprice.getText().toString();
                //Toast.makeText(getActivity(),"Added to cart "+food_uid, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(),OrderActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("id", food_uid);
                intent.putExtra("total", total_price);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();

                //Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String exist = jsonObject.getString("exist");

                int ex_qty = 1;
                String totalpricevalue;
                String singlepricevalue = jsonObject.getString("price");
                if(!exist.equals("null")){
                    String exist_qty = jsonObject.getJSONObject("exist").getString("qty");
                    ex_qty = Integer.parseInt(exist_qty);

                    Integer exist_total = jsonObject.getJSONObject("exist").getInt("total_price");
                    totalpricevalue = exist_total.toString();
                } else {
                    totalpricevalue = jsonObject.getString("price");
                    singlepricevalue = totalpricevalue;
                }

                final String newsinglepricevalue = singlepricevalue;

                String food_img = jsonObject.getString("img");
                String food_name = jsonObject.getString("name");

                Log.i("Temp", jsonObject.toString());

                //Init value
                singleprice.setText(singlepricevalue);
                totalprice.setText(totalpricevalue);
                foodname.setText(food_name);
                Picasso.with(context)
                        .load(food_img)
                        .into(foodimg);

                //Set the minimum value of NumberPicker
                np.setMinValue(1);
                //Specify the maximum value/number of NumberPicker
                np.setMaxValue(10);

                np.setValue(ex_qty);

                //Gets whether the selector wheel wraps when reaching the min/max value.
                np.setWrapSelectorWheel(true);

                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                        Integer total = newVal * Integer.parseInt(newsinglepricevalue);; //hardcoded temporary
                        String qty = Integer.toString(total);
                        //Toast.makeText(getActivity(),qty, Toast.LENGTH_LONG).show();

                        totalprice.setText(qty);
                    }
                });

            } catch (Exception e) {


            }

        }
    }
}
