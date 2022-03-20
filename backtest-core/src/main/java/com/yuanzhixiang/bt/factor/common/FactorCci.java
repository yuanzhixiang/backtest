//package com.yuanzhixiang.bt.factor.common;
//
//import static com.yuanzhixiang.bt.kit.BigDecimalConstants._0;
//import static com.yuanzhixiang.bt.kit.BigDecimalConstants._3;
//import static java.math.BigDecimal.valueOf;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.function.Supplier;
//
//import com.yuanzhixiang.bt.domain.model.valobj.Factors;
//import com.yuanzhixiang.bt.engine.Configuration;
//import com.yuanzhixiang.bt.engine.Context;
//import com.yuanzhixiang.bt.engine.ContextLocal;
//import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;
//import com.yuanzhixiang.bt.factor.common.RealPriceFactor.RealPrice;
//import com.yuanzhixiang.bt.kit.LambdaElement;
//
///**
// * @author yuanzhixiang
// */
//public class FactorCci implements Factor<FactorCci> {
//
//    public static Double get(Factors factors, int offset) {
//        return VALUE.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
//    }
//
//    private static final SuppliedLocal<Factors, Double> VALUE = new SuppliedLocal<>();
//
//    private static final BigDecimal FACTOR = valueOf(0.015);
//
//    private static Supplier<Double> getSupplier(int cycle, BigDecimal cycleBigDecimal, Factors factors) {
//        return () -> {
//            BigDecimal maBigDecimal = valueOf(FactorMa.get(factors, 0).getMa(cycle));
//
//            BigDecimal mdBigDecimal = calculateMd(cycle, cycleBigDecimal, factors, maBigDecimal);
//
//            Factors mergeFactor = MergerFactor.get(factors, 0, cycle);
//
//            BigDecimal tpBigDecimal = RealPriceFactor.addForCompute(factors.getSymbol(), mergeFactor.getClose(),
//                mergeFactor.getHigh(), mergeFactor.getLow()).divide(_3, 8, RoundingMode.HALF_UP);
//
//            return tpBigDecimal.subtract(maBigDecimal).divide(mdBigDecimal, 8, RoundingMode.HALF_UP)
//                .divide(FACTOR, 2, RoundingMode.HALF_UP).doubleValue();
//        };
//    }
//
//    private static BigDecimal calculateMd(int cycle, BigDecimal cycleBigDecimal, Factors factors,
//        BigDecimal maBigDecimal) {
//        BigDecimal mdBigDecimal = _0.add(maBigDecimal.multiply(cycleBigDecimal));
//        LambdaElement<BigDecimal> element = new LambdaElement<>(mdBigDecimal);
//        Factors.foreach(cycle, factors, offsetFactors -> {
//            RealPrice realPrice = RealPriceFactor.get(offsetFactors, 0);
//            element.setE(element.getE().subtract(valueOf(realPrice.getClose())));
//        });
//        return element.getE().divide(cycleBigDecimal, 8, RoundingMode.HALF_UP);
//    }
//
//    public FactorCci() {
//        this(14);
//    }
//
//    public FactorCci(int cycle) {
//        this.cycle = cycle;
//    }
//
//    private final int cycle;
//
//    @Override
//    public void register(Configuration configuration, FactorCci old) {
//        // ignore old factor
//        configuration.registerFactor(new FactorMa(cycle));
//    }
//
//    @Override
//    public void bind(Context context, Factors factors) {
//        VALUE.setSupplier(factors, getSupplier(cycle, valueOf(cycle), factors));
//    }
//}
