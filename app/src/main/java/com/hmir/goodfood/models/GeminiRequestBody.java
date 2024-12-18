package com.hmir.goodfood.models;

import java.util.List;

public class GeminiRequestBody {
    private List<Content> contents;

    public GeminiRequestBody(List<Content> contents) {
        this.contents = contents;
    }

    public static class Content {
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        private String text;
        private InlineData inlineData;

        public Part(String text) {
            this.text = text;
        }

        public Part(String data, String mimeType) {
            this.inlineData = new InlineData(mimeType, data);
        }
    }

    public static class InlineData {
        private String mimeType;
        private String data;

        public InlineData(String mimeType, String data) {
            this.mimeType = mimeType;
            this.data = data;
        }
    }
}