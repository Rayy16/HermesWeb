package com.hermes.common.helper;

public class BaseContextHelper {
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setCurrentUid(String uid) {
        threadLocal.set(uid);
    }

    public static String getCurrentUid() {
        return threadLocal.get();
    }

    public static void removeCurrentUid() {
        threadLocal.remove();
    }
}
