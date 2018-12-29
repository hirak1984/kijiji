package pvt.hrk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pvt.hrk.database.GenericDAO;
import pvt.hrk.database.PersistenceManager;
import pvt.hrk.entity.AdItem;
import pvt.hrk.utils.Utils;

public class KijijiAdDownloader {
	 static Logger LOGGER = LoggerFactory.getLogger(KijijiAdDownloader.class);
	private static final List<String> URL_LIST = SOURCE_URLS();
	public static WebDriver BROWSER = new HtmlUnitDriver();
	private static final long TIME_IN_SECONDS = 5;

	public static void main(String[] args) {
		long functionStart = System.nanoTime();
		List<AdItem> list = new ArrayList<>();
		URL_LIST.forEach(URL -> {
			LOGGER.info("Now Browsing:");
			LOGGER.info(URL);
			long start = System.nanoTime();
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
					return;
				}
				WebElement DESCRIPTION = AD.findElement(By.className("description"));
				list.add(new AdItem("","", getContent(PRICE), getContent(TITLE), getContent(LOCATION),
						getContent(DATE_POSTED.get(0)), getContent(DESCRIPTION)));
			});
			LOGGER.info("Download completed in {} seconds:",TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start));

			WAIT_FOR(TIME_IN_SECONDS);
		});

		list.forEach(item -> {
			GenericDAO<AdItem, Serializable> dao = new GenericDAO<>(AdItem.class);
			AdItem savedItem = dao.findById(item.getTitle().hashCode() + Utils.GET_EPOCH_DATE());

			if (savedItem != null) {
				LOGGER.warn("Already exists,skipping");
				LOGGER.warn("{}",item);
				return;
			}
			dao.save(item);
		});

		BROWSER.quit();
		PersistenceManager.INSTANCE.close();
		System.out.println("Total time taken:" + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - functionStart));
	}

	public static final void WAIT_FOR(long seconds) {
		try {
			LOGGER.info("Waiting for {} seconds...",seconds);
			Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	private static final String getContent(WebElement element) {
		String text = Utils.format_preserve_space(element.getAttribute("textContent"));
		return text;
	}

	private static final List<String> SOURCE_URLS() {
		List<String> list = new ArrayList<>();
		list.add("http://www.kijiji.ca/b-buy-sell/gta-greater-toronto-area/c10l1700272?price=0__200&gpTopAds=y");
		String pagedUrl = "http://www.kijiji.ca/b-buy-sell/gta-greater-toronto-area/hirak/c10l1700272?price=0__500&gpTopAds=y";
		for (int i = 2; i < 5; i++) {
			list.add(pagedUrl.replaceAll("hirak", String.format("page-%d", i)));
		}

		return list;
	}
}
