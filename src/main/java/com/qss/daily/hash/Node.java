package com.qss.daily.hash;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiushengsen
 * @dateTime 2018/8/12 下午3:28
 * @descripiton
 **/
public class Node {
    private String ip;

    private Map<String, Object> values;

    public String getIp() {
        return ip;
    }

    public Node setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public Node setValues(Map<String, Object> values) {
        this.values = values;
        return this;
    }

    public Node(String ip) {
        this.ip = ip;
        this.values = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return ip != null ? ip.equals(node.ip) : node.ip == null;
    }

    @Override
    public int hashCode() {
        return ip != null ? ip.hashCode() : 0;
    }
}
