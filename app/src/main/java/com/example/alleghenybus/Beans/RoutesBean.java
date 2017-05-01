package com.example.alleghenybus.Beans;

import java.util.List;

/**
 * Created by LBB on 4/30/17.
 */

public class RoutesBean {
    private String routeId;
    private String routeName;
    private List<String> routeDirections;

    public RoutesBean(String routeId, String routeName){
        this.routeId = routeId;
        this.routeName = routeName;
    }

    public String getRouteId(){
        return routeId;
    }
    public void setRouteId(String routeId){
        this.routeId = routeId;
    }
    public String getRouteName(){
        return routeName;
    }
    public void setRouteName(String routeName){
        this.routeName = routeName;
    }

    public List<String> getRouteDirections() {
        return routeDirections;
    }

    public void setRouteDirections(List<String> routeDirections) {
        this.routeDirections = routeDirections;
    }
}
