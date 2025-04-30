package com.backend.Secure.Gateway.API.modal;

public class TrafficInfo {
    private String ipAddress;
    private int requestCount;
    private int limit;

    public TrafficInfo(String ipAddress, int requestCount, int limit) {
        this.ipAddress = ipAddress;
        this.requestCount = requestCount;
        this.limit = limit;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getLimit() {
        return limit;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
