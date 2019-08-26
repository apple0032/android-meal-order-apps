package net.simplifiedcoding.bottomnavigationexample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Belal on 1/23/2018.
 */

public class PurchaseDetailsFragment extends Fragment {

    Context context;
    ProgressDialog dialog;
    ListView myCart;
    TextView pDate;
    TextView pTlMeal;
    TextView pTlQty;
    TextView pTlPrice;
    TextView pID;
    ImageView qrCode;
    ImageView bigQr;
    Integer counter = 0;
    Timer timer;
    Timer timer2;
    Button qrBtn;
    PopupWindow popupWindow;
    ConstraintLayout cons;
    Button closePopupBtn;
    String uid;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_purchase_d, null);

        Intent intent = getActivity().getIntent();
        int id = intent.getIntExtra("id", 0);
        uid = Integer.toString(id);

        //Toast.makeText(getActivity(), "You picked "+uid, Toast.LENGTH_LONG).show();

        myCart = (ListView) view.findViewById(R.id.menulist);
        pDate = (TextView) view.findViewById(R.id.menutitle);
        pTlMeal = (TextView) view.findViewById(R.id.total_item);
        pTlQty = (TextView) view.findViewById(R.id.total_qty);
        pTlPrice = (TextView) view.findViewById(R.id.total_price);
        pID = (TextView) view.findViewById(R.id.pid);
        qrCode = view.findViewById(R.id.qrcode);

        Integer userid = getActivity().getSharedPreferences("login_data", getActivity().MODE_PRIVATE)
                .getInt("userid", 0);

        DownloadTask task = new DownloadTask();
        task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/purchase/"+userid+"?q="+uid);




        qrBtn = (Button) view.findViewById(R.id.qrbtn);
        cons = (ConstraintLayout) view.findViewById(R.id.cons);

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
                JSONObject js = arr.getJSONObject(0);

                String pResult = js.getString("items");
                String pDatetime = js.getString("datetime");
                String pTotalMeal = js.getString("total_meal");
                String pTotalItem = js.getString("total_items");
                String pTotalPrice = js.getString("total_price");
                String status = js.getString("status");
                final Integer purchaseID = js.getInt("purchaseID");

                JSONArray arr2 = new JSONArray(pResult);
                Log.i("QQQQ", arr2.toString());
                arr = arr2;


                Integer leg = arr.length();
                //control checkout button

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


                PurchaseAdapter customAdapter = new PurchaseAdapter(getActivity(), arrayList);
                myCart.setAdapter(customAdapter);

                pDate.setText(pDatetime);
                pTlMeal.setText(pTotalMeal);
                pTlQty.setText(pTotalItem);
                pTlPrice.setText("$ "+pTotalPrice);
                pID.setText(Integer.toString(purchaseID));

                if(status.equals("done")){
                    qrBtn.setVisibility(View.INVISIBLE);
                    //qrCode.setVisibility(View.INVISIBLE);
                    qrCode.setImageResource(R.drawable.locked);
                } else {

                    //Generate QR Code
                    final String QRurl = "https://www.patrick-wied.at/static/qrgen/qrgen.php?r=12&a=0&content=http://www.apple0032.com/meal-order-api/checkout/" + purchaseID;
                    final Handler handler = new Handler();
                    Timer timer = new Timer();
                    TimerTask doAsynchronousTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                public void run() {
                                    try {
                                        Picasso.with(context)
                                                //.load("http://phpqrcode.sourceforge.net/qrsample.php?data=ThePurchaseIDIs"+purchaseID+"&ecc=H&matrix=15")
                                                .load(QRurl)
                                                .into(qrCode);

                                    } catch (Exception e) {
                                    }
                                }
                            });
                        }
                    };
                    timer.schedule(doAsynchronousTask, 0, 3000);


                    qrBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getActivity(), ".......", Toast.LENGTH_LONG).show();
                            //instantiate the popup.xml layout file
                            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View customView = layoutInflater.inflate(R.layout.popup, null);
                            closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
                            bigQr = (ImageView) customView.findViewById(R.id.bigQR);

                            //instantiate popup window
                            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                            //display the popup window
                            popupWindow.showAtLocation(cons, Gravity.CENTER, 0, 0);

                            Picasso.with(context)
                                    .load(QRurl)
                                    .into(bigQr);

                            //close the popup window on button click
                            closePopupBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                    timer2.cancel();
                                }
                            });

                            final Handler handler2 = new Handler();
                            timer2 = new Timer();
                            TimerTask doAsynchronousTask2 = new TimerTask() {
                                @Override
                                public void run() {
                                    handler2.post(new Runnable() {
                                        public void run() {
                                            try {
                                                counter++;
                                                String cc = Integer.toString(counter);

                                                DownloadTaskStatus task = new DownloadTaskStatus();
                                                task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/purchase-status/" + uid);

                                                if (counter > 20) {
                                                    timer2.cancel();
                                                } else {
                                                    Toast.makeText(getActivity(), "Scanning QR Code...", Toast.LENGTH_LONG).show();
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                                }
                            };
                            timer2.schedule(doAsynchronousTask2, 0, 5000);
                        }
                    });
                }


            } catch (Exception e) {

            }
        }
    }


    public class DownloadTaskStatus extends AsyncTask<String,Void,String> {

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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                Log.i("XXXXXXX",jsonObject.toString());

                String result = jsonObject.getString("result");
                Log.i("KKKKK",result);

                if(result.equals("done")){
                    timer2.cancel();

                    SharedPreferences pref = getActivity().getSharedPreferences("purchase", getActivity().MODE_PRIVATE);
                    pref.edit()
                            .putInt("ps", 1)
                            .commit();

                    Toast.makeText(getActivity(), "Successful Purchase!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }

            } catch (Exception e) {


            }

        }
    }
}
