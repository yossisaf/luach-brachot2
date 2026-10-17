package com.ys.foods;

import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class RowAdapter extends BaseAdapter {
    private final BaseActivity act;
    final List<Row> rows = new ArrayList<Row>();
    private Set<String> bookmarkKeys;

    RowAdapter(BaseActivity act) { this.act = act; }

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

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        Row r = rows.get(position);
        Ui ui = act.ui;
        if (r.type == Row.HEADER) {
            TextView heading = convertView instanceof TextView ? (TextView) convertView : new TextView(act);
            heading.setText(r.text);
            heading.setTextColor(ui.sub);
            heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            heading.setTypeface(Fonts.bold(act));
            heading.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            heading.setPadding(ui.dp(5), ui.dp(12), ui.dp(5), ui.dp(4));
            heading.setBackgroundColor(ui.bg);
            return heading;
        }

        LinearLayout card = new LinearLayout(act);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        card.setPadding(ui.dp(13), ui.dp(10), ui.dp(13), ui.dp(10));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(r.type == Row.SUGGEST ? ui.primarySoft : ui.card);
        bg.setCornerRadius(ui.dp(15));
        bg.setStroke(ui.dp(1), ui.line);
        card.setBackgroundDrawable(bg);

        TextView icon = new TextView(act);
        icon.setGravity(Gravity.CENTER);
        icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        icon.setTextColor(r.cat == null ? ui.primary : ui.accent(r.cat));
        icon.setTypeface(Fonts.bold(act));
        icon.setText(iconFor(r));
        GradientDrawable disc = new GradientDrawable();
        disc.setColor(ui.primarySoft);
        disc.setCornerRadius(ui.dp(13));
        icon.setBackgroundDrawable(disc);
        card.addView(icon, new LinearLayout.LayoutParams(ui.dp(43), ui.dp(43)));

        View gap = new View(act);
        card.addView(gap, new LinearLayout.LayoutParams(ui.dp(12), 1));

        LinearLayout copy = new LinearLayout(act);
        copy.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(act);
        title.setText(titleFor(r));
        title.setTextColor(ui.fg);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16 * ui.scale);
        title.setTypeface(Fonts.bold(act));
        title.setMaxLines(2);
        copy.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        String sub = subtitleFor(r);
        if (sub.length() > 0) {
            TextView subtitle = new TextView(act);
            subtitle.setText(sub);
            subtitle.setTextColor(ui.sub);
            subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12 * ui.scale);
            subtitle.setMaxLines(r.type == Row.FOOD ? 2 : 1);
            LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            slp.topMargin = ui.dp(3);
            copy.addView(subtitle, slp);
        }
        card.addView(copy, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView arrow = new TextView(act);
        arrow.setText("‹");
        arrow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        arrow.setTextColor(ui.sub);
        arrow.setGravity(Gravity.CENTER);
        card.addView(arrow, new LinearLayout.LayoutParams(ui.dp(22), ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout wrapper = new LinearLayout(act);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setPadding(0, ui.dp(3), 0, ui.dp(3));
        wrapper.addView(card, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return wrapper;
    }

    private String titleFor(Row r) {
        if (r.type == Row.FOOD && r.tag instanceof Entry) {
            Entry e = (Entry) r.tag;
            return (bookmarkKeys != null && bookmarkKeys.contains(e.key()) ? "★ " : "") + e.name;
        }
        return r.text;
    }

    private String subtitleFor(Row r) {
        if (r.type == Row.FOOD && r.tag instanceof Entry) {
            String s = ((Entry) r.tag).info.replace('\n', ' ').trim();
            if (s.length() > 92) s = s.substring(0, 89) + "…";
            return s;
        }
        if (r.type == Row.SUGGEST) return "הצעה אקראית • הקש לצפייה";
        if (!(r.tag instanceof String)) return "";
        String t = (String) r.tag;
        if ("b0".equals(t)) return "ברכות על מאכלים ומשקאות";
        if ("b1".equals(t)) return "ברכות על ריחות ובשמים";
        if ("b2".equals(t)) return "פירות עונתיים ובדיקת חרקים";
        if ("bm".equals(t)) return act.prefs.bookmarks().size() + " פריטים ששמרת";
        if ("hist".equals(t)) return "הפריטים שפתחת לאחרונה";
        if ("stats".equals(t)) return "התקדמות והמאכלים המובילים שלך";
        if ("settings".equals(t)) return "מצב תצוגה, צבע וגודל טקסט";
        if ("about".equals(t)) return "מקורות, מידע וקישורים";
        if (t.startsWith("q:")) return "חיפוש אחרון";
        if ("dismiss".equals(t)) return "הסתר את ההצעה מדף הבית";
        if ("empty".equals(t)) return "הפריטים שלך יופיעו כאן";
        return "";
    }

    private String iconFor(Row r) {
        if (r.type == Row.FOOD) return "•";
        if (r.type == Row.SUGGEST) return "✦";
        if (!(r.tag instanceof String)) return "›";
        String t = (String) r.tag;
        if ("b0".equals(t)) return "א";
        if ("b1".equals(t)) return "ר";
        if ("b2".equals(t)) return "ש";
        if ("bm".equals(t)) return "★";
        if ("hist".equals(t)) return "↻";
        if ("stats".equals(t)) return "↗";
        if ("settings".equals(t)) return "⚙";
        if ("about".equals(t)) return "i";
        if (t.startsWith("q:")) return "⌕";
        return "•";
    }
}
