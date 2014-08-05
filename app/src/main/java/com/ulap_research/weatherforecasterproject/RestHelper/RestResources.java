package com.ulap_research.weatherforecasterproject.RestHelper;

/**
 * Created by Soranut on 7/29/2014.
 */
public class RestResources {

    public static final String HOST = "https://163.221.124.255/wfp_dist/api/v1";

    // REST URL

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

}
