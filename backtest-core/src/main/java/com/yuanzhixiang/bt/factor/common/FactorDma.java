//package com.yuanzhixiang.bt.factor.common;
//
//import static java.math.BigDecimal.valueOf;
//
//import java.util.function.Supplier;
//
//import com.yuanzhixiang.bt.engine.domain.Factors;
//import com.yuanzhixiang.bt.engine.Configuration;
//import com.yuanzhixiang.bt.engine.Context;
//import com.yuanzhixiang.bt.engine.ContextLocal;
//import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;
//import com.yuanzhixiang.bt.factor.common.FactorMa.Ma;
//
///**
// * @author Yuan Zhixiang
// */
//public class FactorDma implements Factor<FactorDma> {
//
//    public static Dma get(Factors factors, int offset) {
//        return VALUE.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
//    }
//
//    public static class Dma {
//        private final double dma;
//        private final double ama;
//
//        public Dma(double dma, double ama) {
//            this.dma = dma;
//            this.ama = ama;
//        }
//
//        public double getDma() {
//            return dma;
//        }
//
//        public double getAma() {
//            return ama;
//        }
//    }
//
//    private static final SuppliedLocal<Factors, Dma> VALUE = new SuppliedLocal<>();
//
//    private static Supplier<Dma> getSupplier(int shortCycle, int longCycle, Factors factors) {
//        return () -> {
//            Ma ma = FactorMa.get(factors, 0);
//            Double shortMa = ma.getMa(shortCycle);
//            Double longMa = ma.getMa(longCycle);
//
//            return new Dma(valueOf(shortMa).subtract(valueOf(longMa)).doubleValue(), shortMa);
//        };
//    }
//
//    private final int shortCycle;
//    private final int longCycle;
//
//    public FactorDma(int shortCycle, int longCycle) {
//        this.shortCycle = shortCycle;
//        this.longCycle = longCycle;
//    }
//
//    @Override
//    public void register(Configuration configuration, FactorDma old) {
//        // ignore old factor
//        configuration.registerFactor(new FactorMa(shortCycle, longCycle));
//    }
//
//    @Override
//    public void bind(Context context, Factors factors) {
//        VALUE.setSupplier(factors, getSupplier(shortCycle, longCycle, factors));
//    }
//}
