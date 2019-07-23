package com.talentcerebrumhrms.utils;

import com.google.android.gms.maps.model.LatLng;

public class Places{
    public String name;
    public LatLng latlng;

    public Places(String name, LatLng latlng) {
        this.name = name;
        this.latlng = latlng;
    }
}
