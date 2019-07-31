package net.simplifiedcoding.bottomnavigationexample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

public class MenuFragment extends Fragment {

    Context context;
    ListView myProgram;
    ProgressDialog dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.menu, null);
        String title = (String)getArguments().get("data");
        Log.i("INFOX",title);

        TextView menutitle = (TextView) view.findViewById(R.id.menutitle);
        title = title.toUpperCase();
        menutitle.setText("-"+title+"-");

        DownloadTask task = new DownloadTask();
        task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/meal-category/"+title);

        myProgram = (ListView) view.findViewById(R.id.menulist);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("FUCK", "keyCode: " + keyCode);
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i("Backtomenu", "onKey Back listener is working!!!");
                    Intent intent = new Intent(getActivity(),MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);

                    return true;
                }
                return true;
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
                String result = jsonObject.getString("result");

                JSONArray arr = new JSONArray(result);

                Log.i("Temp", arr.toString());

                ArrayList<SubjectData> arrayList = new ArrayList<SubjectData>();
                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    Integer mealId = jsonPart.getInt("id");
                    String mealname = jsonPart.getString("name");
                    String mealimg = jsonPart.getString("img");
                    Integer mealprice = jsonPart.getInt("price");
                    arrayList.add(new SubjectData(mealId,mealname, mealimg,mealprice));
                }

//                arrayList.add(new SubjectData(1,"Ham & Cheese Burger Set", "https://upload.cc/i1/2019/07/18/PE4aDg.png",40));
//                arrayList.add(new SubjectData(2,"Ham & Egg Burger Set", "https://upload.cc/i1/2019/07/18/RAgnr4.png",50));
//                arrayList.add(new SubjectData(3,"Tuna Tomato Burger Set", "https://upload.cc/i1/2019/07/18/zxywHc.png",55));
//                arrayList.add(new SubjectData(4,"Teriyaki Chicken Burger Set", "https://upload.cc/i1/2019/07/18/vqcirU.png",60));
//                arrayList.add(new SubjectData(5,"Fish Burger", "https://upload.cc/i1/2019/07/19/SeVNWo.png",45));
//                arrayList.add(new SubjectData(6,"Grilled Ham & Cheese Thick Toast", "https://upload.cc/i1/2019/07/19/UZ3nJl.png",45));


                CustomAdapter customAdapter = new CustomAdapter(getActivity(), arrayList);
                myProgram.setAdapter(customAdapter);

            } catch (Exception e) {


            }

        }
    }
}
