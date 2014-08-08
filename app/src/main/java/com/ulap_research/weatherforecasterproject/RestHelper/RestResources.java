package com.ulap_research.weatherforecasterproject.RestHelper;


public class RestResources {

    // REST HOST
    public static final String HOST = "https://163.221.127.90/wfp_dist/api/v1";

    /**
     * REST URL
     */
    // User management
    public static final String REGISTER_URL = HOST + "/users/register";
    public static final String LOGIN_URL = HOST + "/users/login";
    public static final String GET_USER_INFO_URL = HOST + "/users/info";

    // User Ranking
    public static final String GET_USER_RANK = HOST + "/users/rank";
    public static final String GET_USER_GLOBAL_RANK = HOST + "/users/global_rank";

    // Lasted sensor info
    public static final String GET_SENSORS_LIST = HOST + "/sensors/list_lasted";

    // Crops
    public static final String GET_CROPS_LIST = HOST + "/crops/list";
    public static final String GET_USER_CROPS = HOST + "/users/crops";

    // Achievements
    public static final String GET_USERS_ACHIEVEMENTS = HOST + "/users/achievements";
    public static final String GET_ACHIEVEMENTS_LIST = HOST + "/achievements/list";

    // Buy rain
    public static final String BUY_RAIN = HOST + "/users/rain/buy";

    // Plant crop
    public static final String PLANT_CROP = HOST + "/crops/plant";

    // Sell crop
    public static final String GET_CROP_SELL_PRICE = HOST + "/users/crops/sell_price/";
    public static final String SELL_CROP = HOST + "/users/crops/sell";

    // Water crop
    public static final String WATER_CROP = HOST + "/users/rain/use";

    // Upload sensor data
    public static final String UPLOAD_SENSOR_DATA = HOST + "/sensors/upload";

    // Update cloud point after uploaded data
    public static final String UPDATE_CLOUD_POINT = HOST + "/users/cloud_point/update";

}
