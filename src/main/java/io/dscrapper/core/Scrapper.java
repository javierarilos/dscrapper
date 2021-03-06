package io.dscrapper.core;

import com.codahale.metrics.Timer;
import io.dscrapper.health.Metrics;
import io.dscrapper.model.SummaryTreeNode;
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
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Scrapper {
    private static MessageDigest messageDigest;
    private static final Timer pagePlusImgsTimer = Metrics.timer("page.plus.images");
    private static final Timer imgTimer = Metrics.timer("image");

    public static SummaryTreeNode getPageWithImagesTimed(URL baseUrl) {
        Timer.Context pagePlusImgsTimerCtx = pagePlusImgsTimer.time();
        try {
            if (!baseUrl.getProtocol().toLowerCase().contains("http")) {
                return new SummaryTreeNode(baseUrl, "NON-HTTP", baseUrl.toString());
            }

            Document d;
            try {
                d = Jsoup.parse(baseUrl, 10000);
            } catch (IOException e) {
                System.out.println("Error in " + baseUrl + " = " + e);
                return new SummaryTreeNode(baseUrl, "ERROR", "GOT ERROR.");
            }

            SummaryTreeNode currentNode = new SummaryTreeNode(baseUrl, "ROOT-PAGE", getHash(d.toString().getBytes()));

            Elements images = d.getElementsByTag("img");
            for (Element img : images) {
                String imgUrlStr = img.absUrl("src");
                Timer.Context imgTimerCtx = imgTimer.time();
                try {
                    URL imgUrl = new URL(imgUrlStr);
                    byte[] imgBytes = getImgHttpClient(imgUrl);

                    currentNode.addChild(
                            new SummaryTreeNode(imgUrl, "IMG", getHash(imgBytes))
                    );

                } catch (IOException e) {
                    System.out.println(">>>> GOT ERROR: getting "+img.absUrl("src")+" -> " + e);
                } finally {
                    imgTimerCtx.stop();
                }
            }
            return currentNode;
        } finally {
            pagePlusImgsTimerCtx.stop();

        }

    }

    private static byte[] getImgHttpClient(URL imgUrl) throws IOException {

        CloseableHttpClient httpclient = HttpClients.custom().disableCookieManagement().build();
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
