package com.example.alleghenybus.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.alleghenybus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectRouteActivity extends AppCompatActivity{
    private ListView list;
    private List<Map<String,Object>> mDataList = new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);
        list = (ListView)findViewById(R.id.routeList);
        setUpData();
        SimpleAdapter listItemAdapter = new SimpleAdapter(this, mDataList, R.layout.route_item
        ,new String[]{"routeId", "stopName", "eta", "image"}, new int[]{R.id.routeId, R.id.stopName, R.id.eta, R.id.busImage});
        list.setAdapter(listItemAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO ON CLICK

                Intent intent = new Intent(SelectRouteActivity.this, MapsActivity.class);
                setResult(android.app.Activity.RESULT_OK,intent);
                finish();
            }
        });
    }


    public void setUpData(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("routeId", "61A");
        map.put("stopName", "From Fifth at Craig");
        map.put("eta", "Arriving in 5 mins");
        map.put("image", R.drawable.bus_stop);
        mDataList.add(map);

        map = new HashMap<String,Object>();
        map.put("routeId", "71A");
        map.put("stopName", "From Centre at Craig");
        map.put("eta", "Arriving in 13 mins");
        map.put("image", R.drawable.bus_stop);
        mDataList.add(map);


    }
}
