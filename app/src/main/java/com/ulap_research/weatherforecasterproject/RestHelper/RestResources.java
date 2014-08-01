package com.ulap_research.weatherforecasterproject.RestHelper;

/**
 * Created by Soranut on 7/29/2014.
 */
public class RestResources {

    public static final String HOST = "https://163.221.127.68/wfp_dist/api/v1";

    // REST URL
    public static final String REGISTER_URL = HOST + "/users/register";
    public static final String LOGIN_URL = HOST + "/users/login";
    public static final String GET_USER_INFO_URL = HOST + "/users/info";

    public static final String GET_USER_RANK = HOST + "/users/rank";
    public static final String GET_USER_GLOBAL_RANK = HOST + "/users/global_rank";

    public static final String GET_CROPS_LIST = HOST + "/crops/list";
    public static final String GET_ACHIEVEMENTS_LIST = HOST + "/achievements/list";

}
