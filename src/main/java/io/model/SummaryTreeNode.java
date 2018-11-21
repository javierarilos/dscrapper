package io.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class SummaryTreeNode {
    private int depth;
    private String tag;
    private SummaryTreeNode parent;
    private URL url;
    private String hash;
    private List<SummaryTreeNode> children;

    public SummaryTreeNode(URL url, String tag, String hash, SummaryTreeNode parent) {
        this.url = url;
        this.tag = tag;
        this.hash = hash;
        this.depth = 0;
        this.children = new ArrayList<>();

        if (parent != null) {
            this.parent = parent;
            this.depth = parent.getDepth() + 1;
        }
    }

    public void addChild(SummaryTreeNode childNode) {
        this.children.add(childNode);
    }

    public int getMaxDepth() {
        SummaryTreeNode currNode = this;
        while (currNode.hasChildren()) {
            currNode = currNode.getChildren().get(0);
        }
        return currNode.depth;
    }

    private boolean hasChildren() {
        return this.children.size() >= 1;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof SummaryTreeNode)) {
            return false;
        }
        return this.url != null && url.equals(((SummaryTreeNode) other).getUrl());
    }

    @Override
    public String toString() {
        StringBuilder indentSb = new StringBuilder();

        for (int i = 0; i < depth; i++) {
            indentSb.append(' ');
        }

        StringBuilder sb = new StringBuilder();
        char[] bs = hash.substring(0, 10).toCharArray();
        String[] hexs = new String[bs.length];
        for (int i = 0; i < bs.length; i++) {
            hexs[i] = Integer.toHexString(bs[i]);
        }

        sb.append(indentSb)
                .append("URL: ").append(url)
                .append(" Tag: ").append(tag)
                .append(" Hash: ").append(StringUtils.join(hexs, ""))
                .append("\n");

        children.forEach(c -> sb.append(c.toString()));
        return sb.toString();
    }

    public boolean hasChild(URL url) {
        if (url == null) {
            return false;
        }
        return children.stream().anyMatch(c -> url.equals(c.getUrl()));
    }
}
