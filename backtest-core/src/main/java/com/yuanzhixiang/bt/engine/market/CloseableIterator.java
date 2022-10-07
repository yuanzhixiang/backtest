package com.yuanzhixiang.bt.engine.market;

import java.util.Iterator;

/**
 * @author Yuan Zhixiang
 */
public interface CloseableIterator<E> extends Iterator<E>, AutoCloseable {

    @Override
    default void close() throws Exception {
    }
}
