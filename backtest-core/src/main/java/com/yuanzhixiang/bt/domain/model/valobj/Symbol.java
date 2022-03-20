package com.yuanzhixiang.bt.domain.model.valobj;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.engine.Local.LocalMap;
import com.yuanzhixiang.bt.engine.LocalVariable;
import com.yuanzhixiang.bt.exception.BackTestException;

/**
 * @author yuanzhixiang
 */
public class Symbol implements LocalVariable {

    private final String code;

    public Symbol(String code) {
        if (code == null) {
            throw new BackTestException("The code can not be null.");
        }
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Symbol)) {
            return false;
        }
        Symbol symbol = (Symbol) o;
        return Objects.equals(code, symbol.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }

    private static final Local<Context, Map<Symbol, LocalMap>> SYMBOL_LOCAL_MAP = new Local<>();

    @Override
    public LocalMap getLocalMap() {
        Context context = ContextLocal.get();

        if (context == null) {
            throw new BackTestException("Use local map must be in engine runtime.");
        }

        Map<Symbol, LocalMap> symbolLocalMap = SYMBOL_LOCAL_MAP.get(context);
        if (symbolLocalMap == null) {
            symbolLocalMap = new HashMap<>(16);
            SYMBOL_LOCAL_MAP.set(context, symbolLocalMap);
            return null;
        } else {
            return symbolLocalMap.get(this);
        }
    }

    @Override
    public void setLocalMap(LocalMap localMap) {
        Context context = ContextLocal.get();

        if (context == null) {
            throw new BackTestException("Use local map must be in engine runtime.");
        }

        Map<Symbol, LocalMap> symbolLocalMap = SYMBOL_LOCAL_MAP.get(context);
        if (symbolLocalMap == null) {
            symbolLocalMap = new HashMap<>(16);
            SYMBOL_LOCAL_MAP.set(context, symbolLocalMap);
        }

        symbolLocalMap.put(this, localMap);
    }
}
