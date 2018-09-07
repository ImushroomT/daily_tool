package com.qss.daily.hash;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 * @author qiushengsen
 * @dateTime 2018/8/12 下午3:33
 * @descripiton 一致性hash
 **/
public class ConsistHashCluster extends Cluster {
    private SortedMap<Integer, Node> virtualNodesMap = new TreeMap<>();

    private static final int VIRTUAL_NUMS = 128;

    public ConsistHashCluster() {
        super();
    }

    @Override
    public void addNode(Node node) {
        super.addNode(node);
        IntStream.range(0, VIRTUAL_NUMS)
                .forEach((index) ->
                    virtualNodesMap.put(hash(Objects.hash(node.getIp() + "#" + index)), node)
                );
    }

    @Override
    public void removeNode(Node node) {
        super.removeNode(node);
        IntStream.range(0, VIRTUAL_NUMS)
                .forEach((index) -> virtualNodesMap.remove(hash(Objects.hash(node.getIp() + "#" + index))));
    }

    @Override
    public Object get(String key) {
        int hKey = hash(Objects.hash(key));
        SortedMap<Integer, Node> sortedMap = virtualNodesMap.lastKey() < hKey ?
                virtualNodesMap.tailMap(0) : virtualNodesMap.tailMap(hKey);
        if (sortedMap.isEmpty()) {
            return null;
        }
        Node targetNode = sortedMap.get(sortedMap.firstKey());
        return targetNode.getValues().get(key);
    }

    @Override
    public void put(String key, Object value) {
        int hKey = hash(Objects.hash(key));
        SortedMap<Integer, Node> sortedMap = virtualNodesMap.lastKey() < hKey ?
                virtualNodesMap.tailMap(0) : virtualNodesMap.tailMap(hKey);
        if (sortedMap.isEmpty()) {
            throw new RuntimeException("can not find available node");
        }
        Node targetNode = sortedMap.get(sortedMap.firstKey());
        targetNode.getValues().put(key, value);
    }
}
