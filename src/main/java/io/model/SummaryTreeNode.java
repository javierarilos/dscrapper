package io.model;

import lombok.Data;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Data
public class SummaryTreeNode {
    private int depth;
    private SummaryTreeNode parent;
    private URL url;
    private String hash;
    private List<SummaryTreeNode> children;

    public SummaryTreeNode(URL url, String hash) {
        this.url = url;
        this.hash = hash;
        this.depth = 0;
        this.children = new ArrayList<>();
    }

    public SummaryTreeNode(URL url, String hash, SummaryTreeNode parent) {
        this(url, hash);
        this.parent = parent;
        this.depth = parent.getDepth() + 1;
    }

    public void addChild(SummaryTreeNode childNode) {
        this.children.add(childNode);
    }

    public int getMaxDepth() {
        SummaryTreeNode currNode = this;
        while(currNode.hasChildren()) {
            currNode = currNode.getChildren().get(0);
        }
        return currNode.depth;
    }

    private boolean hasChildren() {
        return this.children.size() >= 1;
    }
}
