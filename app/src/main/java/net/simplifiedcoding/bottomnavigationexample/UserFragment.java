package net.simplifiedcoding.bottomnavigationexample;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
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

public class UserFragment extends Fragment {

    ProgressDialog dialog;
    TextView email;
    TextView password;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String username = getActivity().getSharedPreferences("login_data", getActivity().MODE_PRIVATE)
                .getString("username", "null");
        Integer userid = getActivity().getSharedPreferences("login_data", getActivity().MODE_PRIVATE)
                .getInt("userid", 0);

        if(username.equals("null")) {

            view = inflater.inflate(R.layout.fragment_user, null);

            Button button = (Button) view.findViewById(R.id.btn_login);

            email = (TextView) view.findViewById(R.id.input_email);
            password = (TextView) view.findViewById(R.id.input_password);


            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String input_email = email.getText().toString();
                    String input_password = password.getText().toString();

                    DownloadTask loginAPI = new DownloadTask();

                    loginAPI.execute(
                            "http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/login",
                            "email=" + input_email + "&password=" + input_password);
                }
            });
        } else {
            view = inflater.inflate(R.layout.fragment_logined, null);

            TextView duserid = (TextView) view.findViewById(R.id.userid);
            duserid.setText(Integer.toString(userid));

            TextView dusername = (TextView) view.findViewById(R.id.username);
            dusername.setText(username);

            Button button = (Button) view.findViewById(R.id.logoutbtn);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"You had been logged out!",Toast.LENGTH_SHORT).show();
                    getActivity().getSharedPreferences("login_data", 0).edit().clear().commit();
                    Intent intent = new Intent(getActivity(),UserActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            });
        }

        return view;
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

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

                int responseCode=connection.getResponseCode();

                String response = "";
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

                Log.i("CCCCC",response.toString());

                JSONObject jsonObject = new JSONObject(response);
                String login_result = jsonObject.getString("result");

                Log.i("ZZZZZZZZZZZ",login_result);
                if(login_result.equals("fail")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Login fail. Please try again !",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //success
                    String infoString = jsonObject.getString("info");
                    JSONObject info = new JSONObject(infoString);
                    final String username = info.getString("name");
                    Integer userid = info.getInt("id");

                    SharedPreferences pref = getActivity().getSharedPreferences("login_data", getActivity().MODE_PRIVATE);
                    pref.edit()
                            .putString("username", username)
                            .putInt("userid", userid)
                            .commit();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Login success. Hi "+username+".",Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent intent = new Intent(getActivity(),UserActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }


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
                Log.i("CCCCC",jsonObject.toString());

            } catch (Exception e) {


            }

        }
    }
}
