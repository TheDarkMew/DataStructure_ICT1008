import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Scanner;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter which site you want to crawl: ");
        Scanner input = new Scanner(System.in);
        String socialMedia = input.nextLine();
        System.out.println("Enter query: ");
        String query = input.nextLine();

        if (socialMedia.matches("twitter")) {
            Twitter searchTwitter = new Twitter(query);
        } else if (socialMedia.matches("reddit")) {
            Reddit searchReddit = new Reddit(query);
            searchReddit.crawlReddit();
        }
    }
}
