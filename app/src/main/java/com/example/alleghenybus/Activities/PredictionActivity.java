package com.example.alleghenybus.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.alleghenybus.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by LBB on 5/7/17.
 */

public class PredictionActivity extends AppCompatActivity {
    private ListView list;
    private List<Map<String,Object>> mDataList = new ArrayList<Map<String,Object>>();
    String xmlResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        Intent intent = getIntent();
        xmlResponse = intent.getStringExtra("xmlResponse");
        list = (ListView)findViewById(R.id.predictionList);
        try {
            setUpData();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
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


    public void setUpData() throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        Map<String,Object> map = null;
        String text = null;
        Log.e("xml", xmlResponse);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput( new StringReader(xmlResponse));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagname = xpp.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagname.equalsIgnoreCase("prd"))
                        map = new HashMap<>();
                    break;

                case XmlPullParser.TEXT:
                    text = xpp.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagname.equalsIgnoreCase("rt")) {
                        map.put("rtid", text);
                    } else if (tagname.equalsIgnoreCase("rtdir")) {
                        map.put("des", text);
                    } else if (tagname.equalsIgnoreCase("prdctdn")){
                        if (text.equalsIgnoreCase("due"))
                            map.put("prd", text);
                        else map.put("prd", text + " mins");
                    }else if (tagname.equalsIgnoreCase("prd")){
                        mDataList.add(map);
                    }
                    else if (tagname.equalsIgnoreCase("error")){
                        map = new HashMap<>();
                        map.put("des","No routes avalaible");
                        mDataList.add(map);
                    }

                    break;

                default:
                    break;
            }

            eventType = xpp.next();

        }
    }
}
