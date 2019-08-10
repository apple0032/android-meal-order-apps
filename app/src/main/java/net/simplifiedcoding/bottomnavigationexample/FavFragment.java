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


public class FavFragment extends Fragment {

    ProgressDialog dialog;
    Integer userid;
    ListView myFav;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favourite, null);
        myFav = (ListView) view.findViewById(R.id.menulist);

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
        task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/fav/"+userid);

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
                String result = jsonObject.getString("fav");

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

                FavAdapter customAdapter = new FavAdapter(getActivity(), arrayList);
                myFav.setAdapter(customAdapter);


            } catch (Exception e) {


            }

        }
    }
}
