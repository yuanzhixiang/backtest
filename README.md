# 说明

本框架为量化回测框架，量化用到的数据缓存在本地用户目录的 .backtest 文件夹下。demo 在 backtest-core 单元测试目录下，类名为
com.yuanzhixiang.backtest.engine.strategy.demo.SingleMACDStrategy。

本框架不仅支持 A 股，可通过更换数据源支持任何有交易数据的市场。

# FAQ

- 问：日线数据和 tick 数据缓存在哪里？
- 答：用户目录下的 .backtest 文件夹中
