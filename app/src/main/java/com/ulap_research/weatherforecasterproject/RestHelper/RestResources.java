package com.ulap_research.weatherforecasterproject.RestHelper;

/**
 * Created by Soranut on 7/29/2014.
 */
public class RestResources {

    public static final String HOST = "https://163.221.124.249";

    // REST URL
    public static final String REGISTER_URL = HOST + "/wfp_dist/api_v1/users/register";
    public static final String LOGIN_URL = HOST + "/wfp_dist/api_v1/users/login";
    public static final String GET_USER_INFO_URL = HOST + "/wfp_dist/api_v1/users/info";

    public static final String GET_CROPS_LIST = HOST + "/wfp_dist/api_v1/crops/list";
    public static final String GET_ACHIEVEMENTS_LIST = HOST + "/wfp_dist/api_v1/achievements/list";

    // REGISTRATION STATE RESULT
    public static final int USER_CREATED_SUCCESSFULLY = 0;
    public static final int USER_CREATE_FAILED = 1;
    public static final int ACCOUNT_ALREADY_EXISTED = 2;

}
