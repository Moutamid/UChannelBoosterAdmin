package com.moutamid.uchannelboostadmin.utils;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class Constants {
    public static final String IS_LOGIN = "is_login";
    public static final String TYPE_LIKE = "Like: ";
    public static final String TYPE_SUBSCRIBE = "Subscribe: ";
    public static final String TYPE_VIEW = "View: ";
    public static final String COINS_PATH = "coins_path";
    public static final String VIEW_COINS = "view_coins";
    public static final String LIKE_COINS = "like_coins";
    public static final String SUBSCRIBE_COINS = "subscribe_coins";
    public static final String ADD_TASK_VARIABLES = "add_tasks_variable";
    public static final String TIME_ARRAY = "time_array";
    public static final String COIN_PACK = "coin_packs";
    public static final String QUANTITY_ARRAY = "quantity_array";
    public static final String CUT_OFF_AMOUNT_OF_TASKS = "cut_of_amount_of_tasks";
    public static final String CUT_OFF_AMOUNT_OF_VIEWS = "cut_of_amount_of_views";
    public static final String CUT_OFF_AMOUNT_OF_LIKE = "cut_of_amount_of_like";
    public static final String CUT_OFF_AMOUNT_OF_SUBSCRIBE = "cut_of_amount_of_subscribe";

    public static final String CURRENT_LANGUAGE_CODE = "current_language_code";
    public static final String LANGUAGE_CODE_ENGLISH = "en";
    public static final String LANGUAGE_CODE_ARABIC = "ar";
    public static final String LANGUAGE_CODE_SPANISH = "es";
    public static final String LANGUAGE_CODE_FRENCH = "fr";
    public static final String LANGUAGE_CODE_HINDI = "hi";
    public static final String LANGUAGE_CODE_INDONESIAN = "in";
    public static final String LANGUAGE_CODE_JAPANESE = "ja";
    public static final String LANGUAGE_CODE_KOREAN = "ko";
    public static final String LANGUAGE_CODE_VIETNAMESE = "vi";
    public static final String LANGUAGE_CODE_URDU = "ur";

    public static void adjustFontScale(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.fontScale > 1.00) {
            Log.d("TAG1", "fontScale=" + configuration.fontScale);
            configuration.fontScale = 1.00f;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            context.getResources().updateConfiguration(configuration, metrics);
        }
    }
}
