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
import android.widget.NumberPicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class NotificationsFragment extends Fragment {

    Context context;
    ListView myProgram;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Toast.makeText(getActivity(), "This is waiting list...", Toast.LENGTH_SHORT).show();
        View view = inflater.inflate(R.layout.fragment_notifications, null);

        DownloadTask task = new DownloadTask();
        task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/purchase/1");

        myProgram = (ListView) view.findViewById(R.id.menulist);

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
            dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                String result = jsonObject.getString("result");
                JSONArray arr = new JSONArray(result);

                Log.i("Temp", arr.toString());

                ArrayList<WaitData> arrayList = new ArrayList<WaitData>();
                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    Integer pid = jsonPart.getInt("purchaseID");
                    Integer total_price = jsonPart.getInt("total_price");
                    Integer total_items = jsonPart.getInt("total_meal");
                    String datetime = jsonPart.getString("datetime");
                    String status = jsonPart.getString("status");
                    arrayList.add(new WaitData(datetime,status, total_price,total_items, pid));
                    //Log.i("TTTTTTTTTT", Integer.toString(total_price));
                }

                WaitAdapter customAdapter = new WaitAdapter(getActivity(), arrayList);
                myProgram.setAdapter(customAdapter);


            } catch (Exception e) {


            }

        }
    }
}
