//package com.yuanzhixiang.bt.factor.common;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import com.yuanzhixiang.bt.domain.datasource.DataSourceStockDaily;
//import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
//import com.yuanzhixiang.bt.engine.Application;
//import com.yuanzhixiang.bt.engine.Configuration;
//
///**
// * @author yuanzhixiang
// */
//public class FactorCciTest {
//
//    @Test
//    public void _0001() {
//        Configuration configuration = new Configuration();
//        configuration.setDataSource(new DataSourceStockDaily());
//        configuration.setTimeRange(
//            LocalDateTime.of(2021, 1, 1, 0, 0),
//            LocalDateTime.of(2021, 10, 30, 0, 0)
//        );
//        configuration.setSymbolList(List.of(
//            new Symbol("601919")
//        ));
//
//        configuration.registerFactor(new FactorCci());
//
//        new Application(configuration).run((context, factors) -> {
//            if (LocalDate.of(2021, 1, 5).equals(factors.getTradeDate().toLocalDate())) {
//                Assert.assertEquals(FactorCci.get(factors, 0), 150.75, 0);
//            }
//
//            // The restoration factor changes on the day.
//            if (LocalDate.of(2021, 9, 30).equals(factors.getTradeDate().toLocalDate())) {
//                Assert.assertEquals(FactorCci.get(factors, 0), -161.99, 0);
//            }
//        });
//    }
//}
