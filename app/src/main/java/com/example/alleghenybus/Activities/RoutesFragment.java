package com.example.alleghenybus.Activities;

/**
 * Created by alabhyafarkiya on 06/05/17.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alleghenybus.R;

public class RoutesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        View v = inflater.inflate(R.layout.route_fragment_layout, null);

        return v;
    }
}