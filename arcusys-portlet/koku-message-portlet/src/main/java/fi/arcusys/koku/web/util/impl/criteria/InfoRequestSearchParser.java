package fi.arcusys.koku.web.util.impl.criteria;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.services.facades.employee.InfoRequestTasks.InformationRequestSearchCriteria;

/**
 * Temporary class to create searchCriterias
 *
 * @author Toni Turunen
 *
 */
public class InfoRequestSearchParser {

	private static final Logger LOG = LoggerFactory.getLogger(InfoRequestSearchParser.class);

	private final InformationRequestSearchCriteria criteria;

	/* SearchMap key values */
	private static final int MAP_INIT_SIZE					= 7;
	private static final String CREATED_FROM 				= "createdFrom";
	private static final String CREATED_TO 					= "createdTo";
	private static final String REPLIED_FROM 				= "repliedFrom";
	private static final String REPLIED_TO 					= "repliedTo";
	private static final String SENDER_UID 					= "senderUid";
	private static final String RECIEVER_UID 				= "recieverUid";
	private static final String TARGET_PERSON_UID 			= "targetPersonUid";
	private static final String INFORMATION					= "searchFromTietosisalto";
	private static final String FREE_TEXT_SEARCH			= "freeTextSearch";

	public InfoRequestSearchParser(String keyword) {
		this.criteria = new InformationRequestSearchCriteriaImpl(getSearchMap(keyword));
	}

	public InformationRequestSearchCriteria getCriteria() {
		return criteria;
	}

	/**
	 * Parses string dates on finnish time format
	 *
	 * @param date
	 * @return Date
	 */
	private Date stringToDate(String date) {
		if (date == null || date.isEmpty()) {
			return null;
		}

		String[] splitted = date.split("\\.");
		if (splitted.length != 3) {
			return null;
		}

		Calendar cal = Calendar.getInstance(new Locale("fi", "FI"));
		cal.set(Integer.valueOf(splitted[2]), Integer.valueOf(splitted[1])-1, Integer.valueOf(splitted[0]), 0, 0, 0);
		return cal.getTime();
	}

	private Map<String, String> getSearchMap(String keyword) {
		Map<String, String> searchMap = new HashMap<String, String>(MAP_INIT_SIZE);
		if (keyword == null || keyword.isEmpty()) {
			return searchMap;
		}
		try {
			JSONObject json = new JSONObject(keyword);
			addToMap(searchMap, CREATED_FROM, json.getString("createdFrom"));
			addToMap(searchMap, CREATED_TO, json.getString("createdTo"));
			addToMap(searchMap, REPLIED_FROM, json.getString("repliedFrom"));
			addToMap(searchMap, REPLIED_TO, json.getString("repliedTo"));
			final String sender = json.getString("sender");
			final String reciever = json.getString("reciever");
			final String targetPerson = json.getString("targetPerson");
			if (sender != null && !sender.isEmpty()) {
				//addToMap(searchMap, SENDER_UID, userService.getLooraUserUidByUsername(sender));
				addToMap(searchMap, SENDER_UID, sender);
			}
			if (reciever != null && !reciever.isEmpty()) {
				//addToMap(searchMap, RECIEVER_UID, userService.getLooraUserUidByUsername(reciever));
				addToMap(searchMap, RECIEVER_UID, reciever);
			}
			if (targetPerson != null && !targetPerson.isEmpty()) {
				addToMap(searchMap, TARGET_PERSON_UID, targetPerson);
			}
			addToMap(searchMap, INFORMATION, json.getString("information"));
			addToMap(searchMap, FREE_TEXT_SEARCH, json.getString("freeTextSearch"));
		} catch (JSONException e) {
			LOG.warn("Failed parse JSON. Can't create searchMap for Tietopyyntö. keyword: '"+keyword+"'");
		}
		return searchMap;
	}

	private void addToMap(final Map<String, String> map, final String key, final String value) {
		String trimmed = value.trim();
		if (trimmed.isEmpty()) {
			map.put(key, null);
		} else {
			map.put(key, value);
		}
	}


	private class InformationRequestSearchCriteriaImpl implements InformationRequestSearchCriteria {

		private final Map<String, String> searchMap;

		public InformationRequestSearchCriteriaImpl(Map<String, String> searchMap) {
			this.searchMap = searchMap;
		}

		@Override
		public String getTargetPersonUid() {
			return searchMap.get(TARGET_PERSON_UID);
		}

		@Override
		public Calendar getSentTo() {
			return getTime(CREATED_TO);
		}

		@Override
		public Calendar getSentFrom() {
			return getTime(CREATED_FROM);
		}

		@Override
		public String getSenderUid() {
			return searchMap.get(SENDER_UID);
		}

		@Override
		public Calendar getRepliedTo() {
			return getTime(REPLIED_TO);
		}

		@Override
		public Calendar getRepliedFrom() {
			return getTime(REPLIED_FROM);
		}

		private Calendar getTime(String key) {
			final Date date = stringToDate(searchMap.get(key));
			if (date == null) {
				return null;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		}

		@Override
		public String getReceiverUid() {
			return searchMap.get(RECIEVER_UID);
		}

		@Override
		public String getInformationContent() {
			return searchMap.get(INFORMATION);
		}

		@Override
		public String getFreeTextSearch() {
			return searchMap.get(FREE_TEXT_SEARCH);
		}
	};

}
