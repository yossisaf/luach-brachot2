package com.ys.foods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/** Shared UI plumbing: theme, header, search box, list, footer, links. */
abstract class BaseActivity extends Activity {

    Prefs prefs;
    Ui ui;

    LinearLayout root;
    TextView header;
    EditText search;
    ListView list;
    TextView footer;
    RowAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = new Prefs(this);
        ui = new Ui(this, prefs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ui = new Ui(this, prefs);
        applyTheme();
        onShown();
    }

    /** Called after the theme was applied; refresh data here. */
    abstract void onShown();

    // ------------------------------------------------------------------ building blocks

    LinearLayout newRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // API 17
        return root;
    }

    TextView addHeader(String title) {
        header = new TextView(this);
        header.setText(title);
        header.setGravity(Gravity.CENTER);
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        header.setPadding(ui.dp(8), ui.dp(14), ui.dp(8), ui.dp(14));
        root.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return header;
    }

    EditText addSearch(String hint) {
        search = new EditText(this);
        search.setHint(hint);
        search.setSingleLine(true);
        search.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        root.addView(search, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return search;
    }

    ListView addList() {
        list = new ListView(this);
        adapter = new RowAdapter(this);
        list.setAdapter(adapter);
        list.setDivider(null);
        list.setDrawSelectorOnTop(true);
        root.addView(list, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        return list;
    }

    TextView addFooter(String text) {
        footer = new TextView(this);
        footer.setText(text);
        footer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        footer.setPadding(ui.dp(8), ui.dp(4), ui.dp(8), ui.dp(4));
        root.addView(footer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return footer;
    }

    void applyTheme() {
        if (root != null) root.setBackgroundColor(ui.bg);
        if (header != null) {
            header.setBackgroundColor(ui.header);
            header.setTextColor(0xFFFFFFFF);
        }
        if (search != null) {
            search.setTextColor(ui.fg);
            search.setHintTextColor(ui.sub);
            search.setBackgroundColor(ui.field);
            search.setPadding(ui.dp(12), ui.dp(8), ui.dp(12), ui.dp(8));
        }
        if (footer != null) footer.setTextColor(ui.sub);
        Fonts.apply(root);
        if (list != null) {
            list.setSelector(new android.graphics.drawable.ColorDrawable(ui.hl));
            adapter.notifyDataSetChanged();
        }
    }

    // ------------------------------------------------------------------ helpers

    void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && search != null) imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
    }

    /** Opens DetailActivity; remembers the list so left/right can step through it. */
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
            toast("לא נמצא דפדפן");
        }
    }

    /** Typing a letter on a hardware keyboard while the list has focus starts a search. */
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

    // ------------------------------------------------------------------ dialogs

    /** AlertDialog.Builder that matches day/night, forces RTL and applies the bundled font. */
    AlertDialog.Builder dlg() {
        return new Dlg(this, ui.dark ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT);
    }

    private static final class Dlg extends AlertDialog.Builder {
        private final Context c;

        Dlg(Context c, int theme) {
            super(c, theme);
            this.c = c;
        }

        private ArrayAdapter<CharSequence> adapter(CharSequence[] items, int layout) {
            return new ArrayAdapter<CharSequence>(c, layout, android.R.id.text1, items) {
                @Override
                public View getView(int pos, View convert, ViewGroup parent) {
                    View v = super.getView(pos, convert, parent);
                    Fonts.apply(v);
                    return v;
                }
            };
        }

        @Override
        public AlertDialog.Builder setItems(CharSequence[] items, DialogInterface.OnClickListener l) {
            return setAdapter(adapter(items, android.R.layout.simple_list_item_1), l);
        }

        @Override
        public AlertDialog.Builder setSingleChoiceItems(CharSequence[] items, int checked,
                                                        DialogInterface.OnClickListener l) {
            return setSingleChoiceItems(adapter(items, android.R.layout.simple_list_item_single_choice), checked, l);
        }

        @Override
        public AlertDialog show() {
            AlertDialog d = super.show();
            View decor = d.getWindow().getDecorView();
            decor.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            Fonts.apply(decor);
            return d;
        }
    }

    void showAbout() {
        dlg()
                .setTitle("אודות לוח ברכות")
                .setMessage("גירסה 1.1 (תואם אנדרואיד 4.4)\n\n"
                        + "ההלכות נלקחו מאתר בית ההוראה הלכה למעשה של הגאון הרב אופיר מלכא שליט\"א.\n"
                        + "הלוח הינו \"עזרה ראשונה\" לדעת כיצד לנהוג למעשה בכל רגע נתון, את ההסבר והטעם ניתן לראות בספר הליכות ברכות לפי ציוני המקורות המופיעים שם בלוח ברכות.\n"
                        + "בכל מקרה של ספק או חוסר הבנה, חובה לראות את שורשי הדברים בגוף הספר כדי לדעת כיצד לנהוג למעשה.\n\n"
                        + "© 2026 כל הזכויות שמורות")
                .setPositiveButton("סגור", null)
                .setNeutralButton("אתר בית ההוראה", new android.content.DialogInterface.OnClickListener() {
                    @Override public void onClick(android.content.DialogInterface d, int w) {
                        openLink("https://hl5047.co.il");
                    }
                })
                .setNegativeButton("דף האפליקציה בפורום מתמחים טופ", new android.content.DialogInterface.OnClickListener() {
                    @Override public void onClick(android.content.DialogInterface d, int w) {
                        openLink("https://mitmachim.top/post/1138972");
                    }
                })
                .show();
    }
}
