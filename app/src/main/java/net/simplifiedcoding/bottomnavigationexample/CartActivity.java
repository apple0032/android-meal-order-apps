package net.simplifiedcoding.bottomnavigationexample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//implement the interface OnNavigationItemSelectedListener in your activity class
public class CartActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //loading the default fragment
        loadFragment(new CartFragment());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        Intent intent = this.getIntent();
        int id = intent.getIntExtra("id", 0);
        String uid = Integer.toString(id);

        Integer userid = this.getSharedPreferences("login_data", MODE_PRIVATE)
                .getInt("userid", 0);

        DownloadTask downloadInfoOfWeather = new DownloadTask();

        downloadInfoOfWeather.execute(
                "http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/delete",
                "user_id="+userid+"&meal_id="+uid);
        //Toast.makeText(this, "You picked "+uid, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_dashboard:
                fragment = new DashboardFragment();
                break;

            case R.id.navigation_notifications:
                fragment = new NotificationsFragment();
                break;

            case R.id.navigation_profile:
                fragment = new UserFragment();
                break;

            case R.id.navigation_cart:
                fragment = new CartFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.cart:

                Intent intent = new Intent(this, FavActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

                return true;
            case R.id.help:

                Intent intent2 = new Intent(this, InfoActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent2);

                return true;
            default:
                return false;
        }
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
