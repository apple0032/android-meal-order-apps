package net.simplifiedcoding.bottomnavigationexample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Belal on 1/23/2018.
 */

public class DetailsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details, null);
        NumberPicker np = (NumberPicker) view.findViewById(R.id.np);
        final TextView totalprice = (TextView) view.findViewById(R.id.totalprice);

        //Set the minimum value of NumberPicker
        np.setMinValue(1);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(10);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(true);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                Integer total = newVal * 60; //hardcoded temporary
                String qty = Integer.toString(total);
                //Toast.makeText(getActivity(),qty, Toast.LENGTH_LONG).show();

                totalprice.setText(qty);
            }
        });

        Button button = (Button) view.findViewById(R.id.bookmark);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),"Bookmarked!", Toast.LENGTH_SHORT).show();
            }
        });

        Button button2 = (Button) view.findViewById(R.id.addCart);
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),"Added to cart!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
