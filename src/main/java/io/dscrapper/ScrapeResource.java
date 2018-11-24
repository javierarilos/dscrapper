package io.dscrapper;

import com.codahale.metrics.annotation.Timed;
import io.dscrapper.core.Scrapper;
import io.dscrapper.model.Saying;
import io.dscrapper.model.SummaryTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Path("/scrape")
@Produces(MediaType.APPLICATION_JSON)
public class ScrapeResource {
    private static final Logger logger = LoggerFactory.getLogger(ScrapeResource.class);
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;


    public ScrapeResource(String template, String defaultName) {

        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }

    @GET
    @Path("/sequential/{url}")
    @Timed
    public SummaryTreeNode getSequential(@PathParam("url") String encodedURL) throws MalformedURLException, UnsupportedEncodingException {
        logger.info("Scrape REQ: {}", encodedURL);

        String urlString = URLDecoder.decode(encodedURL, "UTF-8");
        SummaryTreeNode summary = Scrapper.getPageWithImagesTimed(new URL(urlString));

        logger.info("Scrapped DONE: {} - {} children={}", summary.getUrl(), summary.getTag(), summary.getChildren().size());
        return summary;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/urlencode")
    public HashMap<String, String> encode(HashMap<String, String> encodeRequest) throws UnsupportedEncodingException {
        if (!encodeRequest.containsKey("string")) {
            throw new ClientErrorException("Parameter string is mandatory", 400);
        }

        encodeRequest.put("encoded", URLEncoder.encode(encodeRequest.get("string"), "UTF-8"));
        return encodeRequest;

    }
}
