package com.yuanzhixiang.bt.engine;

import com.yuanzhixiang.bt.service.ContextService;

/**
 * @author Yuan Zhixiang
 */
public class ContextLocal {

    private static final ThreadLocal<ContextService> CONTEXT = new ThreadLocal<>();

    public static void set(ContextService contextService) {
        CONTEXT.set(contextService);
    }

    public static ContextService get() {
        return CONTEXT.get();
    }

    public static void remove() {
        CONTEXT.remove();
    }
}
