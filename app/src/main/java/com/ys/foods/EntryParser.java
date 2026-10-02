package com.ys.foods;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Exact port of the original parser (g60.T in the APK). The text itself is never altered
 * beyond what the original app did: every line is trimmed, blank lines are skipped,
 * lines are joined with '\n', and the final text is trimmed.
 *
 *   "name:" line      -> starts a new entry (previous one is flushed)
 *   "info:" line      -> appends trimmed remainder + '\n'   (even if empty, like the original)
 *   any other line    -> appended trimmed + '\n' unless blank
 */
public final class EntryParser {
    private EntryParser() {}

    public static List<Entry> parse(String category, BufferedReader r) throws IOException {
        List<Entry> out = new ArrayList<Entry>();
        String name = null;
        StringBuilder info = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            if (line.startsWith("name:")) {
                if (name != null) out.add(new Entry(name, ktrim(info.toString()), category));
                name = ktrim(line.substring(5));
                info.setLength(0);
            } else if (line.startsWith("info:")) {
                info.append(ktrim(line.substring(5))).append('\n');
            } else if (!isBlank(line)) {
                info.append(ktrim(line)).append('\n');
            }
        }
        if (name != null) out.add(new Entry(name, ktrim(info.toString()), category));
        return out;
    }

    private static boolean isBlank(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i)) && !Character.isSpaceChar(s.charAt(i))) return false;
        }
        return true;
    }

    /** Kotlin String.trim(): strips leading/trailing whitespace characters. */
    private static String ktrim(String s) {
        int a = 0, b = s.length();
        while (a < b && ws(s.charAt(a))) a++;
        while (b > a && ws(s.charAt(b - 1))) b--;
        return s.substring(a, b);
    }

    private static boolean ws(char c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c);
    }
}
