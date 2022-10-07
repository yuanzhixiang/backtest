package com.yuanzhixiang.bt.engine

class Launcher(val configuration: Configuration) {

    fun start(name: String = "", async: Boolean = true, strategy: Strategy) {
        val task = Task(configuration, strategy)
        if (async) {
            StrategyThreadFactory(name).newThread(task).start()
        } else {
            task.run()
        }
    }
}