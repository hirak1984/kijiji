package pvt.hrk;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.HashedMap;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pvt.hrk.config.URLS;
import pvt.hrk.database.GenericDAO;
import pvt.hrk.database.PersistenceManager;
import pvt.hrk.entity.AdItem;
import pvt.hrk.entity.Category;
import pvt.hrk.utils.ConvertUtils;
import pvt.hrk.utils.Utils;

public class LatestKijijiDownloader {
	static Logger LOGGER = LoggerFactory.getLogger(LatestKijijiDownloader.class);
	public static WebDriver BROWSER = new HtmlUnitDriver();
	// private static List<String> IGNORE_CATEGORIES =
	// Arrays.asList("AllCategories", "buyandsell");
	public static final String PATTERN = ".*?(\\(.*\\))";
	public static final Pattern p = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	public static final long TIME_IN_SECONDS = 5;

	public static void main(String[] args) throws IOException {
		long start = System.nanoTime();
		URLS.CITY_OF_TORONTO.GET_INITIAL_URLS().forEach(INITIAL_URL -> {
			List<Category> CATEGORIES = DownloadCategoryDetailsFromKijiji(INITIAL_URL);
			LOGGER.info("Found {} CATEGORIES", CATEGORIES.size());
			
			Map<String, List<Category>> subCategoryMap = new HashMap<>();
			CATEGORIES.forEach(CATEGORY -> {
				WAIT_IN_SECONDS(TIME_IN_SECONDS);
				List<Category> SUB_CATEGORIES = DownloadCategoryDetailsFromKijiji(
						URLS.ADD_DOMAIN_TO_URL(CATEGORY.getUrl()));
				LOGGER.info("Found total {} SUB_CATEGORIES for Category:{}", SUB_CATEGORIES.size(),CATEGORY.getRealName());
				subCategoryMap.put(CATEGORY.getRealName(), SUB_CATEGORIES);
			});
			int size = subCategoryMap.keySet().size();
			int[] categoryIndex = {1};
			subCategoryMap.forEach((CategoryName,SUB_CATEGORIES)->{
				LOGGER.warn("Now processing category no {}/{}",categoryIndex[0]++,size);
				int totalSize = SUB_CATEGORIES.size();
				int[] idx = { 1 };
				SUB_CATEGORIES.forEach(SUB_CATEGORY -> {
					LOGGER.warn("Now processing subcategory {}, which is {}/{} for category {} ",SUB_CATEGORY.getRealName(),idx[0]++,totalSize,CategoryName);
					WAIT_IN_SECONDS(TIME_IN_SECONDS);
					List<String> URLS_TO_DOWNLOAD = new ArrayList<>();
					String url = URLS.ADD_DOMAIN_TO_URL(SUB_CATEGORY.getUrl());
					URLS_TO_DOWNLOAD.add(url);
					URLS_TO_DOWNLOAD.addAll(URLS.CITY_OF_TORONTO.paginateThisURL(url));
					URLS_TO_DOWNLOAD.forEach(URL_TO_DOWNLOAD -> {
						LOGGER.info("Now Browsing:");
						LOGGER.warn(URL_TO_DOWNLOAD);
						downloadAdItems(URL_TO_DOWNLOAD, CategoryName, SUB_CATEGORY.getRealName());
					});
				});
			});
			
			
		});
		LOGGER.warn("Total process completed in {} seconds:", TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start));

		releaseResources();
	}

	private static void downloadAdItems(String URL_TO_DOWNLOAD, String categoryName, String subCategoryName) {
		WAIT_IN_SECONDS(1);
		long start = System.nanoTime();

		BROWSER.get(URL_TO_DOWNLOAD);
		List<WebElement> ADS = BROWSER.findElements(By.className("info-container"));
		if (ADS == null || ADS.isEmpty()) {
			return;
		}
		GenericDAO<AdItem, Serializable> dao = new GenericDAO<>(AdItem.class);
		List<AdItem> itemsToSave = new ArrayList<>();
		ADS.forEach(AD -> {
			WebElement PRICE = AD.findElement(By.className("price"));
			WebElement TITLE = AD.findElement(By.className("title"));
			WebElement LOCATION = AD.findElement(By.className("location"));
			List<WebElement> DATE_POSTED = LOCATION.findElements(By.className("date-posted"));
			if (DATE_POSTED == null || DATE_POSTED.isEmpty()) {
				return;
			}
			WebElement DESCRIPTION = AD.findElement(By.className("description"));
			
			AdItem item = new AdItem(categoryName, subCategoryName, getContent(PRICE), getContent(TITLE),
					getContent(LOCATION), getContent(DATE_POSTED.get(0)), getContent(DESCRIPTION));
			AdItem savedItem = dao.findById(item.getTitle().hashCode() + Utils.GET_EPOCH_DATE());

			if (savedItem != null) {
				LOGGER.info("Already exists,skipping");
				LOGGER.info("{}", item);
				return;
			}
			
			//itemsToSave.add(item);
			dao.save(item);
		});
		if(!itemsToSave.isEmpty()){
		LOGGER.info("Now saving {} items in database",itemsToSave.size());
		dao.saveAll(itemsToSave);
		}
		LOGGER.info("Download completed in {} seconds:", TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start));

	}

	private static final String getContent(WebElement element) {
		String text = Utils.format_preserve_space(element.getAttribute("textContent"));
		return text;
	}

	private static List<Category> DownloadCategoryDetailsFromKijiji(String URL) {
		List<Category> categories = new ArrayList<>();
		BROWSER.get(URL);
		WebElement CATEGORY = BROWSER.findElement(By.className("content"));
		WebElement UL = CATEGORY.findElements(By.tagName("ul")).get(0);
		List<WebElement> LIs = UL.findElements(By.tagName("li"));
		LIs.forEach(LI -> {
			String cssClass = LI.getAttribute("class");
			if(cssClass!=null && cssClass.equalsIgnoreCase("highlight")){
				return;
			}
			Category category = populateCategory(LI);
			if (category == null) {
				return;
			}
			categories.add(category);
		});

		return categories;
	}

	public static Category populateCategory(WebElement LI) {
		String textContent = Utils.format(LI.getAttribute("textContent"));
		List<WebElement> ANCHORS = LI.findElements(By.tagName("A"));

		if (textContent != null && ANCHORS != null) {
			String url = ANCHORS.get(0).getAttribute("href");
			final String text = Utils.format(textContent);
			if (!URLS.shouldIgnore(url)) {
				Matcher m = p.matcher(text);
				if (m.find()) {
					String braces = m.group(1);
					String formattedText = text.replaceAll(braces, "");
					Category categoryObj = new Category();
					categoryObj.setRealName(text);
					categoryObj.setCategory(Utils.removeBraces(formattedText));
					categoryObj.setCount(ConvertUtils.convert(braces));
					categoryObj.setDatetime(Calendar.getInstance().getTime());
					categoryObj.setUrl(url);
					return categoryObj;
				}
			}
		}
		return null;
	}

	public static void releaseResources() {
		BROWSER.quit();
		PersistenceManager.INSTANCE.close();
	}

	public static void WAIT_IN_SECONDS(long seconds) {
		try {
			LOGGER.info("Waiting for {} seconds...", seconds);
			Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}
