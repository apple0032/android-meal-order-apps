package net.simplifiedcoding.bottomnavigationexample;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

/**
 * Created by Belal on 1/23/2018.
 */

public class MenuFragment extends Fragment {

    Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.menu, null);
        String title = (String)getArguments().get("data");
        Log.i("INFOX",title);

        TextView menutitle = (TextView) view.findViewById(R.id.menutitle);
        title = title.toUpperCase();
        menutitle.setText("-"+title+"-");

        ListView myProgram = (ListView) view.findViewById(R.id.menulist);

        ArrayList<SubjectData> arrayList = new ArrayList<SubjectData>();
        arrayList.add(new SubjectData(1,"Ham & Cheese Burger Set", "https://upload.cc/i1/2019/07/18/PE4aDg.png",40));
        arrayList.add(new SubjectData(2,"Ham & Egg Burger Set", "https://upload.cc/i1/2019/07/18/RAgnr4.png",50));
        arrayList.add(new SubjectData(3,"Tuna Tomato Burger Set", "https://upload.cc/i1/2019/07/18/zxywHc.png",55));
        arrayList.add(new SubjectData(4,"Teriyaki Chicken Burger Set", "https://upload.cc/i1/2019/07/18/vqcirU.png",60));
        arrayList.add(new SubjectData(5,"Fish Burger", "https://upload.cc/i1/2019/07/19/SeVNWo.png",45));
        arrayList.add(new SubjectData(6,"Grilled Ham & Cheese Thick Toast", "https://upload.cc/i1/2019/07/19/UZ3nJl.png",45));


        CustomAdapter customAdapter = new CustomAdapter(getActivity(), arrayList);
        myProgram.setAdapter(customAdapter);

//        myProgram.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(view.getContext(),"Hello " + (i), Toast.LENGTH_LONG).show();
//            }
//        });
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

}
