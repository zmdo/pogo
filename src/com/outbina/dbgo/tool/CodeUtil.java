package com.outbina.dbgo.tool;

public class CodeUtil {

    // 下划线命名法转换
    public static final String underScoreCase(String name) {
        StringBuilder usc = new StringBuilder();
        char firstLetter = name.charAt(0);
        if ('A' <= firstLetter && firstLetter <= 'Z'){
            firstLetter = (char) (firstLetter + 32 );
        }
        usc.append(firstLetter);
        for (int i = 1 ; i < name.length() ; i ++) {
            char letter = name.charAt(i);
            if ( isUpperLetter(letter) ) {
                if (i + 1 < name.length() && isLowerLetter(name.charAt(i + 1))) {
                    usc.append('_');
                }

                if ( isNumber(letter)) {
                    usc.append((char) (letter));
                } else {
                    usc.append((char) (letter + 32));
                }

            } else {
                usc.append(letter);
            }

        }
        return usc.toString();
    }

    public static boolean isLowerLetter (char c) {
        return ('a' <= c && c <= 'z');
    }

    public static boolean isUpperLetter (char c) {
        return ('A' <= c && c <= 'Z');
    }

    public static boolean isNumber (char c) {
        return ('0' <= c && c <= '9');
    }

}
