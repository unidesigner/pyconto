package gr.forth.ics.util;

public class Args {
    private static final String GT = " must be greater than ";
    private static final String GTE = " must be greater or equal to ";
    private static final String LT = " must be less than ";
    private static final String LTE = " must be less or equal to ";

    private static final String EQUALS = " must be equal to ";
    
    public static void doesNotContainNull(Iterable<?> iterable) {
        notNull(iterable);
        for (Object o : iterable) {
            notNull("Iterable contains null", o);
        }
    }
    
    public static void isTrue(boolean condition) {
        isTrue("Condition failed", condition);
    }
    
    public static void isTrue(String msg, boolean condition) {
        if (!condition) {
            throw new RuntimeException(msg);
        }
    }
    
    public static void notNull(Object o) {
        notNull(null, o);
    }
    
    public static void notNull(String arg, Object o) {
        if (arg == null) {
            arg = "Argument";
        }
        if (o == null) {
            throw new IllegalArgumentException(arg + " is null");
        }
    }
    
    public static void notNull(Object... args) {
        notNull(null, args);
    }
    
    public static void notNull(String message, Object... args) {
        if (message == null) {
            message = "Some argument";
        }
        for (Object o : args) {
            notNull(message, o);
        }
    }
    
    public static void notEmpty(Iterable<?> iter) {
        notEmpty(null, iter);
    }
    
    public static void notEmpty(String arg, Iterable<?> iter) {
        if (arg == null) {
            arg = "Iterable";
        }
        notNull(iter);
        if (iter.iterator().hasNext()) return;
        throw new IllegalArgumentException(arg + " is empty");
    }
    
    public static void hasNoNull(Iterable<?> iter) {
        hasNoNull(null, iter);
    }
    
    public static void hasNoNull(String arg, Iterable<?> iter) {
        notNull(iter);
        if (arg == null) {
            arg = "Iterable";
        }
        for (Object o : iter) {
            if (o == null) {
                throw new IllegalArgumentException(arg + " contains null");
            }
        }
    }
    
    public static void equals(int value, int expected) {
        if (value == expected) return;
        throw new IllegalArgumentException(value + EQUALS + expected);
    }
    
    public static void equals(long value, long expected) {
        if (value == expected) return;
        throw new IllegalArgumentException(value + EQUALS + expected);
    }
    
    public static void equals(double value, double expected) {
        if (value == expected) return;
        throw new IllegalArgumentException(value + EQUALS + expected);
    }
    
    public static void equals(float value, float expected) {
        if (value == expected) return;
        throw new IllegalArgumentException(value + EQUALS + expected);
    }
    
    public static void equals(char value, char expected) {
        if (value == expected) return;
        throw new IllegalArgumentException(value + EQUALS + expected);
    }
    
    public static void equals(short value, short expected) {
        if (value == expected) return;
        throw new IllegalArgumentException(value + EQUALS + expected);
    }
    
    public static void equals(byte value, byte expected) {
        if (value == expected) return;
        throw new IllegalArgumentException(value + EQUALS + expected);
    }

    public static void equals(Object value, Object expected) {
        if (value == expected || value.equals(expected)) return;
        throw new IllegalArgumentException(value + EQUALS + expected);
    }
    
    public static void gt(int value, int from) {
        if (value > from) return;
        throw new IllegalArgumentException(value + GT + from);
    }
    
    public static void lt(int value, int from) {
        if (value < from) return;
        throw new IllegalArgumentException(value + LT + from);
    }
    
    public static void gte(int value, int from) {
        if (value >= from) return;
        throw new IllegalArgumentException(value + GTE + from);
    }
    
    public static void lte(int value, int from) {
        if (value <= from) return;
        throw new IllegalArgumentException(value + LTE + from);
    }
    
    public static void gt(long value, long from) {
        if (value > from) return;
        throw new IllegalArgumentException(value + GT + from);
    }
    
    public static void lt(long value, long from) {
        if (value < from) return;
        throw new IllegalArgumentException(value + LT + from);
    }
    
    public static void gte(long value, long from) {
        if (value >= from) return;
        throw new IllegalArgumentException(value + GTE + from);
    }
    
    public static void lte(long value, long from) {
        if (value <= from) return;
        throw new IllegalArgumentException(value + LTE + from);
    }
    
    public static void gt(short value, short from) {
        if (value > from) return;
        throw new IllegalArgumentException(value + GT + from);
    }
    
    public static void lt(short value, short from) {
        if (value < from) return;
        throw new IllegalArgumentException(value + LT + from);
    }
    
    public static void gte(short value, short from) {
        if (value >= from) return;
        throw new IllegalArgumentException(value + GTE + from);
    }
    
    public static void lte(short value, short from) {
        if (value <= from) return;
        throw new IllegalArgumentException(value + LTE + from);
    }
    
    public static void gt(byte value, byte from) {
        if (value > from) return;
        throw new IllegalArgumentException(value + GT + from);
    }
    
    public static void lt(byte value, byte from) {
        if (value < from) return;
        throw new IllegalArgumentException(value + LT + from);
    }
    
    public static void gte(byte value, byte from) {
        if (value >= from) return;
        throw new IllegalArgumentException(value + GTE + from);
    }
    
    public static void lte(byte value, byte from) {
        if (value <= from) return;
        throw new IllegalArgumentException(value + LTE + from);
    }
    
    public static void gt(char value, char from) {
        if (value > from) return;
        throw new IllegalArgumentException(value + GT + from);
    }
    
    public static void lt(char value, char from) {
        if (value < from) return;
        throw new IllegalArgumentException(value + LT + from);
    }
    
    public static void gte(char value, char from) {
        if (value >= from) return;
        throw new IllegalArgumentException(value + GTE + from);
    }
    
    public static void lte(char value, char from) {
        if (value <= from) return;
        throw new IllegalArgumentException(value + LTE + from);
    }
    
    public static void gt(double value, double from) {
        if (value > from) return;
        throw new IllegalArgumentException(value + GT + from);
    }
    
    public static void lt(double value, double from) {
        if (value < from) return;
        throw new IllegalArgumentException(value + LT + from);
    }
    
    public static void gte(double value, double from) {
        if (value >= from) return;
        throw new IllegalArgumentException(value + GTE + from);
    }
    
    public static void lte(double value, double from) {
        if (value <= from) return;
        throw new IllegalArgumentException(value + LTE + from);
    }
    
    public static void gt(float value, float from) {
        if (value > from) return;
        throw new IllegalArgumentException(value + GT + from);
    }
    
    public static void lt(float value, float from) {
        if (value < from) return;
        throw new IllegalArgumentException(value + LT + from);
    }
    
    public static void gte(float value, float from) {
        if (value >= from) return;
        throw new IllegalArgumentException(value + GTE + from);
    }
    
    public static void lte(float value, float from) {
        if (value <= from) return;
        throw new IllegalArgumentException(value + LTE + from);
    }
    
    public static <T> void gt(Comparable<T> c1, T c2) {
        if (c1.compareTo(c2) > 0) return;
        throw new IllegalArgumentException(c1 + GT + c2);
    }
    
    public static <T> void lt(Comparable<T> c1, T c2) {
        if (c1.compareTo(c2) < 0) return;
        throw new IllegalArgumentException(c1 + LT + c2);
    }
    
    public static <T> void gte(Comparable<T> c1, T c2) {
        if (c1.compareTo(c2) >= 0) return;
        throw new IllegalArgumentException(c1 + GTE + c2);
    }
    
    public static <T> void lte(Comparable<T> c1, T c2) {
        if (c1.compareTo(c2) <= 0) return;
        throw new IllegalArgumentException(c1 + LTE + c2);
    }
    
    public static <T> void inRangeII(Comparable<T> value, T from, T to) {
        gte(value, from);
        lte(value, to);
    }
    
    public static <T> void inRangeEE(Comparable<T> value, T from, T to) {
        gt(value, from);
        lt(value, to);
    }
    
    public static <T> void inRangeIE(Comparable<T> value, T from, T to) {
        gt(value, from);
        lt(value, to);
    }
    
    public static <T> void inRangeEI(Comparable<T> value, T from, T to) {
        gt(value, from);
        lte(value, to);
    }
    
    public static void inRangeII(int value, int from, int to) {
        gte(value, from);
        lte(value, to);
    }
    
    public static void inRangeEE(int value, int from, int to) {
        gt(value, from);
        lt(value, to);
    }
    
    public static void inRangeIE(int value, int from, int to) {
        gte(value, from);
        lt(value, to);
    }
    
    public static void inRangeEI(int value, int from, int to) {
        gt(value, from);
        lte(value, to);
    }
    
    public static void inRangeII(long value, long from, long to) {
        gte(value, from);
        lte(value, to);
    }
    
    public static void inRangeEE(long value, long from, long to) {
        gt(value, from);
        lt(value, to);
    }
    
    public static void inRangeIE(long value, long from, long to) {
        gte(value, from);
        lt(value, to);
    }
    
    public static void inRangeEI(long value, long from, long to) {
        gt(value, from);
        lte(value, to);
    }
    
    public static void inRangeII(short value, short from, short to) {
        gte(value, from);
        lte(value, to);
    }
    
    public static void inRangeEE(short value, short from, short to) {
        gt(value, from);
        lt(value, to);
    }
    
    public static void inRangeIE(short value, short from, short to) {
        gte(value, from);
        lt(value, to);
    }
    
    public static void inRangeEI(short value, short from, short to) {
        gt(value, from);
        lte(value, to);
    }
    
    public static void inRangeII(byte value, byte from, byte to) {
        gte(value, from);
        lte(value, to);
    }
    
    public static void inRangeEE(byte value, byte from, byte to) {
        gt(value, from);
        lt(value, to);
    }
    
    public static void inRangeIE(byte value, byte from, byte to) {
        gte(value, from);
        lt(value, to);
    }
    
    public static void inRangeEI(byte value, byte from, byte to) {
        gt(value, from);
        lte(value, to);
    }
    
    public static void check(boolean assertion, String messageIfFailed) {
        if (!assertion) {
            throw new RuntimeException(messageIfFailed);
        }
    }
}
