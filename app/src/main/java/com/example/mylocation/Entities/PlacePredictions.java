package com.example.mylocation.Entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PlacePredictions {

    @SerializedName("status")
    private String status;

    @SerializedName("predictions")
    private List<PlacePrediction> placePredictions;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PlacePrediction> getPlacePredictions() {
        return this.placePredictions;
    }

    public void setPlacePredictions(List<PlacePrediction> placePredictions) {
        this.placePredictions = placePredictions;
    }
}