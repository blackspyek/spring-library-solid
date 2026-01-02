package org.pollub.feedback.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Categories for feedback submissions.
 */
public enum FeedbackCategory {
    @JsonProperty("accessibility")
    ACCESSIBILITY,
    @JsonProperty("navigation")
    NAVIGATION,
    @JsonProperty("content")
    CONTENT,
    @JsonProperty("other")
    OTHER
}
