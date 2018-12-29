package pvt.hrk.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "item", propOrder = { "title", "description", "pubDate", "date", "lat", "_long", "price" })
public class Item {
	@XmlElement(required = true)
	private String title;
	/*
	 * @XmlElement(required = true) private String link;
	 */
	@XmlElement(required = true)
	private String description;
	/*
	 * @XmlElement(required = true) private List<Enclosure> enclosure;
	 */
	@XmlElement(required = true)
	@XmlSchemaType(name = "pubDate")
	private String pubDate;
	/*
	 * @XmlElement(required = true) private String guid;
	 */
	@XmlElement(required = true)
	@XmlSchemaType(name = "date")
	private String date;
	private double lat;
	@XmlElement(name = "long")
	private double _long;
	@XmlElement(name = "price")
	private double price;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/*
	 * public String getLink() { return link; } public void setLink(String link)
	 * { this.link = link; }
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * public List<Enclosure> getEnclosure() { return enclosure; } public void
	 * setEnclosure(List<Enclosure> enclosure) { this.enclosure = enclosure; }
	 */
	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	/*
	 * public String getGuid() { return guid; } public void setGuid(String guid)
	 * { this.guid = guid; }
	 */
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double get_long() {
		return _long;
	}

	public void set_long(double _long) {
		this._long = _long;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
