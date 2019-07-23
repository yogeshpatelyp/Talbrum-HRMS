package com.talentcerebrumhrms.models;

/**
 * Created by saransh on 10/03/18.
 */

public class Data {
    private String longitude;

    private String latitude;

    private String description;

    private String name;

    private String city;

    public String getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude ()
    {
        return longitude;
    }

    public void setLongitude (String tags)
    {
        this.longitude = longitude;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCity ()
    {
        return city;
    }

    public void setCity (String city)
    {
        this.city = city;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [latitude = "+latitude+", longitude = "+longitude+", description = "+description+", name = "+name+", city = "+city+"]";
    }
}
