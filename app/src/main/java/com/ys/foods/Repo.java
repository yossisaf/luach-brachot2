package com.ys.foods;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Repo {
    public static final String[] CATS = {"hanenin", "reach", "tolahim"};
    public static final String[] FILES = {"blessings.txt", "smell.txt", "shehechiyanu.txt"};
    public static final String[] BOARD_TITLES = {
            "לוח ברכות הנהנין", "לוח ברכות הריח", "לוח שהחיינו ותולעים"
    };
    private static final List<List<Entry>> CACHE = new ArrayList<List<Entry>>();
    private static Map<String, Entry> index;

    private Repo() {}

    public static synchronized List<Entry> board(Context c, int i) {
        while (CACHE.size() < CATS.length) CACHE.add(null);
        List<Entry> l = CACHE.get(i);
        if (l == null) {
            l = new ArrayList<Entry>();
            try {
                InputStream in = c.getAssets().open(FILES[i]);
                BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                try {
                    l = EntryParser.parse(CATS[i], r);
                } finally {
                    r.close();
                }
            } catch (Exception ignored) {
            }
            CACHE.set(i, l);
        }
        return l;
    }

    public static List<Entry> all(Context c) {
        List<Entry> out = new ArrayList<Entry>();
        for (int i = 0; i < CATS.length; i++) out.addAll(board(c, i));
        return out;
    }

    public static Entry find(Context c, String key) {
        if (key == null) return null;
        synchronized (Repo.class) {
            if (index == null) {
                // first entry wins for duplicate names, like a linear search would
                Map<String, Entry> m = new HashMap<String, Entry>();
                for (int i = 0; i < CATS.length; i++) {
                    for (Entry e : board(c, i)) if (!m.containsKey(e.key())) m.put(e.key(), e);
                }
                index = m;
            }
            return index.get(key);
        }
    }

    public static List<Entry> bookmarks(Context c, Prefs p) {
        Set<String> keys = p.bookmarks();
        List<Entry> out = new ArrayList<Entry>();
        for (Entry e : all(c)) if (keys.contains(e.key())) out.add(e);
        return out;
    }

    public static List<Entry> history(Context c, Prefs p) {
        List<Entry> out = new ArrayList<Entry>();
        for (String k : p.historyKeys()) {
            Entry e = find(c, k);
            if (e != null) out.add(e);
        }
        return out;
    }

    /** Name matches first, then entries whose text matches. */
    public static List<Entry> filter(List<Entry> base, String q) {
        q = q == null ? "" : q.trim();
        if (q.length() == 0) return new ArrayList<Entry>(base);
        List<Entry> out = new ArrayList<Entry>();
        for (Entry e : base) if (e.name.contains(q)) out.add(e);
        for (Entry e : base) if (!e.name.contains(q) && e.info.contains(q)) out.add(e);
        return out;
    }
}
