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

	/**
	 * Constructor 
	 *
	 * @param site Webpage to search
	 * @param search Term to search for
	 */
	WebpageCrawlerRunnable(String site, String search) {
		this.site = site;
		this.search = search;
	}

	/**
	 * Run thread 
	 */
	public void run() {
		BufferedWriter results = null;
		try {
			System.out.println(this.site);
			FileWriter fstream = new FileWriter("results.txt", true);
			results = new BufferedWriter(fstream);

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
				boolean header = true;
				while((line = br.readLine()) != null) {
					if ( line.contains(this.search) ) {
						if (header) {
							results.write("\n" + site);
							header = false;
						}
						results.write("\n" + line);
					}
				}            
			}
		} catch (IOException ioe) {
			//System.out.println("IO Error:" + ioe.getMessage());
		} catch(Exception e) {
			//System.out.println("Error: " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch(Exception e) {
					// do nothing
				}
			}
		}
	}

}