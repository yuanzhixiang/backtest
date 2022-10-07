package com.yuanzhixiang.bt.engine;

/**
 * @author Yuan Zhixiang
 */
public class BackTestException extends RuntimeException {
    public BackTestException(Throwable cause) {
        super(cause);
    }

    public BackTestException(String message) {
        super(message);
    }

    public BackTestException(String message, Throwable cause) {
        super(message, cause);
    }
}
