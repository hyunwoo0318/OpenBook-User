package Project.OpenBook.Constants;

public class KeywordUsageConst {
    public static final int KEYWORD_USAGE_DEFAULT = 3;

    public static int getKeywordProb(int count) {
        if (count > 7) {
            count = 7;
        }
        return count + KEYWORD_USAGE_DEFAULT;
    }
}
