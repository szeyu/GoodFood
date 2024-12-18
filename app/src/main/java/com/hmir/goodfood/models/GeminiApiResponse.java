package com.hmir.goodfood.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the response from the Gemini API.
 */
public class GeminiApiResponse {
    private List<Candidate> candidates;

    /**
     * Gets the list of candidates from the response.
     *
     * @return The list of candidates.
     */
    public List<Candidate> getCandidates() {
        return candidates;
    }

    /**
     * Sets the list of candidates in the response.
     *
     * @param candidates The list of candidates.
     */
    public void setCandidates(List<Candidate> candidates) {
        this.candidates = new ArrayList<>(candidates);
    }

    /**
     * Represents a candidate in the response.
     */
    public static class Candidate {
        private Content content;

        /**
         * Gets the content of the candidate.
         *
         * @return The content.
         */
        public Content getContent() {
            return content;
        }

        /**
         * Sets the content of the candidate.
         *
         * @param content The content.
         */
        public void setContent(Content content) {
            this.content = content;
        }

        /**
         * Represents the content of a candidate.
         */
        public static class Content {
            private List<Part> parts;

            /**
             * Gets the list of parts in the content.
             *
             * @return The list of parts.
             */
            public List<Part> getParts() {
                return parts;
            }

            /**
             * Sets the list of parts in the content.
             *
             * @param parts The list of parts.
             */
            public void setParts(List<Part> parts) {
                this.parts = new ArrayList<>(parts);
            }

            /**
             * Represents a part of the content.
             */
            public static class Part {
                private String text;

                /**
                 * Gets the text of the part.
                 *
                 * @return The text.
                 */
                public String getText() {
                    return text;
                }

                /**
                 * Sets the text of the part.
                 *
                 * @param text The text.
                 */
                public void setText(String text) {
                    this.text = text;
                }
            }
        }
    }
}

