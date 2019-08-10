package net.simplifiedcoding.bottomnavigationexample;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class FavAdapter implements ListAdapter {
    ArrayList<SubjectData> arrayList;
    Context context;
    ImageButton dislove;

    public FavAdapter(Context context, ArrayList<SubjectData> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SubjectData subjectData = arrayList.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_fav, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,DetailActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("id", subjectData.Id);
                    context.startActivity(intent);
                }
            });

            TextView title = convertView.findViewById(R.id.title);
            ImageView image = convertView.findViewById(R.id.list_image);
            title.setText(subjectData.SubjectName);


            Picasso.with(context)
                    .load(subjectData.Image)
                    .into(image);

            final Integer userid = context.getSharedPreferences("login_data", context.MODE_PRIVATE)
                    .getInt("userid", 0);
            dislove = convertView.findViewById(R.id.dislove);
            dislove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(),"deleted "+userid, Toast.LENGTH_LONG).show();
                    PostTask startPurchase = new PostTask();
                    startPurchase.execute(
                            "http://ec2-18-216-196-249.us-east-2.compute.amazonaws.com/meal-order-api/fav-rm",
                            "user_id="+userid+"&meal_id="+subjectData.Id);

                    Intent intent = new Intent(context,FavActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                }
            });


        }
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
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