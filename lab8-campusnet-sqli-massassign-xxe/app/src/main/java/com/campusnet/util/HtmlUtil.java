package com.campusnet.util;

public class HtmlUtil {

    /** Equivalent of PHP's htmlspecialchars() - escape before echoing anything user-controlled. */
    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                 .replace("<", "&lt;")
                 .replace(">", "&gt;")
                 .replace("\"", "&quot;")
                 .replace("'", "&#39;");
    }
}
