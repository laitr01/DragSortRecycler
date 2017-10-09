package com.androiddev.dragsortinrecycleview;

/**
 * Created by admin on 10/9/2017.
 */

public class BookEntity {
    private String title;
    private String description;

    public BookEntity(String title, String description){
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
