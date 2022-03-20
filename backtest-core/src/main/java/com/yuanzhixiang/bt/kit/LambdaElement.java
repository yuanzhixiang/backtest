package com.yuanzhixiang.bt.kit;

/**
 * @author yuanzhixiang
 */
public class LambdaElement<E> {

    private E e;

    public LambdaElement(E e) {
        this.e = e;
    }

    public E getE() {
        return e;
    }

    public void setE(E e) {
        this.e = e;
    }
}
