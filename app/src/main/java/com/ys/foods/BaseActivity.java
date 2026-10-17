package com.ys.foods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

abstract class BaseActivity extends Activity {
    Prefs prefs;
    Ui ui;
    LinearLayout root;
    TextView header;
    EditText search;
    ListView list;
    TextView footer;
    RowAdapter adapter;
    LinearLayout bottomNav;
    int selectedNav = -1;
    final TextView[] navItems = new TextView[4];

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = new Prefs(this);
        ui = new Ui(this, prefs);
    }

    @Override protected void onResume() {
        super.onResume();
        ui = new Ui(this, prefs);
        applyTheme();
        onShown();
    }

    abstract void onShown();

    LinearLayout newRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.setBackgroundColor(ui.bg);
        return root;
    }

    TextView addHeader(String title) {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        bar.setPadding(ui.dp(16), ui.dp(10), ui.dp(16), ui.dp(8));

        if (!(this instanceof HomeActivity)) {
            TextView back = new TextView(this);
            back.setText("‹");
            back.setGravity(Gravity.CENTER);
            back.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);
            back.setTextColor(ui.fg);
            back.setBackgroundDrawable(roundedBackground(ui.card, ui.line, 16));
            bar.addView(back, new LinearLayout.LayoutParams(ui.dp(42), ui.dp(42)));
            back.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { finish(); }
            });
            View gap = new View(this);
            bar.addView(gap, new LinearLayout.LayoutParams(ui.dp(10), 1));
        }

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        header = new TextView(this);
        header.setText(title);
        header.setTextColor(ui.fg);
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, this instanceof HomeActivity ? 27 : 23);
        header.setTypeface(Fonts.bold(this));
        titleBox.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView subTitle = new TextView(this);
        subTitle.setText(this instanceof HomeActivity ? "חיפוש פשוט • מידע ברור" : "לוח ברכות");
        subTitle.setTextColor(ui.sub);
        subTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = ui.dp(2);
        titleBox.addView(subTitle, subLp);
        bar.addView(titleBox, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        root.addView(bar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return header;
    }

    EditText addSearch(String hint) {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.HORIZONTAL);
        wrap.setGravity(Gravity.CENTER_VERTICAL);
        wrap.setPadding(ui.dp(14), ui.dp(7), ui.dp(14), ui.dp(7));
        search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16 * ui.scale);
        search.setHint(hint);
        search.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        search.setTextColor(ui.fg);
        search.setHintTextColor(ui.sub);
        search.setPadding(ui.dp(14), ui.dp(11), ui.dp(14), ui.dp(11));
        search.setBackgroundDrawable(roundedBackground(ui.field, ui.line, 15));
        wrap.addView(search, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(wrap, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return search;
    }

    ListView addList() {
        list = new ListView(this);
        adapter = new RowAdapter(this);
        list.setAdapter(adapter);
        list.setDivider(null);
        list.setSelector(new android.graphics.drawable.ColorDrawable(0x00000000));
        list.setCacheColorHint(0x00000000);
        list.setPadding(ui.dp(12), ui.dp(5), ui.dp(12), ui.dp(8));
        list.setClipToPadding(false);
        root.addView(list, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        return list;
    }

    LinearLayout addScrollContent() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        content.setPadding(ui.dp(16), ui.dp(8), ui.dp(16), ui.dp(18));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        return content;
    }

    void addBottomNav(int selected) {
        selectedNav = selected;
        View divider = new View(this);
        divider.setBackgroundColor(ui.line);
        root.addView(divider, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ui.dp(1)));
        bottomNav = new LinearLayout(this);
        bottomNav.setOrientation(LinearLayout.HORIZONTAL);
        bottomNav.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        bottomNav.setPadding(ui.dp(4), ui.dp(4), ui.dp(4), ui.dp(3));
        String[] labels = {"בית", "סימניות", "היסטוריה", "הגדרות"};
        String[] glyphs = {"⌂", "★", "↻", "⚙"};
        for (int i = 0; i < labels.length; i++) {
            final int destination = i;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(ui.dp(3), ui.dp(4), ui.dp(3), ui.dp(4));
            TextView icon = new TextView(this);
            icon.setText(glyphs[i]);
            icon.setGravity(Gravity.CENTER);
            icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            icon.setTextColor(i == selected ? ui.primary : ui.sub);
            item.addView(icon, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ui.dp(23)));
            TextView label = new TextView(this);
            label.setText(labels[i]);
            label.setGravity(Gravity.CENTER);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            label.setTextColor(i == selected ? ui.primary : ui.sub);
            item.addView(label, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            item.setBackgroundDrawable(roundedBackground(i == selected ? ui.primarySoft : ui.bg, 0x00000000, 12));
            navItems[i] = icon;
            bottomNav.addView(item, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            item.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { navigateTo(destination); }
            });
        }
        root.addView(bottomNav, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void navigateTo(int destination) {
        if (destination == selectedNav) return;
        Intent i;
        if (destination == 0) i = new Intent(this, HomeActivity.class);
        else if (destination == 1) {
            i = new Intent(this, BoardActivity.class);
            i.putExtra("mode", BoardActivity.MODE_BOOKMARKS);
        } else if (destination == 2) {
            i = new Intent(this, BoardActivity.class);
            i.putExtra("mode", BoardActivity.MODE_HISTORY);
        } else i = new Intent(this, SettingsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        if (!(this instanceof HomeActivity)) finish();
    }

    GradientDrawable roundedBackground(int color, int strokeColor, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(ui.dp(radiusDp));
        if (strokeColor != 0x00000000) d.setStroke(ui.dp(1), strokeColor);
        return d;
    }

    TextView makeText(String value, float sizeSp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp * ui.scale);
        tv.setTextColor(color);
        tv.setTypeface(bold ? Fonts.bold(this) : Fonts.regular(this));
        return tv;
    }

    TextView actionButton(String value) {
        TextView b = makeText(value, 14, 0xFFFFFFFF, true);
        b.setGravity(Gravity.CENTER);
        b.setPadding(ui.dp(14), ui.dp(12), ui.dp(14), ui.dp(12));
        b.setBackgroundDrawable(roundedBackground(ui.primary, 0x00000000, 13));
        b.setClickable(true);
        return b;
    }

    void addFooter(String text) {
        footer = makeText(text, 11, ui.sub, false);
        footer.setPadding(ui.dp(12), ui.dp(6), ui.dp(12), ui.dp(6));
        root.addView(footer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    void applyTheme() {
        if (root != null) root.setBackgroundColor(ui.bg);
        if (header != null) {
            header.setTextColor(ui.fg);
            header.setTypeface(Fonts.bold(this));
        }
        if (search != null) {
            search.setTextColor(ui.fg);
            search.setHintTextColor(ui.sub);
            search.setBackgroundDrawable(roundedBackground(ui.field, ui.line, 15));
        }
        if (footer != null) footer.setTextColor(ui.sub);
        if (list != null) {
            list.setBackgroundColor(ui.bg);
            list.setSelector(new android.graphics.drawable.ColorDrawable(0x00000000));
            if (adapter != null) adapter.notifyDataSetChanged();
        }
        if (bottomNav != null) {
            bottomNav.setBackgroundColor(ui.card);
            for (int i = 0; i < bottomNav.getChildCount(); i++) {
                View child = bottomNav.getChildAt(i);
                if (child instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) child;
                    child.setBackgroundDrawable(roundedBackground(i == selectedNav ? ui.primarySoft : ui.card, 0x00000000, 12));
                    for (int k = 0; k < group.getChildCount(); k++) {
                        View v = group.getChildAt(k);
                        if (v instanceof TextView) ((TextView) v).setTextColor(i == selectedNav ? ui.primary : ui.sub);
                    }
                }
            }
        }
        Fonts.apply(root);
    }

    void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && search != null) imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
    }

    void openEntries(List<Entry> entries, int index) {
        if (index < 0 || index >= entries.size()) return;
        Nav.list = new ArrayList<Entry>(entries);
        Nav.index = index;
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra("key", entries.get(index).key());
        startActivity(i);
    }

    void openLink(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            toast("לא נמצא דפדפן מתאים");
        }
    }

    boolean forwardTypingToSearch(KeyEvent ev) {
        if (search == null || list == null) return false;
        if (ev.getAction() == KeyEvent.ACTION_DOWN && getCurrentFocus() == list
                && ev.isPrintingKey() && !ev.isCtrlPressed() && !ev.isAltPressed()
                && ev.getKeyCode() != KeyEvent.KEYCODE_SPACE) {
            search.requestFocus();
            return search.dispatchKeyEvent(ev);
        }
        return false;
    }

    AlertDialog.Builder dlg() {
        return new Dlg(this, ui.dark ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT);
    }

    private static final class Dlg extends AlertDialog.Builder {
        private final Context c;
        Dlg(Context c, int theme) { super(c, theme); this.c = c; }
        private ArrayAdapter<CharSequence> adapter(CharSequence[] items, int layout) {
            return new ArrayAdapter<CharSequence>(c, layout, android.R.id.text1, items) {
                @Override public View getView(int pos, View convert, ViewGroup parent) {
                    View v = super.getView(pos, convert, parent);
                    Fonts.apply(v);
                    return v;
                }
            };
        }
        @Override public AlertDialog.Builder setItems(CharSequence[] items, DialogInterface.OnClickListener l) {
            return setAdapter(adapter(items, android.R.layout.simple_list_item_1), l);
        }
        @Override public AlertDialog.Builder setSingleChoiceItems(CharSequence[] items, int checked,
                                                                  DialogInterface.OnClickListener l) {
            return setSingleChoiceItems(adapter(items, android.R.layout.simple_list_item_single_choice), checked, l);
        }
        @Override public AlertDialog show() {
            AlertDialog d = super.show();
            View decor = d.getWindow().getDecorView();
            decor.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            Fonts.apply(decor);
            return d;
        }
    }

    void showAbout() {
        dlg().setTitle("אודות לוח ברכות")
                .setMessage("גרסה 1.2 • ממשק חדש • תואם Android 4.4 ומעלה\n\n"
                        + "ההלכות נלקחו מאתר בית ההוראה הלכה למעשה של הגאון הרב אופיר מלכא שליט״א.\n"
                        + "הלוח הוא כלי עזר ראשוני. במקרה של ספק יש לעיין במקורות ובספר הליכות ברכות.\n\n"
                        + "© 2026 כל הזכויות שמורות")
                .setPositiveButton("סגור", null)
                .setNeutralButton("אתר בית ההוראה", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { openLink("https://hl5047.co.il"); }
                })
                .setNegativeButton("דף האפליקציה", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { openLink("https://mitmachim.top/post/1138972"); }
                }).show();
    }
}
