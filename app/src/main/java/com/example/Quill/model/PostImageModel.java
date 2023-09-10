package com.example.Quill.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class PostImageModel {
    private String imageURL,id,description,uid;
    @ServerTimestamp
    private Timestamp timestamp;


    public PostImageModel() {
    }


    public PostImageModel(String imageURL, String id, String description, String uid,Timestamp timestamp) {
        this.imageURL = imageURL;
        this.id = id;
        this.description = description;
        this.timestamp = timestamp;
        this.uid =uid;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
