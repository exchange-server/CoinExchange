package com.spark.blockchain.rpcclient;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;

abstract class ListMapWrapper<X> extends AbstractList<X> {
    public final List<Map> list;

    public ListMapWrapper(List<Map> list) {
        this.list = list;
    }

    protected abstract X wrap(Map var1);

    public X get(int index) {
        return this.wrap((Map)this.list.get(index));
    }

    public int size() {
        return this.list.size();
    }
}
