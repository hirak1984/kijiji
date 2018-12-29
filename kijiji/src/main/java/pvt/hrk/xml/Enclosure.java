package pvt.hrk.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "enclosure")
public class Enclosure {
	 @XmlAttribute(name = "url")
     protected String url;
     @XmlAttribute(name = "type")
     protected String type;
     
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Enclosure [url=" + url + ", type=" + type + "]";
	}
     
}
