package com.example.alleghenybus.Xmlparser;

import android.util.Xml;

import com.example.alleghenybus.Beans.StopsBean;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LBB on 4/30/17.
 */

public class GetStopsXmlParser {
    private static final String ns = null;
    public List<StopsBean> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    private List<StopsBean> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<StopsBean> stops = new ArrayList<StopsBean>();

        parser.require(XmlPullParser.START_TAG, ns, "bustime-response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("stop")) {
                stops.add(readStops(parser));
            } else {
                skip(parser);
            }
        }
        return stops;
    }

    private StopsBean readStops(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "stop");
        String stpId = null;
        String stpName = null;
        double latitute = 0;
        double lontitute = 0;
        List<String> routes = new ArrayList<String>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "stpid":
                    stpId = readStpid(parser);
                    break;
                case "stpnm":
                    stpName = readStpnm(parser);
                    break;
                case "lat":
                    latitute = Double.parseDouble(readLat(parser));
                    break;
                case "lon":
                    lontitute = Double.parseDouble(readLon(parser));
                    break;
                case "rt":
                    routes.add(readRoutes(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new StopsBean(stpId, stpName, latitute, lontitute, routes);
    }

    private String readStpid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "stpid");
        String stpid = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "stpid");
        return stpid;
    }

    private String readStpnm(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "stpnm");
        String stpnm = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "stpnm");
        return stpnm;
    }

    private String readLat(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lat");
        String lat = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lat");
        return lat;
    }

    private String readLon(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lon");
        String lon = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lon");
        return lon;
    }

    private String readRoutes(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "rt");
        String route = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "rt");
        return route;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
