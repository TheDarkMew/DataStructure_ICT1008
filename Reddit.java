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
        //Document parsedDoc = doc.parse()
        Elements searchResults = doc.select("div.search-result-link header.search-result-header > a[href], div.search-result-link div.search-result-meta > span"); //looks for links under the search results for links' header.
        if(searchResults.isEmpty()){
            System.out.println("FUCK");
        }
        int i = 1; //index of posts
        String url = "";https://old.reddit.com/search/?q=
        for (Element result : searchResults) {
            //every 4 elements: title, comments, author, subreddit
            if (result.tagName().equals("a")){

                if (i == 1){
                    System.out.println("text: " + result.text());
                    url = result.attr("abs:href");
                }
                //connects to comments page and prints all comments
                else{
                    crawlComments(url);
                    System.out.println("text: " + result.text());
                    url = result.attr("abs:href");
                }
            }
            else{
                System.out.println("text: " + result.text());
            }
            i++;
        }
    }

    public void crawlComments(String url) throws IOException {
        System.out.println("==========CRAWLING COMMENTS==========");
        Document commentsPage = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
        Elements comments = commentsPage.select("p.tagline a.author, div.thing div.usertext-body div.md > p");
        for (Element comment : comments){
            //every 2 elements: commenter username, comment
            if (comment.tagName().equals("a"))
                System.out.println("\nComment by: " + comment.text());
            else{
                System.out.println(comment.text());
            }
        }
    }
}
