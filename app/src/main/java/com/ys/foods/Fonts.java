package com.ys.foods;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Bundled font: Liberation Sans (SIL OFL 1.1, see assets/fonts/LICENSE-LiberationSans.txt).
 * It contains the complete Hebrew block including punctuation (geresh, gershayim, maqaf), so the
 * text looks the same on every device - Android 4.4 ROMs (especially filtered phones) often ship
 * with no or a very thin Hebrew system font.
 */
final class Fonts {
    private static Typeface regular, bold;

    private Fonts() {}

    static synchronized Typeface regular(Context c) {
        if (regular == null) {
            try {
                regular = Typeface.createFromAsset(c.getAssets(), "fonts/LiberationSans-Regular.ttf");
            } catch (Exception e) {
                regular = Typeface.DEFAULT;
            }
        }
        return regular;
    }

    static synchronized Typeface bold(Context c) {
        if (bold == null) {
            try {
                bold = Typeface.createFromAsset(c.getAssets(), "fonts/LiberationSans-Bold.ttf");
            } catch (Exception e) {
                bold = Typeface.DEFAULT_BOLD;
            }
        }
        return bold;
    }

    /** Applies the font to every TextView (incl. EditText, Button) below v, keeping bold where it was bold. */
    static void apply(View v) {
        if (v == null) return;
        Context c = v.getContext();
        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            Typeface cur = tv.getTypeface();
            tv.setTypeface(cur != null && cur.isBold() ? bold(c) : regular(c));
        }
        if (v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) v;
            for (int i = 0; i < g.getChildCount(); i++) apply(g.getChildAt(i));
        }
    }
}
