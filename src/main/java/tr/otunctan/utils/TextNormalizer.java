package tr.otunctan.utils;

public final class TextNormalizer {


    public static String replace(String path, String term, String replacement) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        return path.replace(term, replacement);
    }
}
