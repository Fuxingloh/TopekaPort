package sg.fxl.topekaport;

import android.content.Intent;

import com.google.gson.Gson;

import sg.fxl.topeka.model.CategoryJson;

/**
 * Created by: Fuxing
 * Date: 26/1/2016
 * Time: 11:34 PM
 * Project: Topeka Port
 */
public class QuizSetting {
    public static final String TAG = "QuizSetting";

    public boolean showStartScreen = true;
    public boolean showEndScreen = true;
    public boolean showTrueAnimationOnly = false;

    public static class Json{
        public static Gson gson = CategoryJson.lazyGson();

        public static String to(QuizSetting data) {
            return gson.toJson(data);
        }

        public static void to(Intent intent, QuizSetting data) {
            intent.putExtra(TAG, to(data));
        }

        public static QuizSetting from(String json) {
            return gson.fromJson(json, QuizSetting.class);
        }

        public static QuizSetting from(Intent intent) {
            return from(intent.getStringExtra(TAG));
        }
    }
}
