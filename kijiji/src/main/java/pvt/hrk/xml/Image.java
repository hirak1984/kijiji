package pvt.hrk.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "image", propOrder = {
    "title",
    "url",
    "link"
})
public class Image {

	  @XmlElement(required = true)
      protected String title;
      @XmlElement(required = true)
      protected String url;
      @XmlElement(required = true)
      protected String link;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	@Override
	public String toString() {
		return "Image [title=" + title + ", url=" + url + ", link=" + link + "]";
	}
      
}
