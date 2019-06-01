package com.dima.emmeggi95.jaycaves.me;

import java.security.SecureRandom;
import java.util.Locale;

/**
 * Custom id generator
 */
public class CustomRandomId {

    /**
     * Generates a @buf digits random alphanumerical string
     * @return random string
     */
    public static String randomIdGenerator(){

            String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lower = upper.toLowerCase(Locale.ROOT);
            String digits = "0123456789";
            String alphanum = upper + lower + digits;
            char[] symbols = alphanum.toCharArray();
            char[] buf = new char[10];

            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[new SecureRandom().nextInt(symbols.length)];
            return new String(buf);

    }

    /**
     *
     */
    public static String randomNumberGenerator(int lenght){
        String digits = "0123456789";
        char[] values = digits.toCharArray();
        char[] buf= new char[lenght];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = values[new SecureRandom().nextInt(values.length)];
        return new String(buf);

    }
}
