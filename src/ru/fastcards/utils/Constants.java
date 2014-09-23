package ru.fastcards.utils;

import ru.fastcards.R;

/**
 * 
 * @author Denis V
 * @since 20.11.2013 modified
 *Add TABLE_APPEAL
 */
public final class Constants {
	private Constants(){};
//	public static final int PHOTO_BOX_SCROLL_VIEW_ROWS_NUMBER = 3;
//	public static final int SHOP_CARDS_COLUMNS_NUM = 3;

	public static final String CACHE_FILE_NAME = "The_project_cache";
	public static final String CACHE_VKONTAKTE_TOKEN = "vkontakte_cached_token";
	public static final String CACHE_VKONTAKTE_USER_ID = "vkontakte_cached_user_id";
	public static final String CACHE_FACEBOOK_TOKEN = "facebook_cached_token";
	public static final String CACHE_INSTAGRAM_TOKEN = "instagram_cached_token";
	public static final String CACHE_FASTCARDS_USER_ID = "fastcards_user_id";
	public static final String CACHE_USER_WEALTH = "user_wealth";
	
	public static final String API_ID = "2800844"; //using here vk id just for now
    public static final String id_facebook= "1414152868808243";
    
    //Request constants for startActivityForResult
    public static final int REQUEST_VK_AUTH = 101;
    public static final int REQUEST_MESSAGE_POSTCARD_VK = 102;
    public static final int REQUEST_WALL_POSTCARD_VK = 103;
    public static final int REQUEST_MESSAGE_POSTCARD_FB = 104;
    public static final int REQUEST_WALL_POSTCARD_FB = 105;
	public static final int REQUEST_FACEBOOK = 106;//"WEB_VIEW_AUTH_HANDLER_TOKEN_KEY";
	public static final int REQUEST_OK = 107;
	public static final int REQUEST_WALL_POSTCARD_OK = 108;
	public static final int REQUEST_MESSAGE_POSTCARD_OK = 109;
	public static final int REQUEST_DISPLAYER = 110;
	public static final int REQUEST_VK_RECIPIENTS = 111;
	public static final int REQUEST_CONTACTS = 112;
	public static final int REQUEST_CREATE_EVENT = 113;
	public static final int REQUEST_CATEGORY = 114;
	public static final int REQUEST_CATEGORY_GROUP = 115;
	public static final int REQUEST_RECIPIENTS = 116;
	public static final int REQUEST_NOTIFICATION = 117;
	public static final int REQUEST_BUY = 118;
	public static final int REQUEST_MANAGE_EVENT = 119;	
	public static final int REQUEST_MODIFY_GROUP = 120;
	public static final int REQUEST_MODIFY_CONTACT = 121;
	public static final int REQUEST_FB_RECIPIENTS = 122;
	public static final int REQUEST_FB_AUTH = 123;
	public static final int REQUEST_RECIPIENTS_GROUP = 124;
	public static final int REQUEST_SEND_MMS = 125;
	public static final int REQUEST_SEND_EMAIL = 126;
	public static final int REQUEST_VK_AUTH_SENDYOURSELF = 127;
	public static final int REQUEST_CONTACTS_EMAIL = 128;
	public static final int REQUEST_CONTACTS_MSG = 129;
	
	//Extras to pass data between activities
	public static final String EXTRA_ALBUM_ID = "album_id";
	public static final String EXTRA_PHOTOS_NUMBER = "photos_number";
	public static final String EXTRA_USER_ID = "user_id";
	public static final String EXTRA_LOAD_TASK = "load_task";	public static final String EXTRA_CONTENT_OWNER = "whoose_albums";
	public static final String EXTRA_PHOTO_URL = "photo_url";
	public static final String EXTRA_POSTCARDS_URL_ARRAY = "photos_url_array";
	public static final String EXTRA_INITIAL_PAGE = "intial_page";
	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_RECIPIENTS_IDS = "friend_ids";
	public static final String EXTRA_RECIPIENTS_POSTCARD_ID = "extra_postcard_id";
	public static final String EXTRA_NAME = "extra_recipients_name";
	
	public static final String EXTRA_RECIPIENTS_THUMB_URL = "extra_recipients_thumb_url";
	public static final String EXTRA_RECIPIENTS_GENDER = "extra_recipients_gender";
	public static final String EXTRA_RECIPIENTS_ORIGIN = "extra_recipients_origin";
	public static final String EXTRA_RECIPIENTS_NICK_NAME = "nickname_extra";
	
	public static final String EXTRA_SELECTED_POSTCARDS_IDS = "selected_postcard_ids";
	public static final String EXTRA_THEME_ID = "postcards_ids_array";
	public static final String EXTRA_PARENT_ITEM_ID = "parent_item_id";
	public static final String EXTRA_PUSH = "push";
	public static final String EXTRA_CATEGORY_ID = "category";
	public static final String EXTRA_DATE = "extra_date";
	public static final String EXTRA_PERIODICITY = "periodicity";
	public static final String EXTRA_CATEGORY_GROUP = "categories_array";
	public static final String EXTRA_ID = "id";
	public static final String EXTRA_EVENT_ID = "event_id";
	public static final String EXTRA_TYPE = "type";
	public static final String EXTRA_PROJECT_ID = "ProjectId";	
	public static final String EXTRA_NOTIFICATION = "Notification";
	public static final String EXTRA_EVENT_LOCATION = "modified_event_location";
	public static final String EXTRA_POSTCARD_SELECTED = "selected_postcard_type";
	public static final String EXTRA_COMUNICATION_TYPE = "extra_com_type";
	public static final String EXTRA_RECIPIENTS_GROUP = "recipients_group";
	public static final String EXTRA_IS_GROUP = "isGroup";
	public static final String EXTRA_MESSAGE = "Message";
	public static final String EXTRA_PURCHASE_ID = "purchase_id";
	public static final String EXTRA_COMUNICATION_FILTER = "CommunicztionFilter";
	
	public static final String EXTRA_FLAG = "ExtraFlag";
	public static final String EXTRA_POSITION = "ExtraPosition";
	public static final String EXTRA_SIZE = "Size";

	//DataBase constants
	public static final String DATA_BASE_NAME = "TheFastcardsDataBase";	
	
	public static final String TABLE_CONTACTS = "Contacts";
	public static final String TABLE_EVENTS_RECIPIENTS = "tableEventsRecipients";
	public static final String TABLE_EVENTS = "tableEvents";
	public static final String TABLE_COMUNICATION = "TableComunication";
	public static final String TABLE_CATEGORY = "TableCategory";
	public static final String TABLE_CATEGORY_GROUP = "TableCategoryGroup";
	public static final String TABLE_THEMES = "TableThemes";
	public static final String TABLE_TEXT_PACKS = "TextPacks";
	public static final String TABLE_TEXTS = "Texts";
	public static final String TABLE_APPEALS = "AppealsTable";
	public static final String TABLE_VERSIONS = "Versions";
	public static final String TABLE_PROJECTS = "TableProjects";
	public static final String TABLE_LISTS = "TableContactGroups";
	public static final String TABLE_LIST_CONTACTS = "TableGroupsRecipients";
	public static final String TABLE_STARS_PURCHASES = "TableStarsPurchases";

	//Origin constants - wich define where the user retrieved from
	public static final int ORIGIN_VK = 1501;
	public static final int ORIGIN_CONTACTS = 1502;
	public static final int ORIGIN_FB = 1503;
	
	//Comunication type constants
	public static final String COMUNICATION_TYPE_PHONE = "comunic_phone";
	public static final String COMUNICATION_TYPE_EMAIL = "comunic_email";
	public static final String COMUNICATION_TYPE_VK = "comunic_vk_id";
	public static final String COMUNICATION_TYPE_FB = "comunic_fb_id";
	public static final String COMUNICATION_TYPE_CONTACTS_ID = "comunic_contacts_id";
	
	//Event constants
	public static final String EVENT_CATEGORY_ID_BIRTHDAY = "17";
	public static final String EVENT_TYPE_BIRTHDAY = "event_type_birthday";
	public static final String EVENT_TYPE_COMMON_HOLIDAYS = "event_type_common";
	public static final String EVENT_TYPE_CUSTOM = "event_type_users";
	
	public static final int EVENT_REPEAT_FALSE = 0;
	public static final int EVENT_REPEAT_TRUE = 1;
	
	
	public static final String PREFS_NAME="preferences_file";
	public static final String RESENTLY_USED_ID="recently_used_id";
	public static final String RECENTLY_USED_ARRAY_SIZE="recently_used_array_size";
	public static final String RECENTLY_USED_TEXT_NUMBER="recently_used_text_number";
	
	public static final String WITHOUT_APPEAL="whithout_appeal";
	
	public static final int CLOSE=0;
	public static final int OPEN=1;
	
	
	public static final int POSTCARD_VERTICAL = 1819;
	public static final int POSTCARD_SQUARE = 1820;
	public static final int POSTCARD_HORIZONTAL = 1821;
	
	public static final int PURCHASE_TYPE_THEME_ID=1;
	public static final int PURCHASE_TYPE_TEXT_ID=2;
	public static final int PURCHASE_TYPE_OFFER_ID=3;
	public static final int PURCHASE_TYPE_MONEY_ID=4;

	public static final int CREATE_EVENT_CALL = 15;
	public static final int EDITOR_CALL = 25;
	public static final int MANADGER_CALL = 35;
	
	public static final String SHOP_DIALOG="shop_dialog";
	
	public static final String POSTCARD_TEXT="postcard_text";
	public static final String POSTCARD_TITLE_TEXT="postcard_title_text";
	public static final String POSTCARD_SIGNATURE_TEXT="postcard_signature_text";
	public static final String POSTCARD_SIGNATURE_IMAGE="postcard_signature_image";
	
	public static final String POSTCARD_IMAGE="postcard_image";
	public static final String VERTICAL_IMAGE="vertical_image";
	public static final String SQUARE_IMAGE="square_image";
	public static final String POSTCARD_FRONT_IMAGE="front_image";
	public static final String POSTCARD_BACK_IMAGE="back_image";
	
	public static final String VERSIONS_CALENDAR = "Calendar";
	public static final String VERSIONS_GROUPS = "Groups";
	public static final String VERSIONS_CATEGORIES = "Categories";
	public static final String VERSIONS_THEMES = "Themes";
	public static final String VERSIONS_TEXTS = "Texts";
	public static final String VERSIONS_OFFERS = "Offers";
	public static final String VERSIONS_APPEALS = "Appeals";
	
	public static final String TAB_OFFER="tab_offer";
	public static final String TAB_THEME="tab_theme";	
	public static final String TAB_TEXT="tab_text";
	
	public static final int ADD=R.drawable.selector_btn_add;
	public static final int DELETE=R.drawable.selector_btn_delete;
	
	public static final String RED="RED";
	public static final String GREEN="GREEN";
	public static final String BLUE="BLUE";
	
	public static final int GET_RECOMMENDED=0;
	public static final int GET_NEW=1;
	public static final int GET_BEST_SELLERS=2;
	public static final String PURCHASE_STARS = "StarsPurchase";
	
	
	public static final int TRANSACTION_SUCCED = 0;
	public static final int NOT_ENOUGH_STARS=1;
	
	public static final String SIGNATURE_VERTICAL="SignVertical";
	public static final String SIGNATURE_PRINT="SignPrint";
	public static final String CACHE_USER_PRIMARY_CONTACT = "PrimaryContact";
	
	public static final String FLAG_MODIFY_TEMPORARY = "FlagModifyTemporary";
//	public static final String COMUNICATION_FILTER_MAIL = "FilterMail";
//	public static final String COMUNICATION_FILTER_PHONE = "FilterPhone";
	

}

