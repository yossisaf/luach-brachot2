package com.ys.foods;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Home: search (all boards), recent searches, optional random-food suggestion, and the menu.
 * Keys: SEARCH focuses the search box; any typed letter starts a search; BACK clears search.
 */
public class HomeActivity extends BaseActivity {

    private static Entry suggestion;
    private static boolean suggestionDismissed;
    private List<Entry> results = new ArrayList<Entry>();

    /** Called from Settings when the random suggestion is switched back on. */
    static void resetSuggestion() {
        suggestionDismissed = false;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        newRoot();
        addHeader("לוח ברכות");
        addSearch("חפש...");
        addList();
        addFooter("1-7: קיצורי דרך · מקש חיפוש / הקלדת אות: חיפוש · מרכז: פתיחה");
        setContentView(root);

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void onTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void afterTextChanged(Editable s) { rebuild(); }
        });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent e) {
                prefs.addSearch(search.getText().toString());
                hideKeyboard();
                list.requestFocus();
                return true;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                onRow(adapter.rows.get(pos));
            }
        });
        list.requestFocus();
    }

    @Override
    void onShown() {
        rebuild();
    }

    private void rebuild() {
        List<Row> rows = new ArrayList<Row>();
        String q = search.getText().toString().trim();
        if (q.length() > 0) {
            results = Repo.filter(Repo.all(this), q);
            rows.add(new Row(Row.HEADER, "תוצאות: " + results.size(), null, null));
            for (Entry e : results) rows.add(new Row(Row.FOOD, e.name, e, e.category));
        } else {
            results = new ArrayList<Entry>();
            if (prefs.showRandom() && !suggestionDismissed) {
                List<Entry> all = Repo.all(this);
                if (suggestion == null && !all.isEmpty()) {
                    suggestion = all.get(new Random().nextInt(all.size()));
                }
                if (suggestion != null) {
                    rows.add(new Row(Row.HEADER, "הצגת מאכל אקראי", null, null));
                    rows.add(new Row(Row.SUGGEST, suggestion.name, suggestion, null));
                    rows.add(new Row(Row.ITEM, "הסר הצעה", "dismiss", null));
                }
            }
            List<String> recent = prefs.searchHistory();
            if (!recent.isEmpty()) {
                rows.add(new Row(Row.HEADER, "חיפושים אחרונים:", null, null));
                for (String s : recent) rows.add(new Row(Row.ITEM, s, "q:" + s, null));
            }
            rows.add(new Row(Row.HEADER, "תפריט", null, null));
            rows.add(new Row(Row.ITEM, Repo.BOARD_TITLES[0], "b0", Repo.CATS[0]));
            rows.add(new Row(Row.ITEM, Repo.BOARD_TITLES[1], "b1", Repo.CATS[1]));
            rows.add(new Row(Row.ITEM, Repo.BOARD_TITLES[2], "b2", Repo.CATS[2]));
            rows.add(new Row(Row.ITEM, "הסימניות שלי", "bm", null));
            rows.add(new Row(Row.ITEM, "היסטוריה", "hist", null));
            rows.add(new Row(Row.ITEM, "סטטיסטיקות שימוש", "stats", null));
            rows.add(new Row(Row.ITEM, "הגדרות", "settings", null));
            rows.add(new Row(Row.ITEM, "אודות", "about", null));
        }
        adapter.setRows(rows);
    }

    private void onRow(Row r) {
        if (r.type == Row.FOOD || r.type == Row.SUGGEST) {
            Entry e = (Entry) r.tag;
            if (r.type == Row.FOOD) {
                prefs.addSearch(search.getText().toString());
                openEntries(results, results.indexOf(e));
            } else {
                List<Entry> one = new ArrayList<Entry>();
                one.add(e);
                openEntries(one, 0);
            }
            return;
        }
        if (!(r.tag instanceof String)) return;
        String t = (String) r.tag;
        if (t.startsWith("q:")) {
            search.setText(t.substring(2));
            search.setSelection(search.getText().length());
        } else if (t.equals("dismiss")) {
            suggestionDismissed = true;
            rebuild();
        } else if (t.startsWith("b") && t.length() == 2) {
            BoardActivity.start(this, t.charAt(1) - '0');
        } else if (t.equals("bm")) {
            BoardActivity.start(this, BoardActivity.MODE_BOOKMARKS);
        } else if (t.equals("hist")) {
            BoardActivity.start(this, BoardActivity.MODE_HISTORY);
        } else if (t.equals("stats")) {
            startActivity(new android.content.Intent(this, StatsActivity.class));
        } else if (t.equals("settings")) {
            startActivity(new android.content.Intent(this, SettingsActivity.class));
        } else if (t.equals("about")) {
            showAbout();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {
        if (ev.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
            if (ev.getAction() == KeyEvent.ACTION_DOWN) {
                search.requestFocus();
                search.setSelection(search.getText().length());
            }
            return true;
        }
        if (ev.getAction() == KeyEvent.ACTION_DOWN && ev.getRepeatCount() == 0
                && getCurrentFocus() == list && search.getText().length() == 0) {
            int k = ev.getKeyCode();
            if (k >= KeyEvent.KEYCODE_1 && k <= KeyEvent.KEYCODE_7) {
                shortcut(k - KeyEvent.KEYCODE_1);
                return true;
            }
        }
        if (forwardTypingToSearch(ev)) return true;
        return super.dispatchKeyEvent(ev);
    }

    /** 1-3 boards, 4 bookmarks, 5 history, 6 statistics, 7 settings (for keypad phones). */
    private void shortcut(int n) {
        if (n <= 2) BoardActivity.start(this, n);
        else if (n == 3) BoardActivity.start(this, BoardActivity.MODE_BOOKMARKS);
        else if (n == 4) BoardActivity.start(this, BoardActivity.MODE_HISTORY);
        else if (n == 5) startActivity(new android.content.Intent(this, StatsActivity.class));
        else startActivity(new android.content.Intent(this, SettingsActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (search.getText().length() > 0) {
            search.setText("");
            list.requestFocus();
        } else {
            super.onBackPressed();
        }
    }
}
