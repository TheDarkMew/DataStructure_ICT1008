import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class Twitter {
    private Document doc = null;

    public Twitter(String query) throws IOException {
        this.doc = doc = Jsoup.connect("https://twitter.com/search?q=" + query).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
    }

    public void crawlTwitter(){

    }

    public void crawlComments(){

    }
}
