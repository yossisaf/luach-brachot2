package com.ys.foods;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;

/** Resolved theme (day/night/auto, accent color, text scale) - same options as the original app. */
final class Ui {
    /** color_option 0..5, same Material colors the original used. */
    static final int[] PALETTE = {0xFF673AB7, 0xFF2196F3, 0xFF4CAF50, 0xFFF44336, 0xFF3F51B5, 0xFF607D8B};
    static final String[] PALETTE_NAMES = {"סגול", "כחול", "ירוק", "אדום", "אינדיגו", "אפור-כחול"};
    /** text_size_option 0..2 = קטן / בינוני / גדול (multipliers are an approximation). */
    static final float[] SCALES = {0.85f, 1.0f, 1.2f};

    final Context ctx;
    final boolean dark;
    final int primary;
    final float scale;
    final int bg, fg, sub, field, header, hl;

    Ui(Context c, Prefs p) {
        ctx = c;
        int mode = p.themeMode();
        if (mode == Prefs.THEME_NIGHT) dark = true;
        else if (mode == Prefs.THEME_DAY) dark = false;
        else dark = systemNight(c) || powerSave(c);

        int co = p.colorOption();
        primary = PALETTE[(co >= 0 && co < PALETTE.length) ? co : 0];
        int ts = p.textSize();
        scale = SCALES[(ts >= 0 && ts < SCALES.length) ? ts : 1];

        bg = dark ? 0xFF121212 : 0xFFFFFFFF;
        fg = dark ? 0xFFEEEEEE : 0xFF111111;
        sub = dark ? 0xFFAAAAAA : 0xFF555555;
        field = dark ? 0xFF1E1E1E : 0xFFF2F2F2;
        header = dark ? blend(primary, 0xFF000000, 0.45f) : primary;
        hl = withAlpha(primary, dark ? 0xA0 : 0x70); // focus highlight (drawn on top of rows)
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

    /** Tint of a board's rows (the original colors boards differently). */
    int accent(String category) {
        if ("hanenin".equals(category)) return dark ? 0xFF283593 : 0xFFC5CAE9;
        if ("reach".equals(category)) return dark ? 0xFF2E7D32 : 0xFFC8E6C9;
        if ("tolahim".equals(category)) return dark ? 0xFFC62828 : 0xFFFFCDD2;
        return 0x00000000;
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
