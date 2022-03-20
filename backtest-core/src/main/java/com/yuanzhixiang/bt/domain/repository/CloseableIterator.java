package com.yuanzhixiang.bt.domain.repository;

import java.util.Iterator;

/**
 * @author yuanzhixiang
 */
public interface CloseableIterator<E> extends Iterator<E>, AutoCloseable {

    @Override
    default void close() throws Exception {
    }
}
