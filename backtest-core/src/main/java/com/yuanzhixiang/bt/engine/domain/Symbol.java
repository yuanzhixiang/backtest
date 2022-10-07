package com.yuanzhixiang.bt.engine.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.engine.Local.LocalMap;
import com.yuanzhixiang.bt.engine.LocalVariable;
import com.yuanzhixiang.bt.engine.BackTestException;

/**
 * @author Yuan Zhixiang
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

    private static final Local<ContextService, Map<Symbol, LocalMap>> SYMBOL_LOCAL_MAP = new Local<>();

    @Override
    public LocalMap getLocalMap() {
        ContextService contextService = ContextLocal.get();

        if (contextService == null) {
            throw new BackTestException("Use local map must be in engine runtime.");
        }

        Map<Symbol, LocalMap> symbolLocalMap = SYMBOL_LOCAL_MAP.get(contextService);
        if (symbolLocalMap == null) {
            symbolLocalMap = new HashMap<>(16);
            SYMBOL_LOCAL_MAP.set(contextService, symbolLocalMap);
            return null;
        } else {
            return symbolLocalMap.get(this);
        }
    }

    @Override
    public void setLocalMap(LocalMap localMap) {
        ContextService contextService = ContextLocal.get();

        if (contextService == null) {
            throw new BackTestException("Use local map must be in engine runtime.");
        }

        Map<Symbol, LocalMap> symbolLocalMap = SYMBOL_LOCAL_MAP.get(contextService);
        if (symbolLocalMap == null) {
            symbolLocalMap = new HashMap<>(16);
            SYMBOL_LOCAL_MAP.set(contextService, symbolLocalMap);
        }

        symbolLocalMap.put(this, localMap);
    }
}
