package mos.kos.cache.tool;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: Json解析类
 * @Author: Kosmos
 * @Date: 2016年8月29日 16:28
 * @Email: KosmoSakura@foxmail.com
 */
public class UGson {
    private static Gson gson;

    static {
        gson = new Gson();
//1、
//        gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd HH:mm:ss")
//                .create();

//2、
//        gson = Converters.registerAll(new GsonBuilder()).create();

//3、
//        gson = Converters.registerAll(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")).create();
    }

    /**
     * @return 返回一个实体类对象 JsonSyntaxException
     */
    public static <T> T getJson(String jsonString, Class<T> cls) throws JsonParseException {
        return gson.fromJson(jsonString, cls);
    }

    /**
     * @return 返回一个列表 JsonSyntaxException
     */
    public static <T> ArrayList<T> getJsonArray(String jsonString, Class<T> cls) throws JsonParseException {
        ArrayList<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(jsonString).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }

    /**
     * @return 返回一个带map的列表
     */
    public static List<Map<String, Object>> getListMap(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = gson.fromJson(jsonString,
                new TypeToken<List<Map<String, Object>>>() {
                }.getType());
        } catch (Exception e) {
            // TODO: handle exception
        }
        return list;
    }

    //类型转换
    public static <T> ArrayList<T> convertDto(ArrayList list, Class<T> cls) {
        ArrayList<T> dtoList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            String json = gson.toJson(object);
            T dto = gson.fromJson(json, cls);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
