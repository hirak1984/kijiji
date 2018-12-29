package pvt.hrk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import pvt.hrk.config.URLS;
import pvt.hrk.database.GenericDAO;
import pvt.hrk.database.PersistenceManager;
import pvt.hrk.entity.AdItem;
import pvt.hrk.entity.Category;
import pvt.hrk.utils.ConvertUtils;
import pvt.hrk.utils.Utils;

public class App {
	public static final String PATTERN = ".*?(\\(.*\\))";
	public static final Pattern p = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	public static final long WAIT_INTERVAL = 20;
	
	private static final List<String> IGNORE_CATEGORIES = Arrays.asList("AllCategories", "buyandsell");
	

	public static WebDriver BROWSER = new HtmlUnitDriver();



	public static void main(String[] args) {

		System.out.println("STARTING APP");
		long appStart = System.nanoTime();
		long start = System.nanoTime();
		System.out.println("DOWNLOADING CATEGORIES...");
		List<Category> categories = DownloadCategoryDetailsFromKijiji(URLS.SOURCE_INITIAL_URLS_ALL());
		GenericDAO<Category, Integer> categoryDao = new GenericDAO<>(Category.class);
		categoryDao.saveAll(categories);
		System.out
				.println("Category download finished in :" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
		start = System.nanoTime();
		System.out.println("DOWNLOADING AD ITEMS...");
		DownloadAdItems(categories);
		System.out.println("Item download finished in :" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));

		releaseResources();

		System.out.println("Processing finished in :" + TimeUnit.NANOSECONDS.toSeconds( System.nanoTime() - appStart));
	}

	private static void DownloadAdItems(List<Category> categories) {

		if (categories != null && !categories.isEmpty()) {
			categories.forEach(category -> {
				if (category.getUrl() == null) {
					return;
				}
			
				
				List<String> PAGINATED_URLS = URLS.PAGINATE_THIS_URL(category.getUrl());
				if(PAGINATED_URLS==null|| PAGINATED_URLS.isEmpty()){
					throw new RuntimeException("Something wrong");
				}
				
				PAGINATED_URLS.forEach(URL->{
					WAIT(WAIT_INTERVAL);
					System.out.println("Now Browsing:");
					System.out.println(URL);
					BROWSER.get(URL);
					List<WebElement> ADS = BROWSER.findElements(By.className("info-container"));
					if (ADS == null || ADS.isEmpty()) {
						return;
					}
					ADS.forEach(AD -> {
						WebElement PRICE = AD.findElement(By.className("price"));
						WebElement TITLE = AD.findElement(By.className("title"));
						WebElement LOCATION = AD.findElement(By.className("location"));
						List<WebElement> DATE_POSTED = LOCATION.findElements(By.className("date-posted"));
						if (DATE_POSTED == null || DATE_POSTED.isEmpty()) {
							// This means it is a sponsored link
							return;
						}
						WebElement DESCRIPTION = AD.findElement(By.className("description"));
						AdItem item = new AdItem(category.getId(), getContent(PRICE), getContent(TITLE),
								getContent(LOCATION), getContent(DATE_POSTED.get(0)), getContent(DESCRIPTION));
						GenericDAO<AdItem, Serializable> dao = new GenericDAO<>(AdItem.class);
						if (dao.findById(item.getTitle().hashCode()) != null) {
							// System.out.println("Already exists,skipping");
							// System.out.println(item);
							return;
						}
						dao.save(item);
					});
				});
				

			});
		}
	}

	public static List<Category> DownloadCategoryDetailsFromKijiji(List<String> URLS) {
		List<Category> categories = new ArrayList<>();
		URLS.forEach(URL -> {
			BROWSER.get(URL);
			WebElement CATEGORY = BROWSER.findElement(By.className("content"));
			WebElement UL = CATEGORY.findElements(By.tagName("ul")).get(0);
			List<WebElement> LIs = UL.findElements(By.tagName("li"));
			LIs.forEach(LI -> {
				Category category = populateCategory(LI, URL.contains("wanted") ? "WANTED" : "OFFERING",
						IGNORE_CATEGORIES);
				if (category == null) {
					// throw new RuntimeException("Category null. Something
					// wrong with web parsing");
					return;
				}
				categories.add(category);
			});

		});

		return categories;
	}

	public static void WAIT(long seconds) {
		try {
			// Waiting 10 seconds before each webscrapping to stay under the
			// radar of Kijiji
			Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	public static String getContent(WebElement element) {
		String text = Utils.format_preserve_space(element.getAttribute("textContent"));
		return text;
	}

	public static Category populateCategory(WebElement LI, String urlName, List<String> IGNORE_CATEGORIES) {
		String textContent = Utils.format(LI.getAttribute("textContent"));
		List<WebElement> ANCHORS = LI.findElements(By.tagName("A"));

		if (textContent != null && ANCHORS != null) {
			final String text = Utils.format(textContent);
			if (IGNORE_CATEGORIES.stream().noneMatch(e -> text.toLowerCase().contains(e.toLowerCase()))) {
				Matcher m = p.matcher(text);
				if (m.find()) {
					String braces = m.group(1);
					String formattedText = text.replaceAll(braces, "");
					Category categoryObj = new Category();
					categoryObj.setCategory(Utils.removeBraces(formattedText));
					categoryObj.setCount(ConvertUtils.convert(braces));
					categoryObj.setDatetime(Calendar.getInstance().getTime());
					categoryObj.setType(urlName);
					categoryObj.setUrl(ANCHORS.get(0).getAttribute("href"));
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
}
