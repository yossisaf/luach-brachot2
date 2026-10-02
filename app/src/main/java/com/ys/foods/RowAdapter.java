package com.ys.foods;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class RowAdapter extends BaseAdapter {
    private final BaseActivity act;
    final List<Row> rows = new ArrayList<Row>();
    private Set<String> bookmarkKeys;

    RowAdapter(BaseActivity act) {
        this.act = act;
    }

    void setRows(List<Row> r) {
        rows.clear();
        rows.addAll(r);
        bookmarkKeys = act.prefs.bookmarks();
        notifyDataSetChanged();
    }

    @Override public int getCount() { return rows.size(); }
    @Override public Object getItem(int i) { return rows.get(i); }
    @Override public long getItemId(int i) { return i; }
    @Override public boolean areAllItemsEnabled() { return false; }
    @Override public boolean isEnabled(int i) { return rows.get(i).type != Row.HEADER; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = (convertView instanceof TextView) ? (TextView) convertView : new TextView(act);
        Row r = rows.get(position);
        Ui ui = act.ui;
        int h = ui.dp(14);
        int v = ui.dp(12);
        boolean bold = false;
        tv.setBackgroundColor(0x00000000);
        tv.setTextColor(ui.fg);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18 * ui.scale);
        tv.setPadding(h, v, h, v);
        String text = r.text;
        switch (r.type) {
            case Row.HEADER:
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14 * ui.scale);
                tv.setTextColor(ui.sub);
                bold = true;
                tv.setPadding(h, ui.dp(10), h, ui.dp(4));
                break;
            case Row.FOOD:
                if (r.tag instanceof Entry && bookmarkKeys != null
                        && bookmarkKeys.contains(((Entry) r.tag).key())) {
                    text = "* " + text;
                }
                break;
            case Row.SUGGEST:
                tv.setBackgroundColor(ui.field);
                bold = true;
                break;
            default: // ITEM
                if (r.cat != null) tv.setBackgroundColor(ui.accent(r.cat));
                break;
        }
        tv.setTypeface(bold ? Fonts.bold(act) : Fonts.regular(act));
        tv.setText(text);
        return tv;
    }
}
