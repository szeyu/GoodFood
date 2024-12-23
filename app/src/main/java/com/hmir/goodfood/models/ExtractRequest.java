package com.hmir.goodfood.models;

/**
 * The {@code ExtractRequest} class represents a request object used for sending image data to a server
 * for the purpose of extracting ingredients from the provided image. The image is encoded in Base64 format.
 */
public class ExtractRequest {

    private String image_data; // Base64-encoded image data

    /**
     * Constructor for creating an {@code ExtractRequest} with the given image data.
     *
     * @param image_data A string containing the Base64-encoded image data.
     */
    public ExtractRequest(String image_data) {
        this.image_data = image_data;
    }

    /**
     * Getter for the image data.
     *
     * @return A string representing the Base64-encoded image data.
     */
    public String getImageData() {
        return image_data;
    }

    /**
     * Setter for the image data.
     *
     * @param image_data A string representing the new Base64-encoded image data.
     */
    public void setImageData(String image_data) {
        this.image_data = image_data;
    }
}
