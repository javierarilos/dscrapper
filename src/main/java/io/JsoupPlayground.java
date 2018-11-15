package io;

import io.model.SummaryTreeNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsoupPlayground {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        URL baseUrl = new URL("http://www.eldiario.es");
        int maxDepth = 3;


        Document d = Jsoup.parse(baseUrl, 10000);

        System.out.println(String.format("title='%s' charset='%s'", d.title(), d.charset()));


        SummaryTreeNode currentNode = new SummaryTreeNode(baseUrl, getHash(d.toString()));

        while (currentNode.getDepth() < maxDepth) {
            Set<URL> links = d.select("a[href]").stream()
                    .map(link -> link.attributes().get("href"))
                    .map(JsoupPlayground::getURL)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (URL url : links) {
                Document childDocument = Jsoup.parse(url, 10000);

                SummaryTreeNode childNode = new SummaryTreeNode(url, getHash(childDocument.toString()), currentNode);

                currentNode.addChild(childNode);
            }
            currentNode = currentNode.getChildren().get(0);
        }


    }

    private static URL getURL(String spec) {
        try {
            return new URL(spec);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private static String getHash(String d) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(d.getBytes());
        return new String(messageDigest.digest());
    }
}
