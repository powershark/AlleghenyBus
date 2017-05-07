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

/**
 * Created by LBB on 5/7/17.
 */

public class PredictionActivity extends AppCompatActivity {
    private ListView list;
    private List<Map<String,Object>> mDataList = new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);
        list = (ListView)findViewById(R.id.predictionList);
        setUpData();
        SimpleAdapter listItemAdapter = new SimpleAdapter(this, mDataList, R.layout.prediction_item
                ,new String[]{"rtid", "des", "prd"}, new int[]{R.id.rtid, R.id.des, R.id.prd});
        list.setAdapter(listItemAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO ON CLICK

                Intent intent = new Intent(PredictionActivity.this, MapsActivity.class);
                setResult(android.app.Activity.RESULT_OK,intent);
                finish();
            }
        });
    }


    public void setUpData(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("rtid", "61A");
        map.put("des", "Inbound-North BLABLABLABLABLA");
        map.put("prd", "NOW");
        mDataList.add(map);

        map = new HashMap<String,Object>();
        map.put("rtid", "61C");
        map.put("des", "Outbound-North BLABLABLABLABLA");
        map.put("prd", "3 min");
        mDataList.add(map);

        map = new HashMap<String,Object>();
        map.put("rtid", "61D");
        map.put("des", "Outbound-South BLABLABLABLABLA");
        map.put("prd", "14 min");
        mDataList.add(map);
    }
}
