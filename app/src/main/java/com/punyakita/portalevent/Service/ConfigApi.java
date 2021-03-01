package com.punyakita.portalevent.Service;

public class ConfigApi {

    public static final String URL_API = "http://192.168.43.254:8000/api/";
    public static final String URL_DATA_FILE = "http://192.168.43.254:8000/data_file/";

    public static final String JSON_SUCCESS = "success";
    public static final String JSON_DATA = "data";
    public static final String JSON_STATUS = "status";
    public static final String JSON_ERROR = "error";
    public static final String JSON_CART_LIST = "cartList";
    public static final String JSON_CART_SELLER = "cartSeller";
    public static final String JSON_USER_SELLER = "usr_seller";
    public static final String JSON_TOTAL_PRICE = "total_price";
    public static final String JSON_ITEM = "item";
    public static final String JSON_RATING = "rating";
    public static final String JSON_CONFIRM = "confirm";
    public static final String JSON_RATING_LAPAK = "ratingLapak";
//    public static final String JSON_ = "";

    public static final String TAG_TOKEN = "token";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_ERROR = "error";
    public static final String TAG_NAME = "name";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PHONE_NUMBER = "phone_number";
    public static final String TAG_BALANCE = "balance";
    public static final String TAG_SELLER = "seller";
    public static final String TAG_AMOUNT = "amount";
    public static final String TAG_SELLING_PRICE = "selling_price";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_STOCK = "stock";
    public static final String TAG_NOTE = "note";
    public static final String TAG_TOTAL_PRICE = "total_price";
    public static final String TAG_RATING = "rating";
    public static final String TAG_REVIEW = "review";
    public static final String TAG_CONFIRM_ID = "confirm_id";
    public static final String TAG_TIME = "time";
    public static final String TAG_ITEM_ID = "item_id";
    public static final String TAG_SELLER_ID = "seller_id";
    public static final String TAG_PURCHASE_ID = "purchase_id";
//    public static final String TAG_ = "";

    public static final String KEY_LOGIN_DATA = "loginData";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static BaseApiService getAPIService() {
        return RetrofitClient.getClient(URL_API).create(BaseApiService.class);
    }

}
