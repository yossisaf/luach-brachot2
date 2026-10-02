package com.ys.foods;

/** One food row. Pure Java. Mirrors the original FoodItem(name, info, category). */
public final class Entry {
    public final String name;
    public final String info;
    /** "hanenin", "reach" or "tolahim" - same values the original app used. */
    public final String category;
    private final String key;

    public Entry(String name, String info, String category) {
        this.name = name;
        this.info = info;
        this.category = category;
        this.key = name + "|" + category;
    }

    /** Same key format as the original app: name|category (used for bookmarks, history, stats). */
    public String key() {
        return key;
    }
}
