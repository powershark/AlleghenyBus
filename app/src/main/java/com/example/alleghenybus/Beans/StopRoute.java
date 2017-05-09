package com.example.alleghenybus.Beans;

import java.io.Serializable;

/**
 * Created by alabhyafarkiya on 08/05/17.
 */

public class StopRoute implements Serializable {
    private String routeId;
    private String eta;
    private String direction;
    private String arrStop ;
    private String destStop;
    private String busTime;
    private String vid;


    public StopRoute(String routeId, String eta, String direction, String arrStop, String destStop, String busTime) {
        this.routeId = routeId;
        this.eta = eta;
        this.direction = direction;
        this.arrStop = arrStop;
        this.destStop = destStop;
        this.busTime = busTime;
    }

    public StopRoute(){

    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public StopRoute(String routeId, String eta, String direction, String stop) {
        this.routeId = routeId;
        this.eta = eta;
        this.direction = direction;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getArrStop() {
        return arrStop;
    }

    public void setArrStop(String arrStop) {
        this.arrStop = arrStop;
    }

    public String getBusTime() {
        return busTime;
    }

    public void setBusTime(String busTime) {
        this.busTime = busTime;
    }

    public String getDestStop() {
        return destStop;
    }

    public void setDestStop(String destStop) {
        this.destStop = destStop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StopRoute stopRoute = (StopRoute) o;

        if (routeId != null ? !routeId.equals(stopRoute.routeId) : stopRoute.routeId != null)
            return false;
        if (direction != null ? !direction.equals(stopRoute.direction) : stopRoute.direction != null)
            return false;
        if (vid != null ? !vid.equals(stopRoute.vid) : stopRoute.vid != null)
            return false;
        return true;
    }

}
