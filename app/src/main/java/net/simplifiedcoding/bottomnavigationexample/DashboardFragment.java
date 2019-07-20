package net.simplifiedcoding.bottomnavigationexample;

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
import android.widget.Toast;

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

    private GridView grid;
    private String[] text = {"Breakfast", "Lunch","Dinner", "Drinks", "Soup","Dessert","Snack"};
    private int[] imageId = {R.drawable.breakfast, R.drawable.lunch, R.drawable.dinner, R.drawable.drinks, R.drawable.soup, R.drawable.dessert, R.drawable.snack};

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, null);

        CustomGrid adapter = new CustomGrid(getActivity(), text, imageId);
        grid = (GridView) view.findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //[+position] +的功用是?
                Toast.makeText(getActivity(), "你選取了" + text[+position], Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
