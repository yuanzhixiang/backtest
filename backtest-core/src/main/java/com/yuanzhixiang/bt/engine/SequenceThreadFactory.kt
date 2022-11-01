package com.yuanzhixiang.bt.engine

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

val SEQUENCE = AtomicInteger()

class SequenceThreadFactory(
    private val namePrefix: String
) : ThreadFactory {

    override fun newThread(task: Runnable): Thread {
        return Thread(task, "$namePrefix-strategy-thread-${SEQUENCE.getAndAdd(1)}")
    }
}