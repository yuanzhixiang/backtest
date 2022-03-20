package com.yuanzhixiang.bt.engine;

/**
 * @author yuanzhixiang
 */
public class ContextLocal {

    private static final ThreadLocal<Context> CONTEXT = new ThreadLocal<>();

    public static void set(Context context) {
        CONTEXT.set(context);
    }

    public static Context get() {
        return CONTEXT.get();
    }

    public static void remove() {
        CONTEXT.remove();
    }
}
