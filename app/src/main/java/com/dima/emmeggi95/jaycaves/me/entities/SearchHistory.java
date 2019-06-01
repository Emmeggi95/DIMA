package com.dima.emmeggi95.jaycaves.me.entities;



import android.content.Context;
import android.content.SharedPreferences;

import com.dima.emmeggi95.jaycaves.me.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * User to save navigation flow
 */
public class SearchHistory {

    private static final int HISTORY_SIZE = 10;
    private static List<String> history;

    public static List<String> getHistory(Context context) {
        Gson gson = new Gson();
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.search_file_key), Context.MODE_PRIVATE);
        Type type = new TypeToken<List<String>>(){}.getType();
        String j = pref.getString(context.getString(R.string.search_history_key), "");
        history = gson.fromJson(j, type);
        return history;
    }

    public static void putOnTop(Context context, String item){
        if(history==null){
            getHistory(context);
        }
        history.remove(item);
        history.add(0, item);
        while(history.size()>HISTORY_SIZE){
            history.remove(HISTORY_SIZE);
        }
        for(String s : history){
            System.out.println(s);
        }
        setHistory(context, history);
    }

    public static void setHistory(Context context, List<String> newHistory){
        history = newHistory;
        Gson gson = new Gson();
        String json = gson.toJson(history);
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.search_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(context.getString(R.string.search_history_key), json);
        editor.apply();
    }

    public static void clearHistory(Context context){
        setHistory(context, new ArrayList<String>());
    }
}
