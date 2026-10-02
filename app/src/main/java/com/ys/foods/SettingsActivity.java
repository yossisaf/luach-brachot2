package com.ys.foods;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/** "הגדרות": theme mode, accent color, text size, random-food switch, reset statistics. */
public class SettingsActivity extends BaseActivity {

    private static final String[] THEME_NAMES = {"יום", "לילה", "אוטומטי"};
    private static final int[] THEME_VALUES = {Prefs.THEME_DAY, Prefs.THEME_NIGHT, Prefs.THEME_AUTO};
    private static final String[] SIZE_NAMES = {"קטן", "בינוני", "גדול"};

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        newRoot();
        addHeader("הגדרות");
        addList();
        setContentView(root);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                Object t = adapter.rows.get(pos).tag;
                if ("theme".equals(t)) chooseTheme();
                else if ("color".equals(t)) chooseColor();
                else if ("size".equals(t)) chooseSize();
                else if ("random".equals(t)) {
                    prefs.setShowRandom(!prefs.showRandom());
                    if (prefs.showRandom()) HomeActivity.resetSuggestion();
                    rebuild();
                } else if ("reset".equals(t)) confirmReset();
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
        rows.add(new Row(Row.HEADER, "הגדרות תצוגה", null, null));
        rows.add(new Row(Row.ITEM, "מצב לילה: " + THEME_NAMES[indexOf(THEME_VALUES, prefs.themeMode())], "theme", null));
        rows.add(new Row(Row.ITEM, "עריכת נושא: " + Ui.PALETTE_NAMES[safe(prefs.colorOption(), Ui.PALETTE_NAMES.length)], "color", null));
        rows.add(new Row(Row.ITEM, "גודל טקסט: " + SIZE_NAMES[safe(prefs.textSize(), SIZE_NAMES.length)], "size", null));
        rows.add(new Row(Row.ITEM, "הצגת מאכל אקראי\nהצגת הצעת מאכל יומית בדף הבית: "
                + (prefs.showRandom() ? "פעיל" : "כבוי"), "random", null));
        rows.add(new Row(Row.HEADER, "נתונים", null, null));
        rows.add(new Row(Row.ITEM, "איפוס סטטיסטיקות", "reset", null));
        adapter.setRows(rows);
    }

    private static int indexOf(int[] a, int v) {
        for (int i = 0; i < a.length; i++) if (a[i] == v) return i;
        return 0;
    }

    private static int safe(int v, int n) {
        return (v >= 0 && v < n) ? v : 0;
    }

    private void chooseTheme() {
        dlg().setTitle("מצב לילה")
                .setSingleChoiceItems(THEME_NAMES, indexOf(THEME_VALUES, prefs.themeMode()),
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface d, int w) {
                                prefs.setThemeMode(THEME_VALUES[w]);
                                d.dismiss();
                                refreshTheme();
                            }
                        }).show();
    }

    private void chooseColor() {
        dlg().setTitle("עריכת נושא")
                .setSingleChoiceItems(Ui.PALETTE_NAMES, safe(prefs.colorOption(), Ui.PALETTE_NAMES.length),
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface d, int w) {
                                prefs.setColorOption(w);
                                d.dismiss();
                                refreshTheme();
                            }
                        }).show();
    }

    private void chooseSize() {
        dlg().setTitle("גודל טקסט")
                .setSingleChoiceItems(SIZE_NAMES, safe(prefs.textSize(), SIZE_NAMES.length),
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface d, int w) {
                                prefs.setTextSize(w);
                                d.dismiss();
                                refreshTheme();
                            }
                        }).show();
    }

    private void refreshTheme() {
        ui = new Ui(this, prefs);
        applyTheme();
        rebuild();
    }

    private void confirmReset() {
        dlg()
                .setMessage("האם אתה בטוח שברצונך לאפס את כל נתוני השימוש? פעולה זו אינה ניתנת לביטול.")
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        prefs.clearStats();
                        toast("הסטטיסטיקות אופסו");
                    }
                })
                .setNegativeButton("ביטול", null)
                .show();
    }
}
