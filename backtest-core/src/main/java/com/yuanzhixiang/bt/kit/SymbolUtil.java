package com.yuanzhixiang.bt.kit;

/**
 * @author Yuan Zhixiang
 */
public class SymbolUtil {

    public static boolean isSSE(String symbol) {
        // Shanghai Stock Exchange, abbreviation is SSE

        // Main board
        return symbol.startsWith("600")
            || symbol.startsWith("601")
            || symbol.startsWith("603")
            || symbol.startsWith("605")

            // Second board
            // Sci-Tech innovation board，abbreviation is STAR Market
            || symbol.startsWith("688")
            || symbol.startsWith("689");
    }

    public static boolean isSZSE(String symbol) {
        // Shenzhen Stock Exchange, abbreviation is SZSE

        // Main board
        return symbol.startsWith("000")
            || symbol.startsWith("001")

            // Second board
            // China Growth Enterprise Market, abbreviation is Chinext
            || symbol.startsWith("300")
            || symbol.startsWith("301");
    }

}
