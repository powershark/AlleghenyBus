package com.example.alleghenybus.Beans;

import java.util.List;
import java.util.Map;


public class StopsBean {
    private String stpId;
    private String stpName;
    private double latitute;
    private double lontitute;
    private List<String> routes;

    public StopsBean(String stpId, String stpName, double latitute, double lontitute, List<String> routes){
        this.stpId = stpId;
        this.stpName = stpName;
        this.latitute = latitute;
        this.lontitute = lontitute;
        this.routes = routes;
    }

    public double getLatitute() {
        return latitute;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public double getLontitute() {
        return lontitute;
    }

    public void setLontitute(double lontitute) {
        this.lontitute = lontitute;
    }

    public String getStpName() {
        return stpName;
    }

    public void setStpName(String stpName) {
        this.stpName = stpName;
    }

    public String getStpId() {
        return stpId;
    }

    public void setStpId(String stpId) {
        this.stpId = stpId;
    }


    public List<String> getRoutes() {
        return routes;
    }

    public void setRoutes(List<String> routes) {
        this.routes = routes;
    }
}
