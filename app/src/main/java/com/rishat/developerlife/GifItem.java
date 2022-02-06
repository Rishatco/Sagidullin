package com.rishat.developerlife;

public class GifItem {
    private String url;
    private String description;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public GifItem(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
