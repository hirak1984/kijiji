package pvt.hrk.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "channel", propOrder = { "title", "pubDate", "date", "item" })
public class Channel {
	@XmlElement(required = true)
	protected String title;
	/*@XmlElement(required = true)
	protected String link;
	@XmlElement(required = true)
	protected String description;
	@XmlElement(required = true)
	protected String language;
	@XmlElement(required = true)
	protected String copyright;
	@XmlElement(required = true)*/
	protected String pubDate;
	@XmlElement(required = true)
	@XmlSchemaType(name = "dc:date")
	protected String date;
	/*@XmlElement(required = true)
	protected String rights;
	@XmlElement(required = true)
	protected Image image;*/
	@XmlElement(required = true)
	protected List<Item> item;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/*public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}*/
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
/*	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}*/
	public List<Item> getItem() {
		return item;
	}
	public void setItem(List<Item> item) {
		this.item = item;
	}
	
	

}
