# 自定义因子

## 快速上手

例如我们要创建一个 `FactorVolumeSort` 的因子，首先创建一个叫 `FactorVolumeSort` 的类，然后继承 `Factor` 并实现方法。

```java
public class FactorVolumeSort implements Factor {
    @Override
    public void bind(Context context, Factors factors) {

    }
}
```

bind 方法的调用时机是在每个新的因子过来时会进行调用，直接在 bind 内部实现因子的计算逻辑，然后用 `Local` 将其保存到 factors 中，下面是一个将收盘价保存到因子内的实现。

```java
public class FactorVolumeSort implements Factor {

    private static final Local<Factors, Double> VALUE = new Local<>();

    @Override
    public void bind(Context context, Factors factors) {
        VALUE.set(factors, factors.getClose().getPrice());
    }
}
```

然后是获取该因子的值，可以在 `FactorVolumeSort` 中增加 get 方法，让使用者可以通过 get 来获取到该因子的数据。

```java
    /**
     * @param factors 因子
     * @param offset  从当前因子的位置进行偏移，0 为获取当前因子的数据，-1 为获取前一个因子的数据，offset 必须小于等于 0
     * @return 该因子的数据
     */
    public static Double get(Factors factors, int offset) {
        return VALUE.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
    }
```

最后是如何注册自定义因子并获取因子的值。

```java
public class DefaultStrategy implements Strategy {

    public DefaultStrategy(Configuration configuration) {
        // 注册自定义因子
        configuration.registerFactor(new FactorVolumeSort());
    }

    @Override
    public void next(Context context, Factors factors) {
        // 获取因子的值
        System.out.println("Close price: " + FactorVolumeSort.get(factors, 0));
    }
}
```

## 动态计算

如果按照上面的方法来计算因子，会有下面两个问题

1. 复权会影响到计算结果的场景就会出现计算错误
2. 未调用该因子依然会进行计算，数据量大的场景性能不好

对于这些问题可以通过 `SuppliedLocal` 进行优化，接下来会对上面的例子进行改造，让其能够每次返回的都是真实价格的收盘价（因为有复权存在，所以复权后的收盘价会不断变化）。

改造时将 `Local` 换成 `SuppliedLocal`，并实现对应的 `Supplier` 方法，最后调用 `SuppliedLocal` 的 `setSupplier` 方法将 `Supplier` 放进去即可，以下是改造后的代码。

```java
public class FactorVolumeSort implements Factor {

    /**
     * @param factors period
     * @param offset  the offset from the current bar position, which is less than or equal to 0
     * @return value
     */
    public static Double get(Factors factors, int offset) {
        return VALUE.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
    }

    // 上面 get 的部分无需改动

    // 这里 VALUE 的类型改为 SuppliedLocal
    private static final SuppliedLocal<Factors, Double> VALUE = new SuppliedLocal<>();

    // 实现 Supplier 方法
    private static Supplier<Double> getSupplier(Context context, Factors factors) {
        return () -> {
            return FactorRealPrice.get(factors).getClose();
        };
    }

    @Override
    public void bind(Context context, Factors factors) {
        // 调用 setSupplier 注入 Supplier 方法
        VALUE.setSupplier(factors, getSupplier(context, factors));
    }
}
```

注意，这里没有对 Supplier 计算的值进行缓存，所以每次调用都需要重新计算，实际使用时根据具体场景选择是否缓存计算出的值，缓存的逻辑用 `Local` 将值保存到 factors 中即可。
