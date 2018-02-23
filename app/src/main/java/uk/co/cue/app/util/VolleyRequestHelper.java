package uk.co.cue.app.util;

import android.content.Context;

import com.android.volley.Request;

import java.util.HashMap;

/**
 * Created by danieltonks on 21/02/2018.
 */

public class VolleyRequestHelper {

    private String url;
    private Request.Method method; //POST or GET
    private HashMap<String, String> params;
    private Context c;

    public VolleyRequestHelper(String url, HashMap<String, String> params, Request.Method method, Context c) {


    }

    public String doRequest() {
        return null;
    }
}
