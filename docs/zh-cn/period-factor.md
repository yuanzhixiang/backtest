# 周期类因子

## 基本使用

使用周期类因子很简单，只需要注册一下后续便可以通过通过周期类因子的静态方法获取数据，以下是周线因子的使用示例。


```java
public class TestStrategy implements Strategy {

    public TestStrategy(Configuration configuration) {
        // 注册周线因子
        configuration.registerPeriodFactor(new PeriodFactorWeekly());
    }

    @Override
    public void next(Context contextService, Factors factors) {
        // 获取周线因子
        Factors weeklyFactors = PeriodFactorWeekly.get(factors);
    }
}
```

使用上述方法可以满足对因子的基本使用场景，周期因子中一般还会有 `change` 方法，使用该方法可以判断是否有出现跨周期的情况

```java
    if (PeriodFactorWeekly.change(factors)) {
        // do something...
    }
```

## 低级别周期因子

对于使用日线以及日线以下的因子需要使用分钟线数据，以下是使用日线因子的例子

```java
public class TestStrategy implements Strategy {

    public TestStrategy(Configuration configuration) {
        // 使用分钟线数据源
        configuration.setDataSource(new DataSourceStockMinute());
        // 注册日线因子
        configuration.registerPeriodFactor(new PeriodFactorDaily());
    }

    @Override
    public void next(Context contextService, Factors factors) {
        // 判断是否出现跨日
        if (PeriodFactorDaily.change(factors)) {
            // do something...
        }
    }
}
```