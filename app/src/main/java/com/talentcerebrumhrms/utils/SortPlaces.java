package com.talentcerebrumhrms.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

public class SortPlaces implements Comparator<Places> {
    LatLng currentLoc;

    public SortPlaces(LatLng current){
        currentLoc = current;
    }
    @Override
    public int compare(final Places place1, final Places place2) {
        double lat1 = place1.latlng.latitude;
        double lon1 = place1.latlng.longitude;
        double lat2 = place2.latlng.latitude;
        double lon2 = place2.latlng.longitude;

        double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
        double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
        System.out.println(" distanceTo Places 1 : "+distanceToPlace1);
        System.out.println(" distanceTo Places 2 : "+distanceToPlace2);
        return (int) (distanceToPlace1 - distanceToPlace2);
    }

    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }
}