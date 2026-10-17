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

public class HomeActivity extends BaseActivity {
    private static Entry suggestion;
    private static boolean suggestionDismissed;
    private List<Entry> results = new ArrayList<Entry>();

    static void resetSuggestion() { suggestionDismissed = false; }

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        newRoot();
        addHeader("לוח ברכות");
        addSearch("חיפוש לפי שם המאכל או הברכה");
        addList();
        addBottomNav(0);
        setContentView(root);

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void onTextChanged(CharSequence s, int a, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { rebuild(); }
        });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent e) {
                prefs.addSearch(search.getText().toString());
                hideKeyboard();
                list.requestFocus();
                return true;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                if (pos >= 0 && pos < adapter.rows.size()) onRow(adapter.rows.get(pos));
            }
        });
        list.requestFocus();
    }

    @Override void onShown() { rebuild(); }

    private void rebuild() {
        if (adapter == null || search == null) return;
        List<Row> rows = new ArrayList<Row>();
        String q = search.getText().toString().trim();
        if (q.length() > 0) {
            results = Repo.filter(Repo.all(this), q);
            rows.add(new Row(Row.HEADER, "תוצאות חיפוש · " + results.size(), null, null));
            for (Entry e : results) rows.add(new Row(Row.FOOD, e.name, e, e.category));
            if (results.isEmpty()) rows.add(new Row(Row.ITEM, "לא נמצאו תוצאות", "empty", null));
        } else {
            results = new ArrayList<Entry>();
            rows.add(new Row(Row.HEADER, "הלוחות", null, null));
            rows.add(new Row(Row.ITEM, "ברכות הנהנין", "b0", Repo.CATS[0]));
            rows.add(new Row(Row.ITEM, "ברכות הריח", "b1", Repo.CATS[1]));
            rows.add(new Row(Row.ITEM, "שהחיינו ותולעים", "b2", Repo.CATS[2]));

            if (prefs.showRandom() && !suggestionDismissed) {
                List<Entry> all = Repo.all(this);
                if (suggestion == null && !all.isEmpty()) suggestion = all.get(new Random().nextInt(all.size()));
                if (suggestion != null) {
                    rows.add(new Row(Row.HEADER, "לגלות משהו חדש", null, null));
                    rows.add(new Row(Row.SUGGEST, suggestion.name, suggestion, null));
                }
            }
            List<String> recent = prefs.searchHistory();
            if (!recent.isEmpty()) {
                rows.add(new Row(Row.HEADER, "החיפושים האחרונים", null, null));
                for (String s : recent) rows.add(new Row(Row.ITEM, s, "q:" + s, null));
            }
            rows.add(new Row(Row.HEADER, "הספרייה האישית", null, null));
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
        } else if ("dismiss".equals(t)) {
            suggestionDismissed = true;
            rebuild();
        } else if (t.startsWith("b") && t.length() == 2) {
            BoardActivity.start(this, t.charAt(1) - '0');
        } else if ("bm".equals(t)) {
            BoardActivity.start(this, BoardActivity.MODE_BOOKMARKS);
        } else if ("hist".equals(t)) {
            BoardActivity.start(this, BoardActivity.MODE_HISTORY);
        } else if ("stats".equals(t)) {
            startActivity(new android.content.Intent(this, StatsActivity.class));
        } else if ("settings".equals(t)) {
            startActivity(new android.content.Intent(this, SettingsActivity.class));
        } else if ("about".equals(t)) showAbout();
    }

    @Override public boolean dispatchKeyEvent(KeyEvent ev) {
        if (ev.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
            if (ev.getAction() == KeyEvent.ACTION_DOWN) {
                search.requestFocus();
                search.setSelection(search.getText().length());
            }
            return true;
        }
        if (forwardTypingToSearch(ev)) return true;
        return super.dispatchKeyEvent(ev);
    }

    @Override public void onBackPressed() {
        if (search.getText().length() > 0) {
            search.setText("");
            list.requestFocus();
        } else super.onBackPressed();
    }
}
