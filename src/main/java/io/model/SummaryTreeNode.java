package io.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Data
public class SummaryTreeNode {
    private String tag;
    private URL url;
    private String hash;
    private List<SummaryTreeNode> children;

    public SummaryTreeNode(URL url, String tag, String hash) {
        this.url = url;
        this.tag = tag;
        this.hash = toHexString(hash);
        this.children = new ArrayList<>();
    }

    public void addChild(SummaryTreeNode childNode) {
        this.children.add(childNode);
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

        StringBuilder sb = new StringBuilder();

        sb.append(indentSb)
                .append("URL: ").append(url)
                .append(" Tag: ").append(tag)
                .append(" Hash: ").append(hash)
                .append("\n");

        children.forEach(c -> sb.append(c.toString()));
        return sb.toString();
    }

    private String toHexString(String hash) {
        String[] hexs = new String[hash.length()];
        for (int i = 0; i < hash.length(); i++) {
            hexs[i] = Integer.toHexString(hash.charAt(i));
        }

        return StringUtils.join(hexs, "");
    }

    public boolean hasChild(URL url) {
        if (url == null) {
            return false;
        }
        return children.stream().anyMatch(c -> url.equals(c.getUrl()));
    }
}
