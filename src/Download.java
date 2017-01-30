import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Download {
	public static void download(String fileName)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
				BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getCookieManager().setCookiesEnabled(true);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setAppletEnabled(false);

			webClient.setAjaxController(new AjaxController() {
				@Override
				public boolean processSynchron(HtmlPage page, WebRequest request, boolean async) {
					return true;
				}
			});

			webClient.getOptions().setTimeout(60000);
			webClient.getOptions().setRedirectEnabled(true);

			java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
			String path = "http://www.topuniversities.com/university-rankings/world-university-rankings/2016";

			long start = System.currentTimeMillis();
			final HtmlPage page = webClient.getPage(path);
			Thread.sleep(10000);
			long end = System.currentTimeMillis();

			System.out.println((end - start) / 1000.0);

			List<String> universities = Bot.getUniversities(page);

			for (String university : universities) {
				writer.write(university);
				writer.newLine();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
