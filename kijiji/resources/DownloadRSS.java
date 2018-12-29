package pvt.hrk;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import pvt.hrk.database.GenericDAO;
import pvt.hrk.database.PersistenceManager;
import pvt.hrk.entity.Item;
import pvt.hrk.entity.RSSMetaData;
import pvt.hrk.utils.ConvertUtils;
import pvt.hrk.xml.Rss;

public class DownloadRSS {

	private static final List<String> RSS_LIST = Arrays.asList(
			"http://www.kijiji.ca/rss-srp-buy-sell/gta-greater-toronto-area/c10l1700272?price=0__200&gpTopAds=y");

	public static void main(String[] args) throws JAXBException {
		
		RSS_LIST.forEach(RSS -> {

			try {
				HttpURLConnection.setFollowRedirects(false);
				HttpURLConnection con = (HttpURLConnection) new URL(RSS).openConnection();
				con.setRequestProperty("If-Modified-Since", "Sat, 15 Oct 2016 03:44:06 GMT");
				con.setRequestMethod("GET");
				

				if (con.getResponseCode() == 200) {
					printHeaders(con.getHeaderFields());
					if (!alreadyExists(RSS,con.getHeaderField("Date"))) {
						String hashedChannel = saveMetadata(RSS, con.getHeaderField("Date"));
						InputStream xml = con.getInputStream();
						Rss rss = parseContent(xml);
						xml.close();
						if(rss!=null){
							saveContents(hashedChannel,rss);
						}
					}
				} else {
					throw new RuntimeException("Didn't get proper response:" + con.getResponseCode() + ", Message:"
							+ con.getResponseMessage());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		PersistenceManager.INSTANCE.close();
	}

	private static void printHeaders(Map<String, List<String>> headerFields) {
		headerFields.forEach((key, value) -> {
			System.out.println(String.format("Key=%s, Value=%s", key, value));
		});
	}

	private static String saveMetadata(String channel,String lastPublishDate) throws NoSuchAlgorithmException{
		GenericDAO<RSSMetaData, Serializable> dao = new GenericDAO<>(RSSMetaData.class);
		RSSMetaData metadata = new RSSMetaData();
		metadata.setChannel(getHash(channel));
		metadata.setDate(lastPublishDate);
		dao.save(metadata);
		return metadata.getChannel();
	}
	private static void saveContents(String channel,Rss rss) throws NoSuchAlgorithmException{
		GenericDAO<Item, Serializable> dao = new GenericDAO<>(Item.class);
		List<Item> item = ConvertUtils.convert(channel, rss.getChannel().getItem());
		dao.saveAll(item);
		
		
	}
	private static boolean alreadyExists(String channel,String lastPublishedDate) throws NoSuchAlgorithmException {
		if (lastPublishedDate == null) {
			throw new RuntimeException("LastPublishDate cannot be null. Please check headers");
		}
		GenericDAO<RSSMetaData, Serializable> dao = new GenericDAO<>(RSSMetaData.class);
		RSSMetaData metaData = new RSSMetaData();
		metaData.setChannel(getHash(channel));
		metaData.setDate(lastPublishedDate);
		List<RSSMetaData> list = dao.findAll();

		return list!=null &&list.contains(metaData);
	}

	private static Rss parseContent(InputStream xml) throws JAXBException, XMLStreamException, FactoryConfigurationError {
		JAXBContext context = JAXBContext.newInstance(Rss.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(xml);
		XMLReaderWithoutNamespace xr = new XMLReaderWithoutNamespace(xsr);
		Rss rss = (Rss) unmarshaller.unmarshal(xr);
		return rss;
	}

	private static class XMLReaderWithoutNamespace extends StreamReaderDelegate {
		public XMLReaderWithoutNamespace(XMLStreamReader reader) {
			super(reader);
		}

		@Override
		public String getAttributeNamespace(int arg0) {
			return "";
		}

		@Override
		public String getNamespaceURI() {
			return "";
		}

	}

	private static String getHash(String str) throws NoSuchAlgorithmException {
		return new String(org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes()));
	}
}
