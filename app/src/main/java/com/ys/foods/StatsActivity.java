package com.ys.foods;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatsActivity extends BaseActivity {
    private LinearLayout content;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        newRoot();
        addHeader("סטטיסטיקות");
        content = addScrollContent();
        setContentView(root);
    }

    @Override void onShown() { refresh(); }

    private void refresh() {
        if (content == null) return;
        content.removeAllViews();
        List<Entry> all = Repo.all(this);
        int total = all.size();
        int unique = prefs.uniqueOpened().size();
        int percent = total > 0 ? (int) (100f * unique / total) : 0;

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(ui.dp(20), ui.dp(20), ui.dp(20), ui.dp(20));
        hero.setBackgroundDrawable(roundedBackground(ui.primary, 0x00000000, 18));
        hero.addView(makeText("ההתקדמות שלך", 14, 0xFFFFFFFF, false));
        TextView pct = makeText(percent + "%", 38, 0xFFFFFFFF, true);
        LinearLayout.LayoutParams pctLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pctLp.topMargin = ui.dp(5);
        hero.addView(pct, pctLp);
        hero.addView(makeText(unique + " מתוך " + total + " פריטים נפתחו", 13, 0xFFE5F4FA, false));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hp.bottomMargin = ui.dp(12);
        content.addView(hero, hp);

        LinearLayout metrics = new LinearLayout(this);
        metrics.setOrientation(LinearLayout.HORIZONTAL);
        metrics.setLayoutDirection(android.view.View.LAYOUT_DIRECTION_RTL);
        metrics.addView(metricCard(String.valueOf(total), "פריטים במאגר"), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        View spacer = new android.view.View(this);
        metrics.addView(spacer, new LinearLayout.LayoutParams(ui.dp(8), 1));
        metrics.addView(metricCard(String.valueOf(unique), "פריטים שנבדקו"), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mp.bottomMargin = ui.dp(18);
        content.addView(metrics, mp);

        List<Entry> top = new ArrayList<Entry>();
        for (Entry e : all) if (prefs.openCount(e) >= 2) top.add(e);
        Collections.sort(top, new Comparator<Entry>() {
            @Override public int compare(Entry a, Entry b) { return prefs.openCount(b) - prefs.openCount(a); }
        });
        content.addView(makeText("המאכלים המובילים שלך", 16, ui.fg, true));
        if (top.isEmpty()) {
            TextView empty = makeText("כשתפתח מאכלים כמה פעמים, הם יופיעו כאן.", 13, ui.sub, false);
            LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ep.topMargin = ui.dp(8);
            content.addView(empty, ep);
        } else {
            for (int i = 0; i < top.size() && i < 3; i++) {
                Entry e = top.get(i);
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                row.setPadding(ui.dp(14), ui.dp(13), ui.dp(14), ui.dp(13));
                row.setBackgroundDrawable(roundedBackground(ui.card, ui.line, 14));
                TextView rank = makeText(String.valueOf(i + 1), 15, ui.primary, true);
                rank.setGravity(android.view.Gravity.CENTER);
                rank.setBackgroundDrawable(roundedBackground(ui.primarySoft, 0x00000000, 11));
                row.addView(rank, new LinearLayout.LayoutParams(ui.dp(36), ui.dp(36)));
                android.view.View gap = new android.view.View(this);
                row.addView(gap, new LinearLayout.LayoutParams(ui.dp(10), 1));
                row.addView(makeText(e.name, 14, ui.fg, true), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                row.addView(makeText(prefs.openCount(e) + " פתיחות", 12, ui.sub, false));
                LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rp.topMargin = ui.dp(7);
                content.addView(row, rp);
            }
        }
        TextView reset = actionButton("איפוס סטטיסטיקות");
        reset.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) { confirmReset(); }
        });
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlp.topMargin = ui.dp(22);
        content.addView(reset, rlp);
    }

    private LinearLayout metricCard(String value, String label) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(ui.dp(14), ui.dp(15), ui.dp(14), ui.dp(15));
        card.setBackgroundDrawable(roundedBackground(ui.card, ui.line, 15));
        card.addView(makeText(value, 25, ui.fg, true));
        TextView l = makeText(label, 11, ui.sub, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = ui.dp(3);
        card.addView(l, lp);
        return card;
    }

    private void confirmReset() {
        dlg().setMessage("לאפס את נתוני השימוש? הסימניות וההיסטוריה לא יימחקו.")
                .setPositiveButton("איפוס", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { prefs.clearStats(); refresh(); }
                }).setNegativeButton("ביטול", null).show();
    }
}
