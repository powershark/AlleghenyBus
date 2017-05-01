package com.example.alleghenybus.beans;

/**
 * Created by LBB on 4/30/17.
 */

public class StopsBean {
    private String stpId;
    private String stpName;
    private double latitute;
    private double lontitute;

    public StopsBean(String stpId, String stpName, double latitute, double lontitute){
        this.stpId = stpId;
        this.stpName = stpName;
        this.latitute = latitute;
        this.lontitute = lontitute;
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
}
