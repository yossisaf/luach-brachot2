package com.ys.foods;

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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class DetailActivity extends BaseActivity {
    private Entry entry;
    private LinearLayout detailContent;
    private TextView bookmarkButton;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        entry = Repo.find(this, getIntent().getStringExtra("key"));
        if (entry == null) { finish(); return; }

        newRoot();
        addHeader("פרטי המאכל");
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        detailContent = new LinearLayout(this);
        detailContent.setOrientation(LinearLayout.VERTICAL);
        detailContent.setPadding(ui.dp(16), ui.dp(8), ui.dp(16), ui.dp(16));
        scroll.addView(detailContent, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        actions.setPadding(ui.dp(16), ui.dp(8), ui.dp(16), ui.dp(12));
        bookmarkButton = actionButton("☆ סימנייה");
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { toggleBookmark(); }
        });
        TextView copy = actionButton("העתקה");
        copy.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { copy(); }
        });
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        bp.rightMargin = ui.dp(6);
        bp.leftMargin = ui.dp(6);
        actions.addView(bookmarkButton, bp);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        cp.leftMargin = ui.dp(6);
        cp.rightMargin = ui.dp(6);
        actions.addView(copy, cp);
        root.addView(actions, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(root);
        render(entry, b == null);
    }

    @Override void onShown() {
        if (entry != null) {
            applyTheme();
            updateBookmarkLabel();
        }
    }

    private void render(Entry e, boolean record) {
        entry = e;
        if (record) prefs.addOpened(e);
        detailContent.removeAllViews();

        LinearLayout titleCard = new LinearLayout(this);
        titleCard.setOrientation(LinearLayout.VERTICAL);
        titleCard.setPadding(ui.dp(19), ui.dp(19), ui.dp(19), ui.dp(19));
        titleCard.setBackgroundDrawable(roundedBackground(ui.card, ui.line, 18));

        TextView eyebrow = makeText(ui.categoryTitle(e.category), 12, ui.primary, true);
        titleCard.addView(eyebrow);
        TextView name = makeText(e.name, 26, ui.fg, true);
        name.setLineSpacing(0, 1.08f);
        LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameLp.topMargin = ui.dp(6);
        titleCard.addView(name, nameLp);
        detailContent.addView(titleCard, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView detailTitle = makeText("המידע והברכה", 15, ui.sub, true);
        LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelLp.topMargin = ui.dp(20);
        labelLp.bottomMargin = ui.dp(8);
        detailContent.addView(detailTitle, labelLp);

        TextView body = makeText(e.info, 18, ui.fg, false);
        body.setLineSpacing(ui.dp(4), 1.13f);
        body.setPadding(ui.dp(18), ui.dp(18), ui.dp(18), ui.dp(18));
        body.setGravity(Gravity.RIGHT);
        body.setBackgroundDrawable(roundedBackground(ui.card, ui.line, 16));
        detailContent.addView(body, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        updateBookmarkLabel();
        Fonts.apply(detailContent);
    }

    private void updateBookmarkLabel() {
        if (bookmarkButton == null || entry == null) return;
        boolean saved = prefs.bookmarks().contains(entry.key());
        bookmarkButton.setText(saved ? "★ נשמר" : "☆ סימנייה");
        bookmarkButton.setBackgroundDrawable(roundedBackground(saved ? ui.accent(entry.category) : ui.primary, 0x00000000, 13));
    }

    private void toggleBookmark() {
        toast(prefs.toggleBookmark(entry) ? "נוסף לסימניות" : "הוסר מהסימניות");
        updateBookmarkLabel();
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
        render(l.get(i), true);
    }

    private void showMenu() {
        final CharSequence[] items = {
                prefs.bookmarks().contains(entry.key()) ? "הסר סימנייה" : "הוסף סימנייה",
                "העתק ללוח"
        };
        dlg().setTitle(entry.name).setItems(items, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface d, int w) {
                if (w == 0) toggleBookmark(); else copy();
            }
        }).show();
    }

    @Override public boolean dispatchKeyEvent(KeyEvent ev) {
        int k = ev.getKeyCode();
        if (k == KeyEvent.KEYCODE_MENU || k == KeyEvent.KEYCODE_SOFT_LEFT) {
            if (ev.getAction() == KeyEvent.ACTION_DOWN && ev.getRepeatCount() == 0) showMenu();
            return true;
        }
        if (ev.getAction() == KeyEvent.ACTION_DOWN && k == KeyEvent.KEYCODE_DPAD_RIGHT) { step(-1); return true; }
        if (ev.getAction() == KeyEvent.ACTION_DOWN && k == KeyEvent.KEYCODE_DPAD_LEFT) { step(1); return true; }
        if (ev.getAction() == KeyEvent.ACTION_DOWN && k == KeyEvent.KEYCODE_STAR) { toggleBookmark(); return true; }
        if (ev.getAction() == KeyEvent.ACTION_DOWN && k == KeyEvent.KEYCODE_POUND) { copy(); return true; }
        return super.dispatchKeyEvent(ev);
    }
}
