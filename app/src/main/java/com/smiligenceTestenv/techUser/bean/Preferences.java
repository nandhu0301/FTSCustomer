package com.smiligenceTestenv.techUser.bean;

import java.util.HashMap;

public abstract class Preferences {
    public  static final HashMap<Integer, Filters> filters = new HashMap<>();
    public static String ORDER_BY = "itemName";
}