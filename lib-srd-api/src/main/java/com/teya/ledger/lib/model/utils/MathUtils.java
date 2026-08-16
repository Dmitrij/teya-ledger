package com.teya.ledger.lib.model.utils;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public final class MathUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private MathUtils() {
        // Utility Class
    }

    public static int randomInt(int minIncluding, int maxIncluding) {
        return ThreadLocalRandom.current().nextInt(minIncluding, maxIncluding + 1);
    }

    public static int randomSecureInt(int minIncluding, int maxIncluding) {
        return SECURE_RANDOM.nextInt(minIncluding, maxIncluding + 1);
    }

}
