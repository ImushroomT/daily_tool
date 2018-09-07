package com.qss.daily.hash;

import java.util.HashSet;
import java.util.Set;

/**
 * @author qiushengsen
 * @dateTime 2018/8/12 下午3:31
 * @descripiton
 **/
public abstract class Cluster {
    Set<Node> nodes;

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void removeNode(Node node) {
        nodes.remove(node);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Cluster() {
        nodes = new HashSet<>();
    }

    /**这里使用了redis的hash计算方法，搭配上虚拟出来的多个节点散列效果很不错*/
    protected int hash(int key) {
        key += ~(key << 15);
        key ^= (key >> 10);
        key += (key << 3);
        key ^= (key >> 6);
        key += ~(key << 11);
        key ^= (key >> 16);
        return key;
    }

    public abstract Object get(String key);

    public abstract void put(String key, Object value);
}
