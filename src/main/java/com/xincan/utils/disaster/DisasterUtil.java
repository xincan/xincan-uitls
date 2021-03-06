package com.xincan.utils.disaster;

import com.alibaba.fastjson.JSONObject;

/**
 * 灾种工具类
 */
public class DisasterUtil {

    /**
     * 根据颜色标识转换成字符串颜色
     * @param color
     * @return
     */
    public static String parseColorString(int color){

        if(color == 0) return "红色";
        if(color == 1) return "橙色";
        if(color == 2) return "黄色";
        if(color == 3) return "蓝色";
        return null;

    }

    /**
     * 根据颜色标识转换成字符串颜色
     * @param color
     * @return
     */
    public static int parseColorInt(String color){

        if(color.equals("红色")) return 0;
        if(color.equals("橙色")) return 1;
        if(color.equals("黄色")) return 2;
        if(color.equals("蓝色")) return 3;
        return -1;
    }

    /**
     * 根据颜色标识转换成字符串颜色
     * @param level
     * @return
     */
    public static String parseLevelString(int level){
        if(level == 0) return "Ⅰ级/特别重大";
        if(level == 1) return "Ⅱ级/重大";
        if(level == 2) return "Ⅲ级/较大";
        if(level == 3) return "Ⅳ级/一般";
        return null;
    }

    /**
     * 根据颜色标识转换成字符串颜色
     * @param level
     * @return
     */
    public static int parseLevelInt(String level){
        if(level.equals("Ⅰ级/特别重大")) return 0;
        if(level.equals("Ⅱ级/重大")) return 1;
        if(level.equals("Ⅲ级/较大")) return 2;
        if(level.equals("Ⅳ级/一般")) return 3;
        return -1;
    }

    /**
     * 根据颜色标识转换成字符串颜色
     * @param color
     * @return
     */
    public static JSONObject parseColorToLevel(int color){
        JSONObject json = new JSONObject();
        if(color == 0) {
             json.put("level", 0);
             json.put("name", "级/特别重大");
        }
        if(color == 1) {
            json.put("level", 1);
            json.put("name", "Ⅱ级/重大");
        }
        if(color == 2) {
            json.put("level", 2);
            json.put("name", "Ⅲ级/较大");
        }
        if(color == 3) {
            json.put("level", 3);
            json.put("name", "Ⅳ级/一般");
        }

        return json;
    }

    /**
     * 获取颜色
     * @param disasterColor
     * 灾种级别：0：Ⅰ级/特别重大；1：Ⅱ级/重大；2：Ⅲ级/较大；3：Ⅳ级/一般
     * @return
     */
    public static JSONObject getDisasterInfo(int disasterColor){
        JSONObject json = new JSONObject();
        if(disasterColor == 0){
            json.put("rgb", "#FF0000");
            json.put("color", "红色");
            json.put("level", "[Ⅰ级/特别重大]");
        }
        if(disasterColor == 1){
            json.put("rgb", "#FFA500");
            json.put("color", "橙色");
            json.put("level", "[Ⅱ级/重大]");
        }
        if(disasterColor == 2){
            json.put("rgb", "#FFFF00");
            json.put("color", "黄色");
            json.put("level", "[Ⅲ级/较大]");
        }
        if(disasterColor == 3){
            json.put("rgb", "#0000FF");
            json.put("color", "蓝色");
            json.put("level", "[Ⅳ级/一般]");
        }
        return json;
    }

}
