import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;

/**
 * WebpageCrawlerRunnable retrieves a webpage  and searches it for a specific search term 
 *
 * @author Melissa Aitkin
 */
public class WebpageCrawler {

	/**
	 * Main
	 *
	 * @param args Arguments to run the class, one argument is required, a search string
	 */
	public static void main(String[] args) {
		String search = "";

		if (args.length > 0) {
			search = args[0];
		} else {
			System.out.println("You need to specify a search term");
			System.exit(0);
		}
		
		try {
			FileWriter fstream = new FileWriter("results.txt", true);
			BufferedWriter bf = new BufferedWriter(fstream);
			bf.write("Webpage Crawler\n");
			bf.write("\nSearching for term: " + search + "\n");

			// Get webpages to search
			ArrayList<String> sites = getWebSites();

			// Process webpages in batches of 20
			for (int i = 0; i < sites.size(); i+=20) {
				processBatch(i, sites, search, bf);
			}
			
			if (bf != null) {
				try {
					bf.close();
				} catch(Exception e) {
					// do nothing
				}
			}
		} catch (IOException ioe) {
			//System.out.println("IO Error:" + ioe.getMessage());
		}

	}

	/**
	 * Create a batch of 20 threads to process the webpages 
	 *
	 * @param offset Place in site list to continue processing from
	 * @param sites List of webpage sites
	 * @param search Term to search for
	 */
	public static void processBatch(int offset, ArrayList<String> sites, String search, BufferedWriter bf) {
		// Create thread list to keep track of active status of threads
		List<Thread> threads = new ArrayList<Thread>();

		// Kick off 20 threads to process webpages
		for (int i= offset; i < (offset + 20); i++) {
			Runnable task = new WebpageCrawlerRunnable(sites.get(i), search, bf);
			Thread worker = new Thread(task);
			worker.setName(String.valueOf(i));
			worker.start();
			threads.add(worker);
		}

		// Check to see if threads are running, return when all complete
		int running = 0;
		do {
			running = 0;
			for (Thread thread : threads) {
				if (thread.isAlive()) {
					running++;
				}
			}
		} while (running > 0);

	}

	/**
	 * Get list of webpages to search for text file 
	 *
	 * @return sites List of webpages
	 */
	public static ArrayList<String> getWebSites() {
		ArrayList<String> sites = new ArrayList<String>();
		try {
			String urlString = "https://s3.amazonaws.com/fieldlens-public/urls.txt";
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			boolean header = true;
			while((line = br.readLine()) != null) {
				if (!header) {
					String[] parts = line.split(",");
					if (parts.length > 1) {
						String site = parts[1];
						sites.add(site.replace("\"", ""));
					}
				}
				header = false;
			} 
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sites;
	}

}