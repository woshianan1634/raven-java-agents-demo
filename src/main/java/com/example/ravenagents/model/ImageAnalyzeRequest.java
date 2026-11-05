package com.example.ravenagents.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageAnalyzeRequest {

    /** Optional image URL. */
    private String imageUrl;
    /** Optional base64-encoded image (with or without data-URI prefix). */
    private String imageBase64;
    /** Free-text description / question about the image. */
    private String description;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
