package com.yuanzhixiang.bt.example.report.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Yuan Zhixiang
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    /**
     * Title of column
     *
     * @return the title of column
     */
    String value() default "";

    boolean percentage() default false;

    CalculateType statisticType() default CalculateType.NONE;

}
