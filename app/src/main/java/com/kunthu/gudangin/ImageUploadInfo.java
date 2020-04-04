package com.kunthu.gudangin;

public class ImageUploadInfo {

    String title;
    String description;
    String image;
    String search;
    String uid;

    public ImageUploadInfo() {
    }

    public ImageUploadInfo(String title, String description, String image, String search, String uid) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.search = search;
        this.uid = uid;

    }

    public String getuid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getSearch() {
        return search;
    }


}
