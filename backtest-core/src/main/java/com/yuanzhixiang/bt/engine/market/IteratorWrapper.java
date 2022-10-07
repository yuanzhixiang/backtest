package com.yuanzhixiang.bt.engine.market;

/**
 * @author Yuan Zhixiang
 */
public class IteratorWrapper<E> implements CloseableIterator<E> {

    private final CloseableIterator<E> feed;

    private E previous = null;
    private boolean getPrevious = false;

    public IteratorWrapper(CloseableIterator<E> feed) {
        this.feed = feed;
    }

    @Override
    public void close() throws Exception {
        feed.close();
    }

    @Override
    public boolean hasNext() {
        return feed.hasNext() || getPrevious;
    }

    @Override
    public E next() {
        if (getPrevious) {
            getPrevious = false;
            return previous;
        }

        E next = feed.next();
        previous = next;
        return next;
    }

    public void back() {
        getPrevious = true;
    }
}
