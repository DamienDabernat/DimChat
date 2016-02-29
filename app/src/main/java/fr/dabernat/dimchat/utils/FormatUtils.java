package fr.dabernat.dimchat.utils;

import android.util.DisplayMetrics;
import java.text.Normalizer;


import fr.dabernat.dimchat.ApplicationManager;

public class FormatUtils {

    private static final String TAG = "FormatUtils";

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = ApplicationManager.getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static String normalizeString(String text) {

        if (text == null) {
            return null;
        }

        text = text.toLowerCase();
        text = text.replace("œ", "oe");
        text = text.replace("æ", "ae");
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public static String toUpperCase(String query) {
        return query.substring(0, 1).toUpperCase() + query.substring(1);
    }
}