package com.example.myapp1.data;

public class ToDo {
    private String id;
    private String title;
    private String description;
    private boolean checked;
    private String date;
    private String list_id;

    public ToDo() {
    }

    public ToDo(String id, String title, String description, boolean checked, String date, String list_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.checked = checked;
        this.date = date;
        this.list_id = list_id;
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
