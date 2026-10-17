package com.ys.foods;

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

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        mode = getIntent().getIntExtra("mode", 0);
        if (mode < 0 || mode > MODE_HISTORY) mode = 0;
        newRoot();
        addHeader(mode == MODE_BOOKMARKS ? "הסימניות שלי"
                : mode == MODE_HISTORY ? "היסטוריה" : Repo.BOARD_TITLES[mode]);
        addSearch(mode == MODE_BOOKMARKS ? "חיפוש בסימניות"
                : mode == MODE_HISTORY ? "חיפוש בהיסטוריה" : "חיפוש בלוח");
        addList();
        addBottomNav(mode == MODE_BOOKMARKS ? 1 : mode == MODE_HISTORY ? 2 : -1);
        setContentView(root);

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void onTextChanged(CharSequence s, int a, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { rebuild(); }
        });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent e) {
                hideKeyboard();
                list.requestFocus();
                return true;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                Row r = adapter.rows.get(pos);
                if (r.type == Row.FOOD) openEntries(shown, shown.indexOf((Entry) r.tag));
                else if (r.tag instanceof String && ((String) r.tag).startsWith("q:")) {
                    search.setText(((String) r.tag).substring(2));
                }
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> p, View v, int pos, long id) {
                showActions(pos);
                return true;
            }
        });
        list.requestFocus();
    }

    @Override void onShown() {
        rebuild();
        if (!noticeChecked) {
            noticeChecked = true;
            maybeShowNotice();
        }
    }

    private void rebuild() {
        if (adapter == null || search == null) return;
        List<Entry> base;
        if (mode == MODE_BOOKMARKS) base = Repo.bookmarks(this, prefs);
        else if (mode == MODE_HISTORY) base = Repo.history(this, prefs);
        else base = Repo.board(this, mode);

        String q = search.getText().toString().trim();
        shown = Repo.filter(base, q);
        List<Row> rows = new ArrayList<Row>();
        if (q.length() > 0) rows.add(new Row(Row.HEADER, "תוצאות · " + shown.size(), null, null));
        else if (mode == MODE_BOOKMARKS) rows.add(new Row(Row.HEADER, "פריטים ששמרת", null, null));
        else if (mode == MODE_HISTORY) rows.add(new Row(Row.HEADER, "נפתחו לאחרונה", null, null));
        else rows.add(new Row(Row.HEADER, "המאגר", null, null));
        for (Entry e : shown) rows.add(new Row(Row.FOOD, e.name, e, e.category));
        if (shown.isEmpty()) {
            String message = q.length() > 0 ? "לא נמצאו תוצאות" :
                    mode == MODE_BOOKMARKS ? "אין סימניות עדיין" :
                    mode == MODE_HISTORY ? "ההיסטוריה ריקה" : "לא נטענו פריטים מהמאגר";
            rows.add(new Row(Row.ITEM, message, "empty", null));
        }
        adapter.setRows(rows);
    }

    private void maybeShowNotice() {
        if (mode > 2 || mode == 1) return;
        final String cat = Repo.CATS[mode];
        if (prefs.noticeHidden(cat)) return;
        String text = readAsset(mode == 0 ? "notice_hanenin.txt" : "notice_tolahim.txt");
        if (text.length() == 0) return;
        dlg().setMessage(text)
                .setPositiveButton("הבנתי", null)
                .setNegativeButton("אל תציג שוב", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { prefs.hideNotice(cat); }
                }).show();
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
        } catch (Exception e) { return ""; }
    }

    private void showActions(int pos) {
        if (pos < 0 || pos >= adapter.rows.size()) return;
        Row r = adapter.rows.get(pos);
        final Entry e = r.type == Row.FOOD ? (Entry) r.tag : null;
        List<String> labels = new ArrayList<String>();
        final List<Integer> ids = new ArrayList<Integer>();
        if (e != null) {
            labels.add(prefs.bookmarks().contains(e.key()) ? "הסר מהסימניות" : "הוסף לסימניות");
            ids.add(0);
            if (mode == MODE_HISTORY) { labels.add("מחק מההיסטוריה"); ids.add(1); }
        }
        if (mode == MODE_HISTORY) { labels.add("נקה היסטוריה"); ids.add(2); }
        if (labels.isEmpty()) return;
        dlg().setTitle(e == null ? "פעולות" : e.name)
                .setItems(labels.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int which) {
                        int id = ids.get(which);
                        if (id == 0) {
                            toast(prefs.toggleBookmark(e) ? "נוסף לסימניות" : "הוסר מהסימניות");
                            rebuild();
                        } else if (id == 1) {
                            prefs.deleteFromHistory(e);
                            rebuild();
                        } else confirmClearHistory();
                    }
                }).show();
    }

    private void confirmClearHistory() {
        dlg().setMessage("למחוק את כל ההיסטוריה?")
                .setPositiveButton("מחיקה", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { prefs.clearHistory(); rebuild(); }
                })
                .setNegativeButton("ביטול", null).show();
    }

    @Override public boolean dispatchKeyEvent(KeyEvent ev) {
        int k = ev.getKeyCode();
        if (k == KeyEvent.KEYCODE_MENU || k == KeyEvent.KEYCODE_SOFT_LEFT) {
            if (ev.getAction() == KeyEvent.ACTION_DOWN && ev.getRepeatCount() == 0)
                showActions(list.getSelectedItemPosition());
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

    @Override public void onBackPressed() {
        if (search.getText().length() > 0) {
            search.setText("");
            list.requestFocus();
        } else super.onBackPressed();
    }
}
