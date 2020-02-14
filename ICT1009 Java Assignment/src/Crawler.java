abstract class Crawler {
	protected static String[] searchTerms = {"kobe bryant"};
	public abstract void crawlMedia();
	
	public static String[] getSearchTerms() {
		return searchTerms;
	}
}
