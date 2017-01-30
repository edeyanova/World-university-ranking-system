import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.management.openmbean.OpenDataException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.omg.CORBA.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

public class Bot {

	private static String getPageNumber(Page page) {
		HtmlElement currentButton = ((HtmlPage) page).getFirstByXPath(
				"//span/a[contains(@class, 'paginate_button current') and @aria-controls='qs-rankings']");
		String pageNumber = currentButton.asText();

		return pageNumber;
	}

	private static List<HtmlAnchor> getPaginateButtons(Page page) {
		@SuppressWarnings("unchecked")
		List<HtmlAnchor> paginateButtons = (List<HtmlAnchor>) ((HtmlPage) page)
				.getByXPath("//span/a[contains(@class, 'paginate_button') and @aria-controls='qs-rankings']");

		return paginateButtons;
	}

	public static List<String> getUniversities(Page page) throws InterruptedException, IOException {
		HashSet<String> visitedPages = new HashSet<>();
		String pageNumber = getPageNumber(page);
		visitedPages.add(pageNumber);
		List<String> universities = getUniversitiesOfPage(page, visitedPages);

		return universities;
	}


	private static List<String> getUniversitiesOfPage(Page page, HashSet<String> visitedPages)
			throws InterruptedException, IOException {
		Thread.sleep(10000);

		final HtmlTable table = (HtmlTable) ((HtmlPage) page).getHtmlElementById("qs-rankings");
		List<HtmlElement> ranks = (List<HtmlElement>);
		table.getByXPath("tbody//span[contains(@class, 'rank')]");
		List<HtmlElement> universityElements = table.getElementsByAttribute("td", "class", " uni");
		List<String> universityNames = new ArrayList<>();
		for (HtmlElement universityElement : universityElements) {
			String university = universityElement.getElementsByTagName("a").get(0).asText();
			universityNames.add(university);
		}

		List<HtmlElement> ratingElements = table.getElementsByAttribute("td", "class", " rating");
		List<String> ratings = new ArrayList<>();
		for (HtmlElement ratingElement : ratingElements) {
			String rating = ratingElement.getTextContent();
			ratings.add(rating);
		}

		List<String> universities = new ArrayList<>();
		for (int i = 0; i < ranks.size(); i++) {
			String university = ranks.get(i).asText() + " " + universityNames.get(i) + " " + ratings.get(i);
			universities.add(university);
		}

		List<HtmlAnchor> paginateButtons = getPaginateButtons(page);
		for (HtmlAnchor paginateButton : paginateButtons) {
			String paginateButtonNumber = paginateButton.asText();
			if (!visitedPages.contains(paginateButtonNumber)) {
				visitedPages.add(paginateButton.asText());
				Page newPage = paginateButton.click();
				universities.addAll(getUniversitiesOfPage(newPage, visitedPages));
			}
		}
		return universities;
	}
}
