package io.core;

import io.model.SummaryTreeNode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

//        SummaryTreeNode tree = scrape(baseUrl, maxDepth);
        SummaryTreeNode pageWithImages = getPageWithImages(baseUrl);
    }

    private static SummaryTreeNode getPageWithImages(URL baseUrl) {
        if (!baseUrl.getProtocol().toLowerCase().contains("http")) {
            return new SummaryTreeNode(baseUrl, "NON-HTTP", baseUrl.toString(), null);
        }

        Document d;
        try {
            d = Jsoup.parse(baseUrl, 10000);
        } catch (IOException e) {
            System.out.println("Error in " + baseUrl + " = " + e);
            return new SummaryTreeNode(baseUrl, "ERROR", "GOT ERROR.", null);
        }

        System.out.println(String.format("baseUrl=%s title='%s' charset='%s'", baseUrl.toString(), d.title(), d.charset()));


        SummaryTreeNode currentNode = new SummaryTreeNode(baseUrl, "ROOT-PAGE", getHash(d.toString().getBytes()), null);

        Elements images = d.getElementsByTag("img");
        for (Element img : images) {
            String imgUrlStr = img.absUrl("src");
            try {
                URL imgUrl = new URL(imgUrlStr);
                System.out.println("-> getting img: " + imgUrlStr);
//                byte[] imgBytes = getImgJavaLowLevel(imgUrl);
                byte[] imgBytes = getImgHttpClient(imgUrl);

                currentNode.addChild(
                        new SummaryTreeNode(imgUrl, "IMG", getHash(imgBytes), currentNode)
                );

            } catch (IOException e) {
                // TODO IMPLEMENT!!
                e.printStackTrace();
            }
        }
        System.out.println("===============================================");
        System.out.println(currentNode);
        System.out.println("===============================================");
        return currentNode;

    }

    private static byte[] getImgHttpClient(URL imgUrl) throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(imgUrl.toString());
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        ByteArrayOutputStream outputByteStream = new ByteArrayOutputStream();
        response1.getEntity().writeTo(outputByteStream);

        return outputByteStream.toByteArray();
    }

    private static byte[] getImgJavaLowLevel(URL imgUrl) throws IOException {

        InputStream imgStream = imgUrl.openStream();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        for (int b; (b = imgStream.read()) != -1; ) {
            outStream.write(b);
        }

        return outStream.toByteArray();
    }

    private static SummaryTreeNode scrape(URL baseUrl, int maxDepth) throws IOException {
        return scrape(baseUrl, maxDepth, null, null);
    }

    private static SummaryTreeNode scrape(URL baseUrl, int maxDepth, SummaryTreeNode parent, SummaryTreeNode root) {
        if (!baseUrl.getProtocol().toLowerCase().contains("http")) {
            return new SummaryTreeNode(baseUrl, "NON-HTTP", baseUrl.toString(), parent);
        }

        Document d = null;
        try {
            d = Jsoup.parse(baseUrl, 10000);
        } catch (IOException e) {
            System.out.println("Error in " + baseUrl + " = " + e);
            return new SummaryTreeNode(baseUrl, "ERROR", "GOT ERROR.", parent);
        }

        System.out.println(String.format("baseUrl=%s title='%s' charset='%s'", baseUrl.toString(), d.title(), d.charset()));


        SummaryTreeNode currentNode = new SummaryTreeNode(baseUrl, "ROOT-PAGE", getHash(d.toString().getBytes()), parent);

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

    private static String getHash(byte[] d) {
        getMessageDigest().update(d);
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
