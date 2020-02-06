import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Reddit {
    private Document doc = null;

    public Reddit(String query) throws IOException {
        this.doc = doc = Jsoup.connect("https://old.reddit.com/search/?q=" + query).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
    }

    public void crawlReddit() throws IOException {
        System.out.println("==========CRAWLING REDDIT SEARCH==========");
        Elements searchResults = doc.select("div.search-result-link header.search-result-header > a[href], div.search-result-link div.search-result-meta > span,  span.search-time > time[title]").not("span.search-result-icon"); //looks for links under the search results for links' header.
        if(searchResults.isEmpty()){
            System.out.println("FUCK");
        }
        int i = 1; //index of posts
        String url = "";
        for (Element result : searchResults) {
            //title, (blank?), points, exact post timestamp, OP, subreddit
            if (result.tagName().equals("a")){
                if (i != 1 || i == searchResults.size()) {
                    crawlComments(url);
                }
                System.out.println("\n==================================POST=======================================================");
                System.out.println("Post Title: " + result.text());
                url = result.attr("abs:href");
            }
            else if (result.tagName().equals("time")){
                System.out.println("Posted on: " + result.attr("title"));
            }
            else{
                System.out.println(result.text());
            }
            i++;
        }
    }

    public void crawlComments(String url) throws IOException {
        System.out.println("==========CRAWLING COMMENTS==========");
        Document commentsPage = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
        Elements comments = commentsPage.select("div.commentarea p.tagline > a.author, p.tagline > span.author, p.tagline > span.unvoted, div.commentarea p.tagline > time[title], div.thing div.usertext-body div.md > p");
        for (Element comment : comments){
            //every 2 elements: commenter username, comment
            if (comment.tagName().equals("a")) {
                System.out.println("\nComment by: " + comment.text());
            }
            else if (comment.tagName().equals("time")){
                System.out.println("Posted: " + comment.attr("title"));
            }
            else if (comment.tagName().equals("span")){
                if (comment.attr("class").equals("score unvoted")){
                    System.out.println("Score: "+ comment.text());
                }
                else{
                    System.out.println("\nDeleted Comment: " +comment.text());
                }
            }
            else{
                System.out.println(comment.text());
            }
        }
    }
}
