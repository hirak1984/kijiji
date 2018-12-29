package pvt.hrk.entity;

import java.util.Date;

import pvt.hrk.utils.ConvertUtils;

public class Category {
	private Long id;

	private Date datetime;
	private String realName;
	private String category;
	private int count;
	private String type;
	private String url;

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Override
	public String toString() {
		return String.format("Category [type= %s, datetime= %s, category=%s, count=%5d]", type,
				ConvertUtils.DATE_FORMAT.format(datetime), category, count);
	}

}
