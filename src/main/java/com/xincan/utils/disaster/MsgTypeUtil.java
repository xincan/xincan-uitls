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

    public static String parseOneType(int type){

        if(type==0) return  "短期预报";
        if(type==1) return "中期预报";
        if(type==2) return "长期预报";
        if(type==3) return "气象专题专报";
        if(type==4) return "重大气象专题专报";

        return null;
    }
}
