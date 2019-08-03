package net.simplifiedcoding.bottomnavigationexample;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Belal on 1/23/2018.
 */

public class CartFragment extends Fragment {

    Context context;
    ProgressDialog dialog;
    ListView myCart;
    Button checkOutBtn;
    TextView tlprice;
    TextView tlqty;
    Integer userid;
    private Button dateButton;
    private TextView dateText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, null);

        //Get user id from sharePreferences
        userid = getActivity().getSharedPreferences("login_data", getActivity().MODE_PRIVATE)
                .getInt("userid", 0);
        if(userid == 0){
            //Go to login page
            Toast.makeText(getActivity(),"Please login or register first.",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), UserActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        DownloadTask task = new DownloadTask();
        task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/cart/"+userid);

        myCart = (ListView) view.findViewById(R.id.menulist);
        checkOutBtn = (Button) view.findViewById(R.id.checkoutbtn);
        tlprice = (TextView) view.findViewById(R.id.totalprice);
        tlqty = (TextView) view.findViewById(R.id.totalqty);
        dateText = (TextView)view.findViewById(R.id.dateText);
        dateButton = (Button)view.findViewById(R.id.dateButton);

        dateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                // Create a new instance of TimePickerDialog and return it
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener(){

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateText.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false).show();
            }
        });

        checkOutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String picktime = dateText.getText().toString();
                if(!picktime.equals("-")) {

                    //Execute create order API
                    PostTask startPurchase = new PostTask();
                    startPurchase.execute(
                            "http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/purchase",
                            "user_id="+userid+"&waiting="+picktime);

                    //Intent to waiting page to display order..
                    Intent intent = new Intent(getActivity(),WaitingActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().startActivity(intent);

                } else {
                    Toast.makeText(getActivity(), "Please select a time.", Toast.LENGTH_SHORT).show();
                }
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
            dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                Log.i("CCCCC",jsonObject.toString());

                String result = jsonObject.getString("result");
                JSONArray arr = new JSONArray(result);
                Log.i("QQQQ", arr.toString());

                Integer leg = arr.length();
                //control checkout button
                if(leg < 1) {
                    checkOutBtn.setVisibility(View.GONE);
                }
                Log.i("Length", leg.toString());

                ArrayList<CartData> arrayList = new ArrayList<CartData>();
                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    Integer mealId = jsonPart.getInt("id");
                    String mealname = jsonPart.getString("name");
                    String mealimg = jsonPart.getString("img");
                    Integer mealprice = jsonPart.getInt("total_price");
                    Integer mealqty = jsonPart.getInt("qty");
                    arrayList.add(new CartData(mealId,mealname, mealimg,mealprice,mealqty));
                }

                //Toast.makeText(getActivity(),"你有"+leg.toString()+"個Order",Toast.LENGTH_SHORT).show();

                CartAdapter customAdapter = new CartAdapter(getActivity(), arrayList);
                myCart.setAdapter(customAdapter);

                String totalprice = jsonObject.getString("total");
                tlprice.setText("$ "+totalprice);

                String totalqty = jsonObject.getString("qty");
                tlqty.setText(totalqty);

            } catch (Exception e) {


            }

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
