package com.uxcasuals.waves.models;

/**
 * Created by Dhakchianandan on 04/09/15.
 */
public class RadioStation {
    private String name;
    private String url;
    private String logo;

    public RadioStation() {
    }

    public RadioStation(String name, String url, String logo) {
        this.name = name;
        this.url = url;
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "RadioStation{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
