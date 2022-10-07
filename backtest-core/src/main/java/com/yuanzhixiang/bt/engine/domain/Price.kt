package com.yuanzhixiang.bt.engine.domain

import com.yuanzhixiang.bt.engine.Local.LocalMap
import com.yuanzhixiang.bt.engine.LocalVariable
import java.math.BigDecimal

/**
 * @author Yuan Zhixiang
 */
class Price(val price: Double, val adjustment: BigDecimal) : LocalVariable {
    private var localMap: LocalMap? = null
    override fun getLocalMap(): LocalMap {
        return localMap!!
    }

    override fun setLocalMap(localMap: LocalMap) {
        this.localMap = localMap
    }
}