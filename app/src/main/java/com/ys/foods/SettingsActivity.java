package com.ys.foods;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends BaseActivity {
    private LinearLayout content;
    private static final String[] THEME_NAMES = {"יום", "לילה", "אוטומטי"};
    private static final int[] THEME_VALUES = {Prefs.THEME_DAY, Prefs.THEME_NIGHT, Prefs.THEME_AUTO};
    private static final String[] SIZE_NAMES = {"קטן", "בינוני", "גדול"};

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        newRoot();
        addHeader("הגדרות");
        content = addScrollContent();
        addBottomNav(3);
        setContentView(root);
    }

    @Override void onShown() { rebuild(); }

    private void rebuild() {
        if (content == null) return;
        content.removeAllViews();
        addSection("מראה ונוחות");
        addSetting("מצב תצוגה", themeName(prefs.themeMode()), "◐", new View.OnClickListener() {
            @Override public void onClick(View v) { chooseTheme(); }
        });
        addSetting("צבע מוביל", Ui.PALETTE_NAMES[safe(prefs.colorOption(), Ui.PALETTE_NAMES.length)],
                "●", new View.OnClickListener() { @Override public void onClick(View v) { chooseColor(); }});
        addSetting("גודל הטקסט", SIZE_NAMES[safe(prefs.textSize(), SIZE_NAMES.length)],
                "A", new View.OnClickListener() { @Override public void onClick(View v) { chooseSize(); }});
        addSetting("הצעה אקראית", prefs.showRandom() ? "מוצגת במסך הבית" : "מוסתרת ממסך הבית",
                "✦", new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        prefs.setShowRandom(!prefs.showRandom());
                        if (prefs.showRandom()) HomeActivity.resetSuggestion();
                        rebuild();
                    }
                });
        addSection("נתונים");
        addSetting("איפוס סטטיסטיקות", "ניקוי נתוני השימוש בלבד", "↺",
                new View.OnClickListener() { @Override public void onClick(View v) { confirmReset(); }});
        addSection("מידע");
        addSetting("אודות לוח ברכות", "מקורות וקישורים", "i",
                new View.OnClickListener() { @Override public void onClick(View v) { showAbout(); }});
    }

    private void addSection(String title) {
        TextView label = makeText(title, 13, ui.sub, true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = ui.dp(13);
        lp.bottomMargin = ui.dp(9);
        content.addView(label, lp);
    }

    private void addSetting(String title, String subtitle, String glyph, View.OnClickListener click) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(android.view.Gravity.CENTER_VERTICAL);
        card.setPadding(ui.dp(14), ui.dp(13), ui.dp(14), ui.dp(13));
        card.setBackgroundDrawable(roundedBackground(ui.card, ui.line, 15));
        TextView icon = makeText(glyph, 20, ui.primary, true);
        icon.setGravity(android.view.Gravity.CENTER);
        icon.setBackgroundDrawable(roundedBackground(ui.primarySoft, 0x00000000, 12));
        card.addView(icon, new LinearLayout.LayoutParams(ui.dp(43), ui.dp(43)));
        View gap = new View(this);
        card.addView(gap, new LinearLayout.LayoutParams(ui.dp(12), 1));
        LinearLayout text = new LinearLayout(this);
        text.setOrientation(LinearLayout.VERTICAL);
        text.addView(makeText(title, 15, ui.fg, true));
        TextView sub = makeText(subtitle, 12, ui.sub, false);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slp.topMargin = ui.dp(4);
        text.addView(sub, slp);
        card.addView(text, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView arrow = makeText("‹", 24, ui.sub, false);
        arrow.setGravity(android.view.Gravity.CENTER);
        card.addView(arrow, new LinearLayout.LayoutParams(ui.dp(22), ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.bottomMargin = ui.dp(8);
        content.addView(card, clp);
        card.setOnClickListener(click);
    }

    private String themeName(int mode) {
        if (mode == Prefs.THEME_NIGHT) return "לילה";
        if (mode == Prefs.THEME_AUTO) return "אוטומטי";
        return "יום";
    }

    private static int indexOf(int[] a, int v) {
        for (int i = 0; i < a.length; i++) if (a[i] == v) return i;
        return 0;
    }

    private static int safe(int v, int n) { return v >= 0 && v < n ? v : 0; }

    private void chooseTheme() {
        dlg().setTitle("מצב תצוגה").setSingleChoiceItems(THEME_NAMES, indexOf(THEME_VALUES, prefs.themeMode()),
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        prefs.setThemeMode(THEME_VALUES[w]); d.dismiss(); refreshTheme();
                    }
                }).show();
    }

    private void chooseColor() {
        dlg().setTitle("צבע מוביל").setSingleChoiceItems(Ui.PALETTE_NAMES, safe(prefs.colorOption(), Ui.PALETTE_NAMES.length),
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        prefs.setColorOption(w); d.dismiss(); refreshTheme();
                    }
                }).show();
    }

    private void chooseSize() {
        dlg().setTitle("גודל הטקסט").setSingleChoiceItems(SIZE_NAMES, safe(prefs.textSize(), SIZE_NAMES.length),
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        prefs.setTextSize(w); d.dismiss(); refreshTheme();
                    }
                }).show();
    }

    private void refreshTheme() {
        ui = new Ui(this, prefs);
        applyTheme();
        rebuild();
    }

    private void confirmReset() {
        dlg().setMessage("לאפס את נתוני השימוש? הסימניות וההיסטוריה לא יימחקו.")
                .setPositiveButton("איפוס", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        prefs.clearStats(); toast("הסטטיסטיקות אופסו"); rebuild();
                    }
                }).setNegativeButton("ביטול", null).show();
    }
}
