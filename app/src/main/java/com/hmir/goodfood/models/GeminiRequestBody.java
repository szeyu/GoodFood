package com.hmir.goodfood.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the request body for the Gemini API.
 */
public class GeminiRequestBody {
    private final List<Content> contents;

    /**
     * Constructs a new GeminiRequestBody with the specified contents.
     *
     * @param contents The list of contents to include in the request body.
     */
    public GeminiRequestBody(List<Content> contents) {
        this.contents = new ArrayList<>(contents);
    }

    /**
     * Represents a content item in the request body.
     */
    public static class Content {
        private List<Part> parts;

        /**
         * Constructs a new Content with the specified parts.
         *
         * @param parts The list of parts to include in the content.
         */
        public Content(List<Part> parts) {
            this.parts = new ArrayList<>(parts);
        }
    }

    /**
     * Represents a part of the content.
     */
    public static class Part {
        private String text;
        private InlineData inlineData;

        /**
         * Constructs a new Part with the specified text.
         *
         * @param text The text of the part.
         */
        public Part(String text) {
            this.text = text;
        }

        /**
         * Constructs a new Part with the specified data and MIME type.
         *
         * @param data The data of the part.
         * @param mimeType The MIME type of the data.
         */
        public Part(String data, String mimeType) {
            this.inlineData = new InlineData(mimeType, data);
        }
    }

    /**
     * Represents inline data with a MIME type.
     */
    public static class InlineData {
        private String mimeType;
        private String data;

        /**
         * Constructs a new InlineData with the specified MIME type and data.
         *
         * @param mimeType The MIME type of the data.
         * @param data The data.
         */
        public InlineData(String mimeType, String data) {
            this.mimeType = mimeType;
            this.data = data;
        }
    }
}