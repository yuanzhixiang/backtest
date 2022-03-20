package com.yuanzhixiang.bt.factor.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.engine.Application;
import com.yuanzhixiang.bt.engine.Configuration;
import com.yuanzhixiang.bt.factor.common.FactorMa.Ma;

/**
 * @author yuanzhixiang
 */
public class FactorMaTest  {

    /**
     * Verify the correctness of the price before restoration and the average price after restoration.
     */
    @Test
    public void _0001() {
        Configuration configuration = new Configuration();
//        configuration.setDataSource(new DataSourceStockDaily());
        configuration.registerFactor(new FactorMa(5, 10, 20, 60));
        configuration.setTimeRange(
            LocalDateTime.of(2021, 1, 1, 0, 0),
            LocalDateTime.of(2021, 10, 30, 0, 0)
        );
        configuration.setSymbolList(List.of(
            new Symbol("601919")
        ));
        new Application(configuration).run((context, factors) -> {
            if (LocalDate.of(2021, 1, 5).equals(factors.getTradeDate().toLocalDate())) {
                Ma ma = FactorMa.get(factors, 0);
                Assert.assertEquals(12.64, ma.getMa(5), 0);
                Assert.assertEquals(11.7, ma.getMa(10), 0);
                Assert.assertEquals(10.62, ma.getMa(20), 0);
                Assert.assertEquals(8.8, ma.getMa(60), 0);
            }

            // The restoration factor changes on the day.
            if (LocalDate.of(2021, 9, 30).equals(factors.getTradeDate().toLocalDate())) {
                Ma ma = FactorMa.get(factors, 0);
                Assert.assertEquals(18.57, ma.getMa(5), 0);
                Assert.assertEquals(19.76, ma.getMa(10), 0);
                Assert.assertEquals(20.44, ma.getMa(20), 0);
                Assert.assertEquals(20.77, ma.getMa(60), 0);
            }
        });
    }

    /**
     * Verification factor duplicate registration.
     */
    @Test
    public void _0002() {
        Configuration configuration = new Configuration();
        configuration.registerFactor(new FactorMa(5, 10, 20, 60));
        configuration.registerFactor(new FactorMa(5));
        Collection<Factor<?>> factors = configuration.getAllFactor();
        Assert.assertEquals(1, factors.size());
        Assert.assertEquals(4, ((FactorMa) factors.toArray()[0]).getCycleSet().size());

        configuration.registerFactor(new FactorMa(120));
        Assert.assertEquals(1, factors.size());
        Assert.assertEquals(5, ((FactorMa) factors.toArray()[0]).getCycleSet().size());
    }
}

