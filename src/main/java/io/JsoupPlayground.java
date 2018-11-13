package io;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class JsoupPlayground {
    public static void main(String[] args) throws IOException {
        Document d = Jsoup.parse(new URL("http://www.eldiario.es"), 10000);

        System.out.println(String.format("title='%s' charset='%s'", d.title(), d.charset()));

        Elements links = d.select("a[href]"); // select links and imgs
        Elements imgs = d.select("img");

    }
}
