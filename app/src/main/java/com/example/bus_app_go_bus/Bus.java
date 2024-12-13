package com.example.bus_app_go_bus;


public class Bus {
    private String busNumber;
    private String route;

    public Bus(String busNumber, String route) {
        this.busNumber = busNumber;
        this.route = route;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getRoute() {
        return route;
    }
}


