package sg.fxl.topeka.model;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

import sg.fxl.topeka.model.quiz.QuizQuestion;

/**
 * Created by: Fuxing
 * Date: 26/1/2016
 * Time: 4:22 PM
 * Project: Topeka Port
 */
public class CategoryJson {
    private static final String TAG = "CategoryJson";
    private static Gson gson = lazyGson();

    public static Gson lazyGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(QuizQuestion.class, new GenericPropertyMarshal())
                    .create();
        }
        return gson;
    }

    public static String to(Category category) {
        return gson.toJson(category);
    }

    public static void to(Intent intent, Category category) {
        intent.putExtra(TAG, to(category));
    }

    public static Category from(String json) {
        return gson.fromJson(json, Category.class);
    }

    public static Category from(Intent intent) throws IOException {
        return from(intent.getStringExtra(TAG));
    }

    public static class GenericPropertyMarshal implements JsonSerializer<Object>, JsonDeserializer<Object> {
        private static final String CLASS_META_KEY = "CLASS_META_KEY";

        @Override
        public Object deserialize(JsonElement jsonElement, Type type,
                                  JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            JsonObject jsonObj = jsonElement.getAsJsonObject();
            String className = jsonObj.get(CLASS_META_KEY).getAsString();
            try {
                Class<?> clz = Class.forName(className);
                return jsonDeserializationContext.deserialize(jsonElement, clz);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Object object, Type type,
                                     JsonSerializationContext jsonSerializationContext) {
            JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
            jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY, object.getClass().getCanonicalName());
            return jsonEle;
        }
    }
}
