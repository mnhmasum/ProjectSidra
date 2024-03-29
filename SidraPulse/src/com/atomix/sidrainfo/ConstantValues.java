package com.atomix.sidrainfo;

import com.atomix.sidrapulse.R;

public class ConstantValues {
	public static String FUNC_ID_SIGN_IN = "1";
	public static String FUNC_ID_PUSH_SETTING = "2";
	public static String FUNC_ID_MAIN_PAGE_NOTIFICATION = "3";
	public static String FUNC_ID_ANNOUNCEMENT = "4";
	public static String FUNC_ID_STAFF_DIRECTORY_DEPARTMENTS = "5";
	public static String FUNC_ID_STAFF_LIST = "6";
	public static String FUNC_ID_EVENTS = "7";
	public static String FUNC_ID_EVENT_MAKE_FAVOURITE ="8";
	public static String FUNC_ID_GALLERY_ALBUM = "9";
	public static String FUNC_ID_GALLERY_IMAGES = "10";
	public static String FUNC_ID_SIDRA_IN_NEWS_API = "11";
	public static String FUNC_ID_SIDRA_PRESS_RELEASE = "12";
	public static String FUNC_ID_HUMAN_RESOURCES_CATEGORY = "13";
	public static String FUNC_ID_HUMAN_RESOURCES_LIST = "14";
	public static String FUNC_ID_OFFER_AND_PROMOTION_API = "15";
	public static String FUNC_ID_BOOKMARK_OFFERS_AND_PROMOTION = "16";
	public static String FUNC_ID_CLASSIFIED = "17";
	public static String FUNC_ID_CLASSIFIED_CATEGORY = "18";
	public static String FUNC_ID_CLASSIFIED_PHOTO_UPLOAD = "19";
	public static String FUNC_ID_CLASSIFIED_ENTRY = "20";
	public static String FUNC_ID_SHOW_TAG_LIST = "21 ";
	public static String FUNC_ID_API_SHOW_FORUM_LIST = "22";
	public static String FUNC_ID_THREAD_PHOTO_UPLOAD = "23";
	public static String FUNC_ID_THREAD_ENTRY_API = "24";
	public static String FUNC_ID_SHOW_ALL_POLICY_DEPT = "25";
	public static String FUNC_ID_SHOW_POLICY_DATA_BY_DEPT = "26";
	public static String FUNC_ID_DELETE_API = "27";
	public static String FUNC_ID_UNREAD = "28";
	public static String FUNC_ID_OFFER_AND_PROMOTION_CATAGORIES = "29";
	public static String FUNC_ID_FORUM_COMMENT = "30";
	public static String FUNC_ID_LOGOUT = "31";
	public static String FUNC_ID_BUBBLE_READ = "32";
	public static String FUNC_ID_STAFF_SEARCH = "33";
	public static String FUNC_ID_STAFF_BOOKMARE = "34";
	public static String FUNC_ID_CLASSIFIED_UPDATE = "35";
	public static String FUNC_ID_GALLERY_YEAR_LIST = "36";
	
	public static String DEVICE_TYPE = "2";
	public static String FILE_BASE_URL = "http://114.134.91.91/sidra_pull/";
	
	public static int FORUM_TAB_SELECTED_INDEX = 1;
	public static boolean isForumDetailClicked = false;
	public static boolean LIST_NOTIFY = false;
	public static boolean isShowingHotTopic = false;
	
	public static boolean wasScreenOn = true;
	public static boolean PullDownActive = true;
	
	public static String failureMessage= "Unable to complete the task. Please try again later";
	public static String NO_DATA_FOUND_MESSAGE = "No data available";
	public static String NO_COMMENTS_MESSAGE = "No comments yet";
	
	public static int[] MAIN_MENU_PAGE_ONE_IMAGES = {R.drawable.announcements, R.drawable.staff_directory, R.drawable.forums, R.drawable.classifieds, R.drawable.offers_promotions, R.drawable.events};
	public static String[] MAIN_MENU_PAGE_ONE_TEXT = {"Announcement","Staff Directory","Forums","Classifieds","Offers", "Events"};
	
	public static int[] MAIN_MENU_PAGE_TWO_IMAGES = {R.drawable.newsletter, R.drawable.gallery, R.drawable.human_resources, R.drawable.policies, R.drawable.stay_informed};
	public static String[] MAIN_MENU_PAGE_TWO_TEXT = {"Newsletter","Gallery","HR","Policies","Stay Informed"};
	
	public static String[] MONTHS = {"January","February","March","April","May", "June", "July","August", "September","October", "November", "December"};
	public static String[] MONTHS_NUMBER={"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
	
	public static boolean isHashTagActivityRunning = false;
	
	public static int offersTabIndex = 1;
	
	public static int TOAST_DURATION = 3000;
	
}
