package com.company;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Sahib Pandori on 2/18/2017.
 */
public class Node {

    Feature node;
    Node parent;
    ArrayList<Node> children;
    double key;

    public static Comparator<Node> comparator = (o1, o2) -> (o1.key > o2.key ? 1 : -1);

    Node (Feature node) {
        this.key = Double.MAX_VALUE;
        this.node = node;
        children = new ArrayList<>();
        parent = null;
    }

}
