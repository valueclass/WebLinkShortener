package web.links.utils;

import java.util.Random;

public class Utils {
    private static final char[] CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890_-".toCharArray();
    private static final Random RANDOM = new Random();

    public static String generateRandomId(int length) {
        final char[] chars = new char[length];

        chars[0] = CHARS[RANDOM.nextInt(CHARS.length - 2)];

        for (int i = 1; i < length; i++) {
            char c = CHARS[RANDOM.nextInt(CHARS.length)];
            chars[i] = c;
        }

        return new String(chars);
    }

}
