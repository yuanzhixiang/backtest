# 配置项

## 设置回测时间范围

通过 `setTimeRange` 设计回测时的时间范围

```java
    configuration.setTimeRange(LocalDateTime.of(2010, 1, 1, 0, 0), LocalDateTime.now());
```

也可以像下面这样只设置开始或结束时间，如果只设置开始时间，那么会从指定的开始时间开始回测，如果只设置结束时间，那么会从有数据的第一天开始回测，一直回测到设置的最后一天。

```java
    // 只设置开始时间
    configuration.setTimeRange(LocalDateTime.of(2010, 1, 1, 0, 0), null);

    // 只设置结束时间
    configuration.setTimeRange(null, LocalDateTime.of(2018, 1, 1, 0, 0));
```

备注：框架执行时遍历所有数据，这是因为有些指标会使用之前的数据进行计算，而计算的逻辑用到的数据会追溯到上市第一天的数据，所以会从第一天开始向后遍历。todo 这里有优化空间，虽然计算会用到之前的数据，但是其经过长时间的计算，很早之前的数据可能对精度上不造成影响，所以这里可以优化为只从开始时间一年前开始遍历，对精度应该不会构成很大影响。

## 设置数据源

框架默认实现了两种数据源，一种是分钟数据源，另一种是日线数据源

```java
    // 日线数据源
    configuration.setDataSource(new DataSourceStockDaily());
    
    // 分钟数据源
    configuration.setDataSource(new DataSourceStockMinute());
```

## 设置费用

通过下面的方法可以设置佣金，最低佣金，账户余额

```java
    // 设置佣金，这里设置的是万三
    configuration.setCommissionRate(0.0003);
    // 最低佣金，这里设置的是 5 元
    configuration.setMinCommission(5);
    // 设置账户的初始余额
    configuration.setAccountBalance(1000000);
```

## 设置因子

设置因子通过 `registerFactor` 方法注册因子，下面是注册移动平均线的例子

```java
    configuration.registerFactor(new FactorMa(Arrays.asList(5, 10)));
```

在使用时可以直接输入 `Factor` 前缀，IDE 中会提示出所有能够用于注册的因子

![](/image/1.png)

## 设置周期类因子

设置周期类因子通过 `registerPeriodFactor` 方法注册因子，下面是注册周线因子的例子

```java
    configuration.registerPeriodFactor(new PeriodFactorWeekly());
```

在使用时可以直接输入 `PeriodFactor` 前缀，IDE 中会提示出所有能够用于注册的因子

![](/image/2.png)

## 设置生命周期相关类

生命周期用于在回测的各个时间点完成一些额外的自定义功能，例如报告的生成也是通过生命周期来完成，注册生命周期通过 `registerLifeCycle` 方法来完成，下面是注册回测报告的例子

```java
    // 注册回测报告
    configuration.registerLifeCycle(new DefaultReporter());

    // 注册 Group 回测报告
    configuration.registerLifeCycle(new GroupReporter(true));
```

