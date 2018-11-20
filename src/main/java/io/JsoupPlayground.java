package io;

import io.model.SummaryTreeNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JsoupPlayground {
    private static MessageDigest messageDigest;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//        URL baseUrl = new URL("http://www.github.com");
        URL baseUrl = new URL("https://www.eldiario.es");
        int maxDepth = 3;

        SummaryTreeNode tree = scrape(baseUrl, maxDepth);
    }

    private static SummaryTreeNode scrape(URL baseUrl, int maxDepth) throws IOException {
        return scrape(baseUrl, maxDepth, null, null);
    }

    private static SummaryTreeNode scrape(URL baseUrl, int maxDepth, SummaryTreeNode parent, SummaryTreeNode root) {
        if (!baseUrl.getProtocol().toLowerCase().contains("http")) {
            return new SummaryTreeNode(baseUrl, baseUrl.toString(), parent);
        }

        Document d = null;
        try {
            d = Jsoup.parse(baseUrl, 10000);
        } catch (IOException e) {
            System.out.println("Error in " + baseUrl + " = " + e);
            return new SummaryTreeNode(baseUrl, "GOT ERROR.", parent);
        }

        System.out.println(String.format("baseUrl=%s title='%s' charset='%s'", baseUrl.toString(), d.title(), d.charset()));


        SummaryTreeNode currentNode = new SummaryTreeNode(baseUrl, getHash(d.toString()), parent);

        if (currentNode.getDepth() < maxDepth) {
            Set<URL> currentNodeLinks = d.select("a[href]").stream()
                    .map(link -> link.attributes().get("href"))
                    .map(JsoupPlayground::getURL)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (URL url : currentNodeLinks) {
                if (root != null && !root.hasChild(url)) {
                    System.out.println("adding... " + url);
                    currentNode.addChild(scrape(url, maxDepth, currentNode, root));
                } else {
                    System.out.println("Root already contains url=" + url);
                }
            }
        }

        System.out.println("currentNode");
        System.out.println(currentNode);

        return currentNode;
    }

    private static URL getURL(String spec) {
        try {
            return new URL(spec);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private static String getHash(String d) {
        getMessageDigest().update(d.getBytes());
        return new String(messageDigest.digest());
    }

    private static MessageDigest getMessageDigest() {
        try {
            if (messageDigest == null) {
                messageDigest = MessageDigest.getInstance("SHA-256");
            }
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("This should never happen!!!!!");
            System.exit(666);
            return null;
        }
    }
}
