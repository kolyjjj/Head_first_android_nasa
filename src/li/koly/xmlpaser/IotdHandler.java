package li.koly.xmlpaser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class IotdHandler extends DefaultHandler {
	private String url = "http://www.nasa.gov/rss/image_of_the_day.rss";
	private boolean inTitle = false;
	private boolean inDescription = false;
	private boolean inItem = false;
	private boolean inDate = false;
	private Bitmap image = null;
	private String title = null;
	private StringBuffer description = new StringBuffer();
	private String date = null;
	private String imageUrl = null;

	public void processFeed() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(this);

			// URL testUrl = new URL("http://www.baidu.com");
			// HttpURLConnection urlConnection = (HttpURLConnection)
			// testUrl.openConnection();
			//
			// System.out.println("the response code is: " +
			// urlConnection.getResponseCode());
			InputStream inputStream = new URL(url).openStream();
			reader.parse(new InputSource(inputStream));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Bitmap getBitmap(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap bilde = BitmapFactory.decodeStream(input);
			input.close();
			return bilde;
		} catch (IOException ioe) {
			return null;
		}
	}

	public void startElement(String url, String localName, String qName,
			Attributes attributes) throws SAXException {
		System.out.println("-----------The localName is: " + localName);
		if (localName.equals("enclosure")) {
			imageUrl = attributes.getValue("url");
		}

		if (localName.startsWith("item")) {
			inItem = true;
		} else if (inItem) {

			if (localName.equals("title")) {
				inTitle = true;
			} else {
				inTitle = false;
			}

			if (localName.equals("description")) {
				inDescription = true;
			} else {
				inDescription = false;
			}

			if (localName.equals("pubDate")) {
				inDate = true;
			} else {
				inDate = false;
			}
		}
	}

	public void characters(char ch[], int start, int length) {
		String chars = new String(ch).substring(start, start + length);
		System.out.println("+++++++++++" + chars);
		if (imageUrl != null) {
			image = getBitmap(imageUrl);
		}
		if (inTitle && title == null) {
			title = chars;
		}
		if (inDescription) {
			description.append(chars);
		}
		if (inDate && date == null) {
			date = chars;
		}

	}

	public Bitmap getImage() {
		return image;
	}

	public String getTitle() {
		return title;
	}

	public StringBuffer getDescription() {
		return description;
	}

	public String getDate() {
		return date;
	}

}
