package com.smiligenceTestenv.techUser.bean;

import java.util.List;

public class Filters {
    public static Integer INDEX_PRICE = 1;
    public static Integer INDEX_CATEGORY= 1;


    private String name;
    private List<String> values;
    private List<String> selected;

    public Filters(String name, List<String> values, List<String> selected) {
        this.name = name;
        this.values = values;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getSelected() {
        return selected;
    }

    public void setSelected(List<String> selected) {
        this.selected = selected;
    }
}
