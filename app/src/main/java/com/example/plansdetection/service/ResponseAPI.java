package com.example.plansdetection.service;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseAPI implements Serializable {

    @SerializedName("article_id")
    private String articleId;

    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("description")
    private String description;

    @SerializedName("pubDate")
    private String pubDate;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("source_id")
    private String sourceId;

    @SerializedName("source_url")
    private String sourceUrl;

    @SerializedName("language")
    private String language;

    @SerializedName("country")
    private List<String> country;

    @SerializedName("category")
    private List<String> category;

    public ResponseAPI(String articleId, String title, String link, String description, String pubDate, String imageUrl, String sourceId, String sourceUrl, String language, List<String> country, List<String> category) {
        this.articleId = articleId;
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.imageUrl = imageUrl;
        this.sourceId = sourceId;
        this.sourceUrl = sourceUrl;
        this.language = language;
        this.country = country;
        this.category = category;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getCountry() {
        return country;
    }

    public List<String> getCategory() {
        return category;
    }
}
