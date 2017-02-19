package com.company;

import java.util.ArrayList;

/**
 * Created by Sahib Pandori on 2/18/2017.
 */
public class Node {

    Feature node;
    ArrayList<Node> parents;
    ArrayList<Node> children;
    double key;

    Node (Feature node) {
        this.key = Double.MIN_VALUE;
        this.node = node;
        parents = new ArrayList<>();
        children = new ArrayList<>();
    }

    void updateKeys() {
        double edgeWeight = 0;
        for (Node child: children) {
            for (MutualInformation mutualInformation: TAN.allInformation)
                if (mutualInformation.featureX == node && mutualInformation.featureY == child.node)
                    edgeWeight = mutualInformation.mutualInformation;
            if (edgeWeight > child.key)
                child.key = edgeWeight;
        }
    }

}
