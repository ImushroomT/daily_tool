package com.qss.daily.excel;

import java.util.List;

/**
 * Created by qss on 2017-05-18.
 */
public class LevelNode {
    //节点值
    private String value;
    //父节点
    private LevelNode father;
    //下一级节点
    private List<LevelNode> sons;
    //是否是根节点
    boolean root;
    //另一个表的数据起始行
    private Integer rowStart;
    //另一个表的数据起始列
    private Integer columnStart;

    public boolean isRoot() {
        return root;
    }

    public LevelNode setRoot(boolean root) {
        this.root = root;
        return this;
    }

    public String getValue() {
        return value;
    }

    public LevelNode setValue(String value) {
        this.value = value;
        return this;
    }

    public LevelNode getFather() {
        return father;
    }

    public LevelNode setFather(LevelNode father) {
        this.father = father;
        return this;
    }

    public List<LevelNode> getSons() {
        return sons;
    }

    public LevelNode setSons(List<LevelNode> sons) {
        this.sons = sons;
        return this;
    }


    public Integer getRowStart() {
        return rowStart;
    }

    public LevelNode setRowStart(Integer rowStart) {
        this.rowStart = rowStart;
        return this;
    }

    public Integer getColumnStart() {
        return columnStart;
    }

    public LevelNode setColumnStart(Integer columnStart) {
        this.columnStart = columnStart;
        return this;
    }
}
