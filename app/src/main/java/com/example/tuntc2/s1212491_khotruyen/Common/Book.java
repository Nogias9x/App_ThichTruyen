package com.example.tuntc2.s1212491_khotruyen.Common;

import java.io.Serializable;

public class Book implements Serializable {
    public static final String KEY = "story";
    public static final String TABLE_NAME = "story";
    public static final String KEY_ID = "StoryID";
    public static final String KEY_COVER = "Cover";
    public static final String KEY_AUTHOR = "Author";
    public static final String KEY_VIEW = "View";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_CHAPTER = "Status";
    public static final String KEY_TITLE = "Title";

    public static final int STYLE_ONLINE = 0;
    public static final int STYLE_OFFLINE = 1;

    private int mId;
    private String mCoverUrl;
    private String mTitle;
    private String mAuthor;
    private String mDescription;
    private long mView;
    private String mChapter;
    private long mLastVisit;

    public long getLastVisit() {
        return mLastVisit;
    }

    public void setLastVisit(long visit) {
        mLastVisit = visit;
    }

    public Book() {

    }

    public Book(String title, String coverUrl, String author, String description, long view, String chapter) {
        mTitle = title;
        mAuthor = author;
        mCoverUrl = coverUrl;
        mDescription = description;
        mView = view;
        mChapter = chapter;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        mCoverUrl = coverUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public long getView() {
        return mView;
    }

    public void setView(long view) {
        mView = view;
    }

    public String getChapter() {
        return mChapter;
    }

    public void setChapter(String chapter) {
        mChapter = chapter;
    }
}

