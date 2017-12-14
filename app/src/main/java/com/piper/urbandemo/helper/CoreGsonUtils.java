package com.piper.urbandemo.helper;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by developers on 14/12/17.
 */

public class CoreGsonUtils {

    private static Gson instance;

    private static Gson getGsonObject() {

        if (instance == null) {
            instance = new Gson();
        }
        return instance;

    }

    public static <T> String toJson(Object obj) {

        String gsonstr = "";

        Gson gson = getGsonObject();
        gsonstr = gson.toJson(obj);
        return gsonstr;
    }

    public static <T> ArrayList<T> fromJsontoArrayList(String string, Class<T> model) {

        Gson gson = getGsonObject();
        T gfromat = null;
        ArrayList<T> localArrayList = new ArrayList<T>();
        try {

            JSONArray jsonInner = new JSONArray(string);
            int i = 0;
            while (i < jsonInner.length()) {
                gfromat = gson.fromJson(jsonInner.get(i).toString(), model);
                localArrayList.add(gfromat);
                i++;

            }

        } catch (Exception e)

        {
            e.printStackTrace();
        }

        return localArrayList;
    }
}
