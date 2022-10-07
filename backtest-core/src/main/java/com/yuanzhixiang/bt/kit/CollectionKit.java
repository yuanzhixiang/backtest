package com.yuanzhixiang.bt.kit;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Yuan Zhixiang
 */
public class CollectionKit {

    public static <E> List<E> emptyList() {
        return Collections.emptyList();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

}
