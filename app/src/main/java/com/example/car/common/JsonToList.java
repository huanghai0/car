package com.example.car.common;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;


public class JsonToList<T> {

    private final Class<T> type;

    public JsonToList(Class<T> type) {
        this.type = type;
    }

    public Class<T> getMyType() {
        return this.type;
    }

    /**
     * json转ArrayList
     *
     * @param res
     * @return
     */
    public ArrayList<T> jtl(String res) {
        Gson gson = new Gson();
        ArrayList<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(res).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, getMyType()));
        }
        return list;
    }
}
