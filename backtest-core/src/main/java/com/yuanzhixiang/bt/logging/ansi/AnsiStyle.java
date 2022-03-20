package com.yuanzhixiang.bt.logging.ansi;


/**
 * {@link AnsiElement Ansi} styles.
 *
 * @author Phillip Webb
 * @since 1.3.0
 */
public enum AnsiStyle implements AnsiElement {

    NORMAL("0"),

    BOLD("1"),

    FAINT("2"),

    ITALIC("3"),

    UNDERLINE("4");

    private final String code;

    AnsiStyle(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
