package com.ptysolutions.kairos;

import org.json.JSONObject;

/**
 * Created by Ramon on 3/29/2016.
 */
public interface AsyncResponse {
    void processFinish(JSONObject result);
}
