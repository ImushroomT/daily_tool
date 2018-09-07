package com.qss.daily;

import com.qss.daily.hash.Cluster;
import com.qss.daily.hash.ConsistHashCluster;
import com.qss.daily.hash.Node;
import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author qiushengsen
 * @company 京东成都研究院-供应链
 * @dateTime 2018/8/12 下午4:08
 * @descripiton
 **/
public class ConsistHashTest {

    public static final String[] ips = new String[] {
            "x4.1.0.4", "a3.2.0.2", "g2.3.0.3", "e1.4.0.6"
    };

    public static final int VALUE_NUM = 60000;

    public static final String KEY_PREFIX = "KEY#";

    @Test
    public void test_add_node() {
        Cluster cluster = new ConsistHashCluster();
        for (String ip : ips) {
            cluster.addNode(new Node(ip));
        }
        Random random = new Random();
        IntStream.range(0, VALUE_NUM)
                .forEach((index) -> cluster.put(KEY_PREFIX + index, KEY_PREFIX));
        cluster.getNodes().forEach((node) -> System.out.println(node.getIp() + ":" + node.getValues().size()));

        cluster.addNode(new Node("1.5.0.2"));
        long onTargetCount = IntStream.range(0, VALUE_NUM)
                .filter((index) -> cluster.get(KEY_PREFIX + index) != null)
                .count();
        System.out.println(onTargetCount * 1.0 / (VALUE_NUM * 1.0));
    }
}
