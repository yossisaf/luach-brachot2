package com.ys.foods;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * One list screen: a board (0..2), "הסימניות שלי" (3) or "היסטוריה" (4), each with its own search box.
 * MENU key / long press: actions for the selected row (bookmark, delete from history, clear history).
 */
public class BoardActivity extends BaseActivity {
    static final int MODE_BOOKMARKS = 3, MODE_HISTORY = 4;

    private int mode;
    private List<Entry> shown = new ArrayList<Entry>();
    private boolean noticeChecked;

    static void start(Context c, int mode) {
        Intent i = new Intent(c, BoardActivity.class);
        i.putExtra("mode", mode);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        mode = getIntent().getIntExtra("mode", 0);
        newRoot();
        addHeader(mode == MODE_BOOKMARKS ? "הסימניות שלי"
                : mode == MODE_HISTORY ? "היסטוריה" : Repo.BOARD_TITLES[mode]);
        addSearch(mode == MODE_BOOKMARKS ? "חיפוש בסימניות..."
                : mode == MODE_HISTORY ? "חיפוש בהיסטוריה..." : "חפש...");
        addList();
        addFooter("מקש תפריט / לחיצה ארוכה: פעולות · מקש חיפוש: חיפוש · מרכז/Enter: פתיחה");
        setContentView(root);

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void onTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void afterTextChanged(Editable s) { rebuild(); }
        });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent e) {
                hideKeyboard();
                list.requestFocus();
                return true;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                Row r = adapter.rows.get(pos);
                if (r.type == Row.FOOD) openEntries(shown, shown.indexOf((Entry) r.tag));
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p, View v, int pos, long id) {
                showActions(pos);
                return true;
            }
        });
        list.requestFocus();
    }

    @Override
    void onShown() {
        rebuild();
        if (!noticeChecked) {
            noticeChecked = true;
            maybeShowNotice();
        }
    }

    private void rebuild() {
        List<Entry> base;
        if (mode == MODE_BOOKMARKS) base = Repo.bookmarks(this, prefs);
        else if (mode == MODE_HISTORY) base = Repo.history(this, prefs);
        else base = Repo.board(this, mode);

        String q = search.getText().toString().trim();
        shown = Repo.filter(base, q);
        List<Row> rows = new ArrayList<Row>();
        if (q.length() > 0) rows.add(new Row(Row.HEADER, "תוצאות: " + shown.size(), null, null));
        for (Entry e : shown) rows.add(new Row(Row.FOOD, e.name, e, mode <= 2 ? e.category : null));
        if (shown.isEmpty() && q.length() == 0) {
            rows.add(new Row(Row.HEADER, mode == MODE_BOOKMARKS ? "אין סימניות" : mode == MODE_HISTORY ? "ההיסטוריה ריקה" : "", null, null));
        }
        adapter.setRows(rows);
    }

    // ---------------------------------------------------------------- notice before boards

    /** Verbatim notice texts from the original app, shown once unless "אל תציג שוב". */
    private void maybeShowNotice() {
        if (mode > 2 || mode == 1) return; // original: only hanenin + tolahim have notices
        final String cat = Repo.CATS[mode];
        if (prefs.noticeHidden(cat)) return;
        String text = readAsset(mode == 0 ? "notice_hanenin.txt" : "notice_tolahim.txt");
        dlg()
                .setMessage(text)
                .setPositiveButton("אישור", null)
                .setNegativeButton("אל תציג שוב", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { prefs.hideNotice(cat); }
                })
                .show();
    }

    private String readAsset(String name) {
        try {
            InputStream in = getAssets().open(name);
            BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[2048];
            int n;
            while ((n = r.read(buf)) > 0) sb.append(buf, 0, n);
            r.close();
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // ---------------------------------------------------------------- actions

    private void showActions(int pos) {
        if (pos < 0 || pos >= adapter.rows.size()) return;
        Row r = adapter.rows.get(pos);
        final Entry e = (r.type == Row.FOOD) ? (Entry) r.tag : null;
        List<String> labels = new ArrayList<String>();
        final List<Integer> ids = new ArrayList<Integer>();
        if (e != null) {
            labels.add(prefs.bookmarks().contains(e.key()) ? "הסר מהסימניות" : "הוסף לסימניות");
            ids.add(0);
            if (mode == MODE_HISTORY) { labels.add("מחק מהיסטוריה"); ids.add(1); }
        }
        if (mode == MODE_HISTORY) { labels.add("נקה הכל"); ids.add(2); }
        if (labels.isEmpty()) return;
        dlg()
                .setItems(labels.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        int id = ids.get(which);
                        if (id == 0) {
                            toast(prefs.toggleBookmark(e) ? "נוספה לסימניות" : "הוסרה מהסימניות");
                            rebuild();
                        } else if (id == 1) {
                            prefs.deleteFromHistory(e);
                            rebuild();
                        } else {
                            confirmClearHistory();
                        }
                    }
                })
                .show();
    }

    private void confirmClearHistory() {
        dlg()
                .setMessage("למחוק את כל ההיסטוריה?")
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { prefs.clearHistory(); rebuild(); }
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {
        int k = ev.getKeyCode();
        if (k == KeyEvent.KEYCODE_MENU || k == KeyEvent.KEYCODE_SOFT_LEFT) {
            if (ev.getAction() == KeyEvent.ACTION_DOWN && ev.getRepeatCount() == 0) {
                showActions(list.getSelectedItemPosition());
            }
            return true;
        }
        if (k == KeyEvent.KEYCODE_SEARCH) {
            if (ev.getAction() == KeyEvent.ACTION_DOWN) {
                search.requestFocus();
                search.setSelection(search.getText().length());
            }
            return true;
        }
        if (forwardTypingToSearch(ev)) return true;
        return super.dispatchKeyEvent(ev);
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
