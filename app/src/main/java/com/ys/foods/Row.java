package com.ys.foods;

/** One row of a list: section header, menu item, food, or the random suggestion. */
final class Row {
    static final int HEADER = 0, ITEM = 1, FOOD = 2, SUGGEST = 3;

    final int type;
    final String text;
    final Object tag;
    /** Optional board category used to tint the row. */
    final String cat;

    Row(int type, String text, Object tag, String cat) {
        this.type = type;
        this.text = text;
        this.tag = tag;
        this.cat = cat;
    }
}
