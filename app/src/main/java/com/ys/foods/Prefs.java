package com.ys.foods;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Same SharedPreferences file name and keys as the original app ("app_prefs"):
 *   bookmarks (Set)            opened_history_v2 (String, '\n' joined, max 50, newest first)
 *   unique_opened_items (Set)  open_count|<key> (int)
 *   search_history (String, '|' joined, max 5, newest first)
 *   show_random (bool, default true)  theme_mode (int, default 1)
 *   color_option (int, default 0)     text_size_option (int, default 1)
 *   notice_hanenin / notice_tolahim (bool, "don't show again")
 */
public final class Prefs {
    public static final int THEME_AUTO = 0, THEME_DAY = 1, THEME_NIGHT = 2;

    private final SharedPreferences sp;

    public Prefs(Context c) {
        sp = c.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }

    // ---- display settings
    public int themeMode() { return sp.getInt("theme_mode", THEME_DAY); }
    public void setThemeMode(int v) { sp.edit().putInt("theme_mode", v).apply(); }
    public int colorOption() { return sp.getInt("color_option", 0); }
    public void setColorOption(int v) { sp.edit().putInt("color_option", v).apply(); }
    public int textSize() { return sp.getInt("text_size_option", 1); }
    public void setTextSize(int v) { sp.edit().putInt("text_size_option", v).apply(); }
    public boolean showRandom() { return sp.getBoolean("show_random", true); }
    public void setShowRandom(boolean v) { sp.edit().putBoolean("show_random", v).apply(); }
    public boolean noticeHidden(String category) { return sp.getBoolean("notice_" + category, false); }
    public void hideNotice(String category) { sp.edit().putBoolean("notice_" + category, true).apply(); }

    // ---- bookmarks
    public Set<String> bookmarks() {
        return new HashSet<String>(sp.getStringSet("bookmarks", new HashSet<String>()));
    }

    /** @return true if now bookmarked */
    public boolean toggleBookmark(Entry e) {
        Set<String> s = bookmarks();
        boolean now;
        if (s.contains(e.key())) { s.remove(e.key()); now = false; } else { s.add(e.key()); now = true; }
        sp.edit().putStringSet("bookmarks", s).apply();
        return now;
    }

    // ---- opened history + usage statistics
    public List<String> historyKeys() {
        List<String> l = new ArrayList<String>();
        String h = sp.getString("opened_history_v2", "");
        if (h != null && h.trim().length() > 0) {
            for (String x : h.split("\n")) if (x.trim().length() > 0) l.add(x);
        }
        return l;
    }

    private void saveHistory(List<String> l) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < l.size(); i++) {
            if (i > 0) b.append('\n');
            b.append(l.get(i));
        }
        sp.edit().putString("opened_history_v2", b.toString()).apply();
    }

    /** Port of MainAppScreen.addToOpenedHistory: history (50) + open_count + unique set. */
    public void addOpened(Entry e) {
        String key = e.key();
        List<String> l = historyKeys();
        l.remove(key);
        l.add(0, key);
        while (l.size() > 50) l.remove(l.size() - 1);
        saveHistory(l);
        Set<String> unique = uniqueOpened();
        unique.add(key);
        sp.edit()
                .putInt("open_count|" + key, openCount(e) + 1)
                .putStringSet("unique_opened_items", unique)
                .apply();
    }

    public void deleteFromHistory(Entry e) {
        List<String> l = historyKeys();
        l.remove(e.key());
        saveHistory(l);
    }

    public void clearHistory() {
        sp.edit().remove("opened_history_v2").apply();
    }

    public int openCount(Entry e) { return sp.getInt("open_count|" + e.key(), 0); }

    public Set<String> uniqueOpened() {
        return new HashSet<String>(sp.getStringSet("unique_opened_items", new HashSet<String>()));
    }

    /** Port of clearAllStats: removes every open_count|* key and the unique set. */
    public void clearStats() {
        SharedPreferences.Editor ed = sp.edit();
        for (String k : new ArrayList<String>(sp.getAll().keySet())) {
            if (k.startsWith("open_count|")) ed.remove(k);
        }
        ed.remove("unique_opened_items");
        ed.apply();
    }

    // ---- recent searches (last 5)
    public List<String> searchHistory() {
        List<String> l = new ArrayList<String>();
        String h = sp.getString("search_history", "");
        if (h != null && h.trim().length() > 0) {
            for (String x : h.split("\\|")) if (x.trim().length() > 0) l.add(x);
        }
        return l;
    }

    public void addSearch(String q) {
        q = q == null ? "" : q.trim();
        if (q.length() == 0) return;
        List<String> l = searchHistory();
        l.remove(q);
        l.add(0, q);
        while (l.size() > 5) l.remove(l.size() - 1);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < l.size(); i++) {
            if (i > 0) b.append('|');
            b.append(l.get(i));
        }
        sp.edit().putString("search_history", b.toString()).apply();
    }
}
