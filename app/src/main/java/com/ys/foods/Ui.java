package com.ys.foods;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;

/** Lightweight design system built only from Android views available on API 19+. */
final class Ui {
    static final int[] PALETTE = {
            0xFF176B87, 0xFF4056A1, 0xFF16806A, 0xFFB95C43, 0xFF7356A8, 0xFF536579
    };
    static final String[] PALETTE_NAMES = {"כחול-טורקיז", "כחול", "ירוק", "טרה-קוטה", "סגול", "אפור-כחול"};
    static final float[] SCALES = {0.90f, 1.0f, 1.18f};

    final Context ctx;
    final boolean dark;
    final int primary, primarySoft, bg, card, fg, sub, field, line, header, hl;
    final float scale;

    Ui(Context c, Prefs p) {
        ctx = c;
        int mode = p.themeMode();
        if (mode == Prefs.THEME_NIGHT) dark = true;
        else if (mode == Prefs.THEME_DAY) dark = false;
        else dark = systemNight(c) || powerSave(c);

        int co = p.colorOption();
        primary = PALETTE[(co >= 0 && co < PALETTE.length) ? co : 0];
        primarySoft = blend(primary, dark ? 0xFF101722 : 0xFFF5F9FC, dark ? 0.72f : 0.88f);
        int ts = p.textSize();
        scale = SCALES[(ts >= 0 && ts < SCALES.length) ? ts : 1];

        bg = dark ? 0xFF101722 : 0xFFF4F7FB;
        card = dark ? 0xFF1B2635 : 0xFFFFFFFF;
        fg = dark ? 0xFFF3F6FA : 0xFF1C2938;
        sub = dark ? 0xFFAAB7C7 : 0xFF657487;
        field = dark ? 0xFF202C3B : 0xFFFFFFFF;
        line = dark ? 0xFF334153 : 0xFFE2E8EF;
        header = card;
        hl = withAlpha(primary, dark ? 0x44 : 0x22);
    }

    private static boolean systemNight(Context c) {
        int m = c.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return m == Configuration.UI_MODE_NIGHT_YES;
    }

    private static boolean powerSave(Context c) {
        if (Build.VERSION.SDK_INT >= 21) {
            PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
            return pm != null && pm.isPowerSaveMode();
        }
        return false;
    }

    int accent(String category) {
        if ("hanenin".equals(category)) return primary;
        if ("reach".equals(category)) return dark ? 0xFF72B7A4 : 0xFF16806A;
        if ("tolahim".equals(category)) return dark ? 0xFFE6A18E : 0xFFB95C43;
        return primary;
    }

    String categoryTitle(String category) {
        if ("hanenin".equals(category)) return "ברכות הנהנין";
        if ("reach".equals(category)) return "ברכות הריח";
        if ("tolahim".equals(category)) return "שהחיינו ותולעים";
        return "מאגר הברכות";
    }

    int dp(int v) {
        return (int) (v * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    static int withAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    static int blend(int a, int b, float t) {
        int r = (int) (Color.red(a) * (1 - t) + Color.red(b) * t);
        int g = (int) (Color.green(a) * (1 - t) + Color.green(b) * t);
        int bl = (int) (Color.blue(a) * (1 - t) + Color.blue(b) * t);
        return Color.rgb(r, g, bl);
    }
}
