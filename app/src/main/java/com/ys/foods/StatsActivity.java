package com.ys.foods;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * "סטטיסטיקות שימוש" - same computation as the original:
 *  total foods, distinct foods opened, percent explored (int(100 * unique / total)),
 *  and the top 3 foods among those opened at least twice.
 */
public class StatsActivity extends BaseActivity {

    private TextView text;
    private Button reset;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        newRoot();
        addHeader("סטטיסטיקות שימוש");
        ScrollView sv = new ScrollView(this);
        sv.setFocusable(true);
        sv.setFocusableInTouchMode(true);
        text = new TextView(this);
        text.setGravity(Gravity.START);
        text.setLineSpacing(0f, 1.2f);
        text.setPadding(ui.dp(14), ui.dp(14), ui.dp(14), ui.dp(14));
        sv.addView(text, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(sv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        reset = new Button(this);
        reset.setText("איפוס סטטיסטיקות");
        reset.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { confirmReset(); }
        });
        root.addView(reset, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(root);
        sv.requestFocus();
    }

    @Override
    void onShown() {
        text.setTextColor(ui.fg);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18 * ui.scale);
        refresh();
    }

    private void refresh() {
        final List<Entry> all = Repo.all(this);
        int total = all.size();
        int unique = prefs.uniqueOpened().size();
        float ratio = total > 0 ? (float) unique / (float) total : 0f;
        int percent = (int) (100f * ratio);

        final List<Entry> top = new ArrayList<Entry>();
        for (Entry e : all) if (prefs.openCount(e) >= 2) top.add(e);
        Collections.sort(top, new Comparator<Entry>() {
            @Override public int compare(Entry a, Entry b) {
                return prefs.openCount(b) - prefs.openCount(a); // most opened first (stable)
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("סה\"כ מאכלים במאגר: ").append(total).append("\n");
        sb.append("מאכלים שונים שבדקת: ").append(unique).append("\n");
        sb.append(percent).append("% מהמאגר נחקר\n");
        if (!top.isEmpty()) {
            sb.append("\nמאכלים מובילים:\n");
            for (int i = 0; i < top.size() && i < 3; i++) {
                Entry e = top.get(i);
                sb.append(e.name).append(" (").append(prefs.openCount(e)).append(" פתיחות)\n");
            }
        }
        text.setText(sb.toString());
    }

    private void confirmReset() {
        dlg()
                .setMessage("האם אתה בטוח שברצונך לאפס את כל נתוני השימוש? פעולה זו אינה ניתנת לביטול.")
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        prefs.clearStats();
                        toast("הסטטיסטיקות אופסו");
                        refresh();
                    }
                })
                .setNegativeButton("ביטול", null)
                .show();
    }
}
