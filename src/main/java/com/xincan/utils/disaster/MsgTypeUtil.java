package com.xincan.utils.disaster;

public class MsgTypeUtil {

    /**
     * 预警信息转换
     * @param type
     * @return
     */
    public static String parseMsgType(String type){

        if(type.equals("Alert")) return "发布";
        if(type.equals("Update")) return "更新";
        if(type.equals("Cancel")) return "解除";
        if(type.equals("Ack")) return "确认";
        return null;
    }
}
