import java.io.*;
import java.net.*;

/**
 * WebpageCrawlerRunnable retrieves a webpage as a thread and searches it for a specific search term 
 *
 * @author Melissa Aitkin
 */
public class WebpageCrawlerRunnable implements Runnable {
	private String site;
	private String search;
	private BufferedWriter bf;

	/**
	 * Constructor 
	 *
	 * @param site Webpage to search
	 * @param search Term to search for
	 */
	WebpageCrawlerRunnable(String site, String search, BufferedWriter bf) {
		this.site = site;
		this.search = search;
		this.bf = bf;
	}

	/**
	 * Run thread 
	 */
	public void run() {
		try {
			System.out.println(this.site);

			String protocol = "http://";
			String protocol_secured = "https://";

			URL url = new URL(protocol + this.site);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.connect();
			int code = connection.getResponseCode();

			if (code == 301 || code == 302) {
				url = new URL(protocol_secured + this.site);
				connection = (HttpURLConnection)url.openConnection();
				connection.connect();
				code = connection.getResponseCode();
			}

			if (code == 200) {
				InputStream is = connection.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				String found = "NOT FOUND";
				while((line = br.readLine()) != null) {
					if ( line.contains(this.search) ) {
						found = "FOUND";
						break;
					}
				}  
				this.bf.write("\n" + this.site + ": " + found);
			}
		} catch (Exception e) {
			try {
				this.bf.write("\n" + this.site + ": Error retrieving page");
			} catch (IOException ioe) {
				// subsume
			}
		}
	}

}