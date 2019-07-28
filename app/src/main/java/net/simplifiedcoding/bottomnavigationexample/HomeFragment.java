package net.simplifiedcoding.bottomnavigationexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class HomeFragment extends Fragment {

    private GridView grid;
    private String[] text = {"Breakfast", "Lunch", "Tea", "Dinner", "Drinks", "Soup","Dessert","Snack"};
    private int[] imageId = {R.drawable.breakfast, R.drawable.lunch, R.drawable.tea, R.drawable.dinner, R.drawable.drinks, R.drawable.soup, R.drawable.dessert, R.drawable.snack};

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, null);

        CustomGrid adapter = new CustomGrid(getActivity(), text, imageId);
        grid = (GridView) view.findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //[+position] +的功用是?
                //Toast.makeText(getActivity(), "你選取了" + text[+position], Toast.LENGTH_SHORT).show();

                MenuFragment myFragment = new MenuFragment();
                Bundle bundle = new Bundle();
                bundle.putString("data",text[+position]);
                myFragment.setArguments(bundle);

                loadFragment(myFragment);
            }
        });

        return view;
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getFragmentManager ()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
