package com.ys.foods;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/**
 * One food. Opening it records history + usage statistics (like the original).
 * Keys: UP/DOWN/PAGE/SPACE scroll; RIGHT/LEFT previous/next in the list you came from;
 *       '*' bookmark; '#' copy; MENU actions; BACK return.
 */
public class DetailActivity extends BaseActivity {

    private Entry entry;
    private TextView title;
    private ScrollView scroll;
    private TextView body;
    private Button bookmarkBtn;
    private Button copyBtn;
    private boolean recorded;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        entry = Repo.find(this, getIntent().getStringExtra("key"));
        if (entry == null) {
            finish();
            return;
        }
        newRoot();
        title = new TextView(this);
        title.setGravity(Gravity.START);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(ui.dp(12), ui.dp(12), ui.dp(12), ui.dp(12));
        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        scroll = new ScrollView(this);
        scroll.setFocusable(true);
        scroll.setFocusableInTouchMode(true);
        body = new TextView(this);
        body.setGravity(Gravity.START);
        body.setLineSpacing(0f, 1.15f);
        body.setPadding(ui.dp(12), ui.dp(12), ui.dp(12), ui.dp(12));
        scroll.addView(body, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bookmarkBtn = new Button(this);
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { toggleBookmark(); }
        });
        copyBtn = new Button(this);
        copyBtn.setText("העתק ללוח");
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { copy(); }
        });
        bar.addView(bookmarkBtn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        bar.addView(copyBtn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        root.addView(bar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addFooter("ימינה/שמאלה: ערך קודם/הבא · *: סימנייה · #: העתקה · מקש תפריט: פעולות");
        setContentView(root);
        show(entry, b == null);
    }

    @Override
    void onShown() {
        if (entry == null) return;
        styleDetail();
    }

    private void show(Entry e, boolean record) {
        entry = e;
        if (record) prefs.addOpened(e); // history + open_count + unique set, like the original
        styleDetail();
        title.setText(e.name);
        body.setText(e.info);
        scroll.scrollTo(0, 0);
        scroll.requestFocus();
        updateBookmarkButton();
    }

    private void styleDetail() {
        title.setBackgroundColor(ui.header);
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22 * ui.scale);
        body.setTextColor(ui.fg);
        body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 * ui.scale);
        updateBookmarkButton();
    }

    private void updateBookmarkButton() {
        if (bookmarkBtn != null && entry != null) {
            bookmarkBtn.setText(prefs.bookmarks().contains(entry.key()) ? "הסר סימנייה" : "הוסף סימנייה");
        }
    }

    private void toggleBookmark() {
        toast(prefs.toggleBookmark(entry) ? "נוספה לסימניות" : "הוסרה מהסימניות");
        updateBookmarkButton();
    }

    private void copy() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(entry.name, entry.name + "\n" + entry.info));
            toast("הועתק ללוח");
        }
    }

    private void step(int delta) {
        List<Entry> l = Nav.list;
        if (l == null) return;
        int i = Nav.index + delta;
        if (i < 0 || i >= l.size()) return;
        Nav.index = i;
        show(l.get(i), true);
    }

    private void showMenu() {
        final CharSequence[] items = {
                prefs.bookmarks().contains(entry.key()) ? "הסר סימנייה" : "הוסף סימנייה",
                "העתק ללוח"
        };
        dlg()
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        if (w == 0) toggleBookmark(); else copy();
                    }
                })
                .show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {
        int k = ev.getKeyCode();
        if (k == KeyEvent.KEYCODE_MENU || k == KeyEvent.KEYCODE_SOFT_LEFT) {
            if (ev.getAction() == KeyEvent.ACTION_DOWN && ev.getRepeatCount() == 0) showMenu();
            return true;
        }
        if (ev.getAction() == KeyEvent.ACTION_DOWN && !(getCurrentFocus() instanceof Button)) {
            switch (k) {
                case KeyEvent.KEYCODE_DPAD_RIGHT: step(-1); return true; // RTL: right = previous
                case KeyEvent.KEYCODE_DPAD_LEFT:  step(+1); return true;
                case KeyEvent.KEYCODE_STAR:
                    if (ev.getRepeatCount() == 0) toggleBookmark();
                    return true;
                case KeyEvent.KEYCODE_POUND:
                    if (ev.getRepeatCount() == 0) copy();
                    return true;
                case KeyEvent.KEYCODE_PAGE_DOWN:
                case KeyEvent.KEYCODE_SPACE:
                    scroll.pageScroll(View.FOCUS_DOWN);
                    return true;
                case KeyEvent.KEYCODE_PAGE_UP:
                    scroll.pageScroll(View.FOCUS_UP);
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(ev);
    }
}
