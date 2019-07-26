package net.simplifiedcoding.bottomnavigationexample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SearchView;
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

public class DashboardFragment extends Fragment {

//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        //just change the fragment_dashboard
//        //with the fragment you want to inflate
//        //like if the class is HomeFragment it should have R.layout.home_fragment
//        //if it is DashboardFragment it should have R.layout.fragment_dashboard
//        View view = inflater.inflate(R.layout.fragment_dashboard, null);
//
//
//        //String str = (String)getArguments().get("data");
//        //Log.i("INFOX",str);
//
//        ListView myProgram = (ListView) view.findViewById(R.id.myProgram);
//
//        final ArrayList<String> ProgramArray= new ArrayList<String>();
//
//        ProgramArray.add("PHP");
//        ProgramArray.add("Node.js");
//        ProgramArray.add("Java");
//        ProgramArray.add("CSS");
//
//        ProgramArray.add("KEN");
//        ProgramArray.add("IS");
//        ProgramArray.add("VERY");
//        ProgramArray.add("HANDSOME");
//        ProgramArray.add("AND");
//        ProgramArray.add("VERY");
//        ProgramArray.add("RICH");
//        ProgramArray.add("HAHA");
//        ProgramArray.add("KEN");
//        ProgramArray.add("LIKE");
//        ProgramArray.add("A");
//        ProgramArray.add("HERO");
//
//        System.out.println(ProgramArray.toString());
//
//        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_1,
//                ProgramArray);
//
//        myProgram.setAdapter(arrayAdapter);
//
//        myProgram.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(view.getContext(),"Hello " + ProgramArray.get(i), Toast.LENGTH_LONG).show();
//            }
//        });
//
//        return view;
//    }

    //ProgressDialog dialog;
    DialogInterface dialog;
    private GridView grid;
    private String[] text;
    private String[] imageId;
    DashGrid adapter;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, null);



        SearchView mSearchView = (SearchView) view.findViewById(R.id.searching);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getActivity(),query, Toast.LENGTH_LONG).show();
                DownloadTask task_search = new DownloadTask();
                task_search.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/meal?q="+query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                DownloadTask task_fetch = new DownloadTask();
                task_fetch.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/meal");
                return false;
            }
        });


        DownloadTask task = new DownloadTask();
        task.execute("http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/meal");

        grid = (GridView) view.findViewById(R.id.grid);


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
                String result = jsonObject.getString("result");
                JSONArray arr = new JSONArray(result);
                Log.i("Temp", arr.toString());

                Integer leg = arr.length();
                Log.i("Length", leg.toString());

                final String[] text = new String[leg];
                final String[] imageId = new String[leg];
                final Integer[] MealId = new Integer[leg];
                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String mealname = jsonPart.getString("name");
                    String mealimg = jsonPart.getString("img");
                    Integer meal_id = jsonPart.getInt("id");
                    text[i] = mealname;
                    imageId[i] = mealimg;
                    MealId[i] = meal_id;
                }

                //final String[] text = {"Breakfast", "Lunch","Dinner", "Drinks", "Soup","Dessert","Snack"};
                adapter = new DashGrid(getActivity(), text, imageId);
                grid.setAdapter(adapter);
                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //[+position] +的功用是?
                        //Toast.makeText(getActivity(), "你選取了" + MealId[+position], Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),DetailActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("id", MealId[+position]);
                        getActivity().startActivity(intent);
                    }
                });

            } catch (Exception e) {
                //Error handle
            }

        }
    }
}
