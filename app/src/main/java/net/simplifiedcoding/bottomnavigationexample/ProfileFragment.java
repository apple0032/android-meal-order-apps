package net.simplifiedcoding.bottomnavigationexample;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Belal on 1/23/2018.
 */

public class ProfileFragment extends Fragment {

    Context context;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, null);

        //Toast.makeText(getActivity(),"xxxx",Toast.LENGTH_SHORT).show();

        DownloadTask task = new DownloadTask();
        task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/cart/1");


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

        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(),
                    "Loading", "Loading...",true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                Log.i("CCCCC",jsonObject.toString());

                String result = jsonObject.getString("result");
                JSONArray arr = new JSONArray(result);
                Log.i("QQQQ", arr.toString());

                Integer leg = arr.length();
                Log.i("Length", leg.toString());

//                final String[] text = new String[leg];
//                final String[] imageId = new String[leg];
//                final Integer[] MealId = new Integer[leg];
//                for (int i=0; i < arr.length(); i++) {
//                    JSONObject jsonPart = arr.getJSONObject(i);
//                    String mealname = jsonPart.getString("name");
//                    String mealimg = jsonPart.getString("img");
//                    Integer meal_id = jsonPart.getInt("id");
//                    text[i] = mealname;
//                    imageId[i] = mealimg;
//                    MealId[i] = meal_id;
//                }
                Toast.makeText(getActivity(),"你有"+leg.toString()+"個Order, 但係未撚做完",Toast.LENGTH_SHORT).show();

            } catch (Exception e) {


            }

        }
    }
}
