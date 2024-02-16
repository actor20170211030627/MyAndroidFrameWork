package com.actor.jpush;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2024/2/16 on 10
 * @version 1.0
 */
public class GeofenceRegionInfo {
    public String geofence;
    public double longitude;
    public double latitude;

    public GeofenceRegionInfo(String geofence, double longitude, double latitude) {
        this.geofence = geofence;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
