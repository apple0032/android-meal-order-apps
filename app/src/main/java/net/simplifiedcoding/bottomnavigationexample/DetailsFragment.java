package net.simplifiedcoding.bottomnavigationexample;

import android.app.ProgressDialog;
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

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Belal on 1/23/2018.
 */

public class DetailsFragment extends Fragment {

    ProgressDialog dialog;
    Context context;
    NumberPicker np;
    TextView totalprice;
    TextView singleprice;
    ImageView foodimg;
    TextView foodname;
    ElegantNumberButton numberButton;
    Integer userid;
    Button bookmark;
    Button unbookmark;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        int id = intent.getIntExtra("id", 0);
        final String food_uid = Integer.toString(id);

        //Get user id from sharePreferences
        userid = getActivity().getSharedPreferences("login_data", getActivity().MODE_PRIVATE)
                .getInt("userid", 0);

        DownloadTask task = new DownloadTask();
        task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/meal/"+food_uid+"/"+userid);

        //Toast.makeText(getActivity(), "You picked the fucking"+food_uid, Toast.LENGTH_LONG).show();

        View view = inflater.inflate(R.layout.fragment_details, null);
        numberButton = (ElegantNumberButton) view.findViewById(R.id.number_button);
        totalprice = (TextView) view.findViewById(R.id.totalprice);
        singleprice = (TextView) view.findViewById(R.id.singleprice);
        foodname = (TextView) view.findViewById(R.id.foodname);
        foodimg = (ImageView) view.findViewById(R.id.imageView2);

        bookmark = (Button) view.findViewById(R.id.bookmark);
        bookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PostTask addFav = new PostTask();

                addFav.execute(
                        "http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/fav",
                        "user_id=" + userid + "&meal_id=" + food_uid);

                //Toast.makeText(getActivity(),"Bookmarked "+food_uid, Toast.LENGTH_SHORT).show();
                bookmark.setVisibility(View.GONE);
                unbookmark.setVisibility(View.VISIBLE);
            }
        });

        unbookmark = (Button) view.findViewById(R.id.unbookmark);
        unbookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PostTask removeFav = new PostTask();

                removeFav.execute(
                        "http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/fav-rm",
                        "user_id=" + userid + "&meal_id=" + food_uid);

                //Toast.makeText(getActivity(),"Bookmarked "+food_uid, Toast.LENGTH_SHORT).show();
                bookmark.setVisibility(View.VISIBLE);
                unbookmark.setVisibility(View.GONE);
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


                return null;
            }
        }

        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(),
                    "Loading", "Loading...",true);
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
                String fav = jsonObject.getString("fav");

                Log.i("Temp", jsonObject.toString());

                //Init value
                singleprice.setText(singlepricevalue);
                totalprice.setText(totalpricevalue);
                foodname.setText(food_name);
                Picasso.with(context)
                        .load(food_img)
                        .into(foodimg);


                numberButton.setNumber(Integer.toString(ex_qty));
                //String qty = numberButton.getNumber();
                numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        Integer total = newValue * Integer.parseInt(newsinglepricevalue);
                        String qty = Integer.toString(total);

                        totalprice.setText(qty);
                    }
                });

                Log.i("FAVFAVFAV", fav);
                if(fav.equals("true")){
                    bookmark.setVisibility(View.GONE);
                    unbookmark.setVisibility(View.VISIBLE);
                } else {
                    bookmark.setVisibility(View.VISIBLE);
                    unbookmark.setVisibility(View.GONE);
                }


            } catch (Exception e) {


            }

            dialog.dismiss();
        }
    }

    public class PostTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
            String result = "";

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.addRequestProperty("User-Agent", USER_AGENT);
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                connection.setDoOutput(true);
                DataOutputStream write = new DataOutputStream(connection.getOutputStream());

                write.writeBytes(params[1]);
                write.flush();
                write.close();

                // Response: 400
                Log.e("Response", connection.getResponseCode() + "");

            } catch (Exception e) {
                Log.e(e.toString(), "Something with request");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);


            } catch (Exception e) {


            }

        }
    }
}
