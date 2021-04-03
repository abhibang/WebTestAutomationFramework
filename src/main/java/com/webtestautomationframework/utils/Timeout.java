package com.webtestautomationframework.utils;

public enum Timeout {
    VERY_SHORT(10),
    SHORT(15),
    AVERAGE(20),
    LONG(30),
    VERY_LONG(40);

    private long timeout;

    Timeout(long timeout) {
        this.timeout = timeout;
    }

    public long inMillis() {
        return timeout * 1000;
    }

    public long inSeconds() {
        return timeout;
    }

    public static final int ONE_SECOND = 1;
    public static final int TWO_SECONDS = 2;
    public static final int THREE_SECONDS = 3;
    public static final int FIVE_SECONDS = 5;
    public static final int TEN_SECONDS = 10;
    public static final int FIFTEEN_SECONDS = 15;
    public static final int THIRTY_SECONDS = 30;
    public static final int FORTYFIVE_SECONDS = 45;
    public static final int ONE_MINUTE = 60;
    public static final int ONE_AND_HALF_MINUTES = 90;
    public static final int TWO_MINUTES = 120;
    public static final int TWO_AND_HALF_MINUTES = 150;
    public static final int THREE_MINUTES = 180;
    public static final int FIVE_MINUTES = 300;
    public static final int EIGHT_MINUTES = 480;
    public static final int FIFTEEN_MINUTES = 900;
}
