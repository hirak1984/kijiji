package pvt.hrk.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum URLS {

	CITY_OF_TORONTO("city-of-toronto", "l1700273"), GTA("gta-greater-toronto-area", "l1700272"), MARKHAM_YORK(
			"markham-york-region", "l1700274"), OSHAWA_DURHAM("oshawa-durham-region", "l1700275"), MISSISSAUGA_PEEL(
					"mississauga-peel-region", "l1700276"), OAKVILLE_HALTON("oakville-halton-region", "l1700277");
	private static final String DOMAIN_URL = "http://www.kijiji.ca";
	private static final String template = DOMAIN_URL + "/b-buy-sell/%s/c10%sr%s?ad=%s&price=%s";
	private static final String priceRange = "0__1000";
	private static final String radius = "100.0";
	private static final int pagination = 3;
	private static final List<String> IGNORE_CATEGORIES = Arrays.asList(
			/*"AllCategories", "buyandsell"*/
			"b-city-of-toronto",
			"furniture",
			"sporting-goods-exercise",
			"electronics",
			"video-games-consoles",
			"home-appliance",
			"tool",
			"business-industrial",
			"home-outdoor",
			"health-special-needs",
			"computer",
			
			"bikes",
			"home-indoor",
			
			"home-renovation",
			"musical-instrument"
			
			/*"audio",
			"clothing",
			"baby-items",
			"art-collectibles",
			"buy-sell-other",
			"books",
			"toys-games",
			"phone-tablet",
			"jewelry-watch",
			"computer-accessories",
			"hobbies-craft",
			"cd-dvd-blu-ray",
			"tickets",
			"camera-camcorder-lens",
			"tvs-video",
			"free-stuff",
			"garage-sale-yard-sale"*/
			
			);
	private String name;
	private String id;

	URLS(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public Set<String> GET_INITIAL_URLS() {
		Set<String> set = new HashSet<>();
		set.add(String.format(template, this.name, this.id, radius, "offering", priceRange));
		set.add(String.format(template, this.name, this.id, radius, "wanted", priceRange));
		return set;
	}

	public static String ADD_DOMAIN_TO_URL(String url) {
		if (url == null) {
			return DOMAIN_URL;
		} else if (url.startsWith(DOMAIN_URL)) {
			return url;
		} else {
			return DOMAIN_URL + url;
		}
	}

	public List<String> paginateThisURL(String url) {
		if (url.indexOf(this.name) == -1) {
			throw new IllegalArgumentException("Not my URL. Can't paginate this");
		}
		List<String> urls = new ArrayList<>(pagination);
		for (int i = 0; i < pagination; i++) {
			StringBuffer sb = new StringBuffer(this.name).append("/").append("page-").append(i + 2);
			urls.add(url.replaceAll(this.name, sb.toString()));
		}
		return urls;
	}
	
	public static boolean shouldIgnore(String url){
		for(String categories:IGNORE_CATEGORIES){
		if(url.contains(categories)){
			return true;
		}
		}
		return false;
	}

}
