//package com.yuanzhixiang.bt.factor.common;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.junit.Test;
//
//import com.yuanzhixiang.bt.engine.domain.Symbol;
//import com.yuanzhixiang.bt.engine.Application;
//import com.yuanzhixiang.bt.engine.Configuration;
//import com.yuanzhixiang.bt.factor.common.RealPriceFactor.RealPrice;
//
///**
// * @author Yuan Zhixiang
// */
//public class RealPriceFactorTest {
//
//    /**
//     * Verify that the real price is correct.
//     */
//    @Test
//    public void _0001() {
//        Configuration configuration = new Configuration();
////        configuration.setDataSource(new DataSourceStockDaily());
//        configuration.setTimeRange(
//            LocalDateTime.of(2021, 7, 12, 0, 0),
//            LocalDateTime.of(2021, 7, 30, 0, 0)
//        );
//        configuration.setSymbolList(List.of(
//            new Symbol("601919")
//        ));
//        new Application(configuration).run((context, factors) -> {
//            if (LocalDate.of(2021, 7, 13).equals(factors.getTradeDate().toLocalDate())) {
//                // Verify original price
//                assertEquals(29.64, factors.getOpen().getPrice(), 0);
//                assertEquals(30.88, factors.getClose().getPrice(), 0);
//                assertEquals(31.04, factors.getHigh().getPrice(), 0);
//                assertEquals(29.31, factors.getLow().getPrice(), 0);
//
//                // Verify real price
//                RealPrice realPrice = RealPriceFactor.get(factors, 0);
//                assertNotNull(realPrice);
//                assertEquals(29.64, realPrice.getOpen(), 0);
//                assertEquals(30.88, realPrice.getClose(), 0);
//                assertEquals(31.04, realPrice.getHigh(), 0);
//                assertEquals(29.31, realPrice.getLow(), 0);
//            }
//
//            // The restoration factor changes on the day.
//            if (LocalDate.of(2021, 7, 14).equals(factors.getTradeDate().toLocalDate())) {
//                // Verify original price
//                assertEquals(24, factors.getOpen().getPrice(), 0);
//                assertEquals(23.16, factors.getClose().getPrice(), 0);
//                assertEquals(24.15, factors.getHigh().getPrice(), 0);
//                assertEquals(22.85, factors.getLow().getPrice(), 0);
//
//                // Verify real price
//                RealPrice realPrice = RealPriceFactor.get(factors, 0);
//                assertNotNull(realPrice);
//                assertEquals(24, realPrice.getOpen(), 0);
//                assertEquals(23.16, realPrice.getClose(), 0);
//                assertEquals(24.15, realPrice.getHigh(), 0);
//                assertEquals(22.85, realPrice.getLow(), 0);
//
//                // Verify real price
//                RealPrice pRealPrice = RealPriceFactor.get(factors, -1);
//                assertNotNull(pRealPrice);
//                assertEquals(22.8, pRealPrice.getOpen(), 0);
//                assertEquals(23.75, pRealPrice.getClose(), 0);
//                assertEquals(23.88, pRealPrice.getHigh(), 0);
//                assertEquals(22.55, pRealPrice.getLow(), 0);
//            }
//        });
//    }
//
//}
