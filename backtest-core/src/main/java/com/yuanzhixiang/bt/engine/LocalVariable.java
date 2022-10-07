package com.yuanzhixiang.bt.engine;

import com.yuanzhixiang.bt.engine.Local.LocalMap;

/**
 * @author Yuan Zhixiang
 */
public interface LocalVariable {
    // todo default local variable 的状态可以放到 thread 中，这是因为策略在运行的时候必然占用一个线程，而不会在线程间切换，所以可以
    //   将状态保存在线程中
    LocalMap getLocalMap();

    void setLocalMap(LocalMap localMap);

}
