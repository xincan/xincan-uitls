package com.xincan.utils.xml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * xml工具类
 */
public class XMLUtil {

    // 预警消息内容
    private static String content;

    /**
     *
     * 获取文件名称
     *
     * 格式：【 预警信息发布单位_签发时间_预警事件类型_预警事件级别_预警信息状态.XML 】
     *
     * @param organizationCode  预警信息发布单位
     * @param sendTime          签发时间
     * @param disasterCode      预警事件类型
     * @param disasterLevel     预警事件级别
     * @param warnType          预警信息状态
     * @return
     */
    private static String getXMLFileName(String organizationCode, String sendTime, String disasterCode, String disasterLevel, String warnType){
        return (organizationCode +
                "_" +
                sendTime.replace("-","").replace(" ","").replace(":","") +
                "_" +
                disasterCode +
                "_" +
                disasterLevel +
                "_" +
                warnType +
                ".xml").toUpperCase();
    }

    /**
     * 获取资源文件信息
     *
     * 格式：【 预警信息发布单位_签发时间_预警事件类型_预警事件级别_预警信息状态_附件类型_附件编号.后缀 】
     *
     * @param organizationCode  预警信息发布单位
     * @param sendTime          签发时间
     * @param disasterCode      预警事件类型
     * @param disasterLevel     预警事件级别
     * @param warnType          预警信息状态
     * @param index             文件下标
     * @return
     */
    private static String getResourceName(String organizationCode, String sendTime, String disasterCode, String disasterLevel, String warnType, int index, String fileName){

        // 1：获取文件后缀名
        String fix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        // 2：判断文件类型
        String type = fix.equals("doc") || fix.equals("docx") || fix.equals("xls") || fix.equals("xlsx") ? "P" : "O";
        // 3：拼接文件名称

        return (organizationCode +
                "_" +
                sendTime.replace("-","").replace(" ","").replace(":","") +
                "_" +
                disasterCode +
                "_" +
                disasterLevel +
                "_" +
                warnType +
                "_" +
                type +
                "_0" +
                (index+1) +
                "."+ fix).toUpperCase();

//        "33000041600000_20121025091026_11B01_BLUE_ALERT _O_0"+(i+1)+".jpg"
    }

    /**
     *
     * 预警事件严重程度
     *
     * Red(红色预警/I级/特别重大)
     * Orange(橙色预警/II级/重大)
     * Yellow(黄色预警/III/较大)
     * Blue(蓝色预警/IV/一般)
     * Unknown(未知,9)
     *
     * @param disasterColor
     * @return
     */
    private static String getSeverity(Integer disasterColor){
        if(disasterColor == 0) return "Red";
        if(disasterColor == 1) return "Orange";
        if(disasterColor == 2) return "Yellow";
        if(disasterColor == 3) return "Blue";
        return "Unknown";
    }

    /**
     *
     * 获取预警标题
     * 格式：【预警信息发布单位 + 发布 + 预警事件类型名称 + 预警事件类型颜色 + 预警】
     *
     * @param organizationName
     * @param disasterName
     * @param disasterColor
     * @return
     */
    private static String getHeadline(String organizationName,String disasterName, Integer disasterColor){
        String headline = organizationName +"发布"+ disasterName;
        if(disasterColor == 0) {
            headline += "红色";
        }
        if(disasterColor == 1) {
            headline += "橙色";
        }
        if(disasterColor == 2) {
            headline += "黄色";
        }
        if(disasterColor == 3) {
            headline += "蓝色";
        }
        headline += "预警";
        return headline;
    }




    /**
     *
     * 获取预警消息唯一标识
     *
     * 格式：【 预警信息发布单位_签发时间 】
     * @param organizationCode
     * @param sendTime
     * @return
     */
    private static String getIdentifier(String organizationCode, String sendTime){
       return organizationCode + "_" + sendTime.replace("-","").replace(" ","").replace(":","");
    }


    /**
     *
     * 拼接消息主体集合
     *
     * @param code 当前code dom节点
     * @param json 数据源
     */
    private static void appendMethodList(Element code, JSONObject json){
        // 获取发布手段
        JSONArray channelArray = json.getJSONArray("channel");

        // 获取影响地区
        JSONArray areaArray = json.getJSONArray("area");

        // 获取群组
        JSONObject group = json.getJSONObject("group");

        for(int i = 0; i<channelArray.size(); i++){
            JSONObject channel = channelArray.getJSONObject(i);

            // （必选）消息主体
            Element method = code.addElement("method");
            // （必选）发布手段编码： 如短信的是：SMS
            method.addElement("methodName").setText(channel.getString("channelCode"));

            // 获取地区ID
            String areaId = areaArray.getJSONObject(0).getString("areaId");

            // 获取预警内容
            content = json.getString("content_" + channel.getString("channelId") + "_" + areaId);

            // （必选）使用该手段发布的发布内容（若为空，则发布手段到<description>字段取完整的预警信息内容）
            method.addElement("message").setText(content);

            // 获取当前发布手段下的渠道
            JSONArray groupArray = group.getJSONArray(channel.getString("channelId"));

            if(groupArray == null){
                // 使用该手段发布的对象/对象群组ID
                method.addElement("audienceGrp").setText("");
            }else {
                String groupId = "";
                for (int j = 0; j < groupArray.size(); j++){
                    groupId += "," + groupArray.getJSONObject(j).getString("userGroupId");
                }
                // 使用该手段发布的对象/对象群组ID
                method.addElement("audienceGrp").setText(groupId.substring(1));
            }

            // （可选）使用该手段发布的单独对象
            method.addElement("audiencePrt").setText("");

        }
    }

    /**
     * 拼接地区节点
     * @param info
     * @param json
     */
    private static void appendArea(Element info, JSONObject json){

        // 获取地区信息
        JSONArray areaArray = json.getJSONArray("area");

        if(areaArray.size() == 0) return;

        for (int i = 0; i < areaArray.size(); i++){

            //（可选）包含影响区域和发布区域的子元素	，可以存在多个，目标区域为所有描述区域的并集。
            Element area = info.addElement("area");

            JSONObject a = areaArray.getJSONObject(i);

            //（必选）影响区域的文本描述	对突发事件影响区域的文字描述
            area.addElement("areaDesc").setText(a.getString("areaName"));

            //（可选）用多边形表示预警信息的发布区域，值为多个坐标点对，用空格分隔；
            // 描述多边形时。首尾为同一个点，为封闭图形；描述线状元素时，首尾不封闭。
            area.addElement("polygon").setText("");

            //（可选）用圆形表示预警信息的发布区域，以圆心点和半径表示，值为坐标点对和半径，用空格分隔；点状元素，可用半径为0的circle描述；半径单位为米。
            area.addElement("circle").setText("");

            //（必选）预警信息发布区域的行政区划代码	用行政区划代码表示预警信息的发布区域，多个行政区划代码之间用“，”分隔，行政区划代码参见《国家突发事件预警信息发布系统预警信息地域编码规范》
            area.addElement("geocode").setText(a.getString("areaCode"));

        }


    }

    /**
     * 拼接上传资源节点
     * @param info
     * @param json
     */
    private static void appendResource(Element info, JSONObject json){

        // 获取文件列表
        JSONArray fileArray = json.getJSONArray("files");

        if(fileArray.size() == 0) return;

        for(int i = 0; i<fileArray.size(); i++){

            JSONObject file = fileArray.getJSONObject(i);

            //（可选）包含附件的子元素
            // 1）指向与本条预警信息<info>元素相应的一个补充文件，可以使图像或者声音等文件。
            // 2）可存在多个。
            Element resource = info.addElement("resource");

            //（必选）附件文件名	系统根据用户上传的附件自动生成的与预警信息文件名匹配的附件文件名，参见第8节预警信息附件文件命名。
            String resourceName = getResourceName(
                    json.getString("organizationCode"),
                    json.getString("sendTime"),
                    json.getString("disasterCode"),
                    json.getString("disasterLevel"),
                    json.getString("warnType"),
                    i,
                    file.getString("name")
            );

            // （必选）系统根据用户上传的附件自动生成的与预警信息文件名匹配的附件文件名。
            resource.addElement("resourceDesc").setText(resourceName);

            //（必选）附件文件大小 单位为byte由系统自动生成。
            resource.addElement("size").setText(file.getString("size") + "K");

            //（可选）附件内容的Hash码	采用SHA-1算法，参照FIPS 180-2。 用于判断附件是否被篡改。
            //SHA-1规范URL：//http://en.wikipedia.org/wiki/SHA-1。 默认为空。
            resource.addElement("digest").setText("");
        }
    }


    /**
     * 生成国突对接CAP协议文件
     * @param json
     */
    public static String setRecordXML(JSONObject json, String saveUrl){

        // Document
        Document document = DocumentHelper.createDocument();

        // 创建一级子节点
        Element root = document.addElement(json.getString("warnType").toLowerCase());
        root.addNamespace("xmlns", "http://com.icss.amp");

        // （必选）预警消息唯一标识
        root.addElement("identifier").setText(getIdentifier(json.getString("organizationCode"), json.getString("sendTime")));

        // （必选）预警信息发布单位名称
        root.addElement("sender").setText(json.getString("organizationName"));

        // （必选）预警信息发布单位编码
        root.addElement("senderCode").setText(json.getString("organizationCode"));

        // （可选）发布单位手机
        root.addElement("phone").setText("");

        // （必选）预警信息发布时间
        root.addElement("sendTime").setText(json.getString("sendTime") + "+08:00");

        // （必选）预警信息类型：[Actual（实际）,Exercise（演练）,Test（测试）,Draft（草稿）],目前取值仅使用“Actual”和“Test”，其中 “Test”可用于发布测试预警， Exercise和Draft暂不使用
        root.addElement("status").setText(json.getString("warnType"));

        // （必选）预警信息状态：[Alert（首次）,Update（更新）,Cancel（解除）,Ack（确认）,Error（错误）]，目前只采用“Alert”“Update”“Cancel”三个枚举值，其余枚举值保留，暂不使用。
        root.addElement("msgType").setText(json.getString("msgType"));

        // （必选）发布范围：[Public（公开）,Restricted（限制权限）,Private（特定地址）],固定使用“Public”值，其余两个枚举值保留，暂不使用。
        root.addElement("scope").setText(json.getString("scope"));

        // （必选）预警信息的发布手段和发布对象
        Element code = root.addElement("code");

        // 获取消息主体集合
        appendMethodList(code,json);

        // （可选）密级：取值包括：[None（无）,Inner（内部）],默认为“None”
        root.addElement("secClassification").setText("");

        // （可选）对预警信息解除原因的说明, 若<msgType> 为“Cancel”，则在此说明其原因。
        root.addElement("note").setText("");

        // （可选）描述本条预警信息引用的预警信息
        // <msgType>为“Update”时，本字段表示所更新的预警事件的首发信息<identifier>；
        // <msgType>为“Cancel”时，本字段表示所解除的预警事件的首发信息<identifier>；
        root.addElement("references").setText("");

        //（必选）信息
        Element info = root.addElement("info");

        //（可选）预警信息描述所使用的语言代码，取值参照RFC3066 Natural language identifier，默认值为“zh-CN”
        info.addElement("language").setText("zh-CN");

        //（必选）预警事件类型编码：	取值见《国家应急平台体系信息资源分类与编码规范》
        info.addElement("eventType").setText(json.getString("disasterCode"));

        //（必选）预警事件的紧急程度：[Immediate(立即行动), Expected(准备行动), Future(等待行动), Past(已过去), Unknown(未知)], 默认"Unknown"，其它枚举值保留，暂不使用
        info.addElement("urgency").setText("Unknown");

        //（必选）预警事件的严重程度：[Red(红色预警/I级/特别重大), Orange(橙色预警/II级/重大), Yellow(黄色预警/III/较大), Blue(蓝色预警/IV/一般), Unknown(未知,9)]
        info.addElement("severity").setText(getSeverity(json.getInteger("disasterColor")));

        //（必选）预警事件发生的可能程度	：[Observed(确定发生)，VeryLikely(非常可能)，Likely(可能发生)，Unlikely(不大可能)，Unknown(未知)]，默认Unknown，其它枚举值保留，暂不使用。
        info.addElement("certainty").setText("Unknown");

        //（必选）预警信息生效时间，预警信息在系统中的签发时间
        info.addElement("effective").setText(json.getString("sendTime") + "+08:00");

        //（可选）预警事件的预期发生时间
        info.addElement("onset").setText(json.getString("forecastTime") + "+08:00");

        //（可选）预警事件的失效时间
        info.addElement("expires").setText(json.getString("invalidTime") + "+08:00");

        //（必选）系统中预警信息的签发员	系统中签发本条预警信息的发布单位签发员
        info.addElement("senderName").setText(json.getString("organizationName") + json.getString("employeeName"));

        //（必选）预警信息的标题
        // 当<msgType>字段值为Alert或Update时，<Headline>字段格式为： <sender>+“发布”+<eventType>+<severity> 例如：浙江省气象局发布台风黄色预警
        // 当<msgType>字段值为Cancel时，       <Headline>字段格式为： <sender>+“解除”+<eventType>+<severity> 例如：浙江省气象局解除台风黄色预警
        //（标题中级别只显示颜色，界面上下拉框选择时显示红色预警（I级/特别重大）
        info.addElement("headline").setText(getHeadline(
                json.getString("organizationName"),json.getString("disasterName"), json.getInteger("disasterColor"))
        );

        //（必选）预警信息正文
        info.addElement("description").setText(content);

        //（可选）对建议采取措施的描述
        info.addElement("instruction").setText(json.getString("instruction"));

        //（可选）预警信息内容中包含的网址信息	预警信息内容中包含的网址信息，提醒受众可通过网址查看预警详细信息
        info.addElement("web").setText("");

        //（可选）指向与本条预警信息<info>元素相应的一个补充文件，可以使图像或者声音等文件
        appendResource(info, json);

        // （可选）拼接地区节点，可以存在多个
        appendArea(info, json);

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        String fileName = getXMLFileName(
                json.getString("organizationCode"),
                json.getString("sendTime"),
                json.getString("disasterCode"),
                json.getString("disasterLevel"),
                json.getString("warnType")
        );
        String pathName = saveUrl + "/" + fileName;
        try {

            File file = new File(pathName);
            // 创建XMLWriter对象
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            //设置不自动进行转义
            writer.setEscapeText(false);
            // 生成XML文件
            writer.write(document);
            //关闭XMLWriter对象
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathName;
    }



    public static void main(String[] args) {

        String str = "{\"msg\":\"发布成功\",\"msgType\":\"Alert\",\"warnType\":\"Test\",\"channel\":[{\"channelName\":\"短信\",\"channelId\":\"8d2448858b1511e8b73168f7285847c8\",\"channelCode\":\"SMS\"},{\"channelName\":\"大喇叭\",\"channelId\":\"a142bd608b1511e8b73168f7285847c8\",\"channelCode\":\"SPEAKER\"},{\"channelName\":\"广播\",\"channelId\":\"f7ce05fb8b1511e8b73168f7285847c8\",\"channelCode\":\"BROADCAST\"}],\"editTime\":\"2018-08-21 17:51:27\",\"invalidTime\":\"2018-08-21 17:51:27\",\"disasterName\":\"暴雨\",\"organizationId\":\"312ddd73987811e8a5ed8cec4b81c244\",\"forecastTime\":\"2018-08-21 17:51:27\",\"record\":1,\"scope\":\"Public\",\"id\":\"ea14754ba52511e8bee28cec4b81c244\",\"group\":{\"a142bd608b1511e8b73168f7285847c8\":[{\"id\":\"51eedc9d8fe411e885e268f7285847c8\",\"channelId\":\"a142bd608b1511e8b73168f7285847c8\",\"userGroupId\":\"f95e8bef8f2e11e885e268f7285847c8\",\"userGroupName\":\"水利局大喇叭群组\",\"userName\":\"大喇叭1\",\"userCode\":\"343464534asdfa\",\"longitude\":0.0,\"latitude\":0.0,\"altitude\":0.0,\"channelName\":\"大喇叭\"}],\"8d2448858b1511e8b73168f7285847c8\":[{\"id\":\"167210008f0511e885e268f7285847c8\",\"channelId\":\"8d2448858b1511e8b73168f7285847c8\",\"userGroupId\":\"442661118fe411e885e268f7285847c8\",\"userGroupName\":\"水利局短信群组\",\"userName\":\"xincan\",\"userCode\":\"18501377889\",\"longitude\":23.34,\"latitude\":234.23,\"altitude\":23.23,\"channelName\":\"短信\"},{\"id\":\"73d79240a50d11e8bee28cec4b81c244\",\"channelId\":\"8d2448858b1511e8b73168f7285847c8\",\"userGroupId\":\"5d7502ffa50d11e8bee28cec4b81c244\",\"userGroupName\":\"华池县短信测试群组\",\"userName\":\"xincan\",\"userCode\":\"18501377889\",\"longitude\":123.43,\"latitude\":43.34,\"altitude\":12.43,\"channelName\":\"短信\"}]},\"area\":[{\"areaCode\":\"62102300000000\",\"areaId\":\"52db5b81970911e8a5ed8cec4b81c244\",\"areaName\":\"华池县\"}],\"disasterColor\":0,\"content_a142bd608b1511e8b73168f7285847c8_52db5b81970911e8a5ed8cec4b81c244\":\"测试：发布暴雨红色预警（红色）\",\"organizationName\":\"华池县发布中心\",\"disasterId\":\"71cb388d8e6911e885e268f7285847c8\",\"employeeId\":\"ea14754ba52511e8bee28cec4b81c244\",\"content_8d2448858b1511e8b73168f7285847c8_52db5b81970911e8a5ed8cec4b81c244\":\"测试：发布暴雨红色预警（红色）\",\"sendTime\":\"2018-08-21 17:51:27\",\"areaId\":\"52db5b81970911e8a5ed8cec4b81c244\",\"measure\":\"1：请使用滴滴搭船\",\"content_f7ce05fb8b1511e8b73168f7285847c8_52db5b81970911e8a5ed8cec4b81c244\":\"测试：发布暴雨红色预警（红色）\",\"organizationCode\":\"62102300000000\",\"instruction\":\"1：请使用滴滴搭船\",\"disasterCode\":\"11B03\",\"files\":[{\"size\":\"436639\",\"name\":\"5791d82fef635.jpg\",\"id\":\"ea1c11d7a52511e8bee28cec4b81c244\",\"warnEditId\":\"ea14754ba52511e8bee28cec4b81c244\",\"url\":\"/warnFile/5791d82fef635.jpg\"},{\"size\":\"321778\",\"name\":\"5791d8281f685.jpg\",\"id\":\"ea1c12d1a52511e8bee28cec4b81c244\",\"warnEditId\":\"ea14754ba52511e8bee28cec4b81c244\",\"url\":\"/warnFile/5791d8281f685.jpg\"},{\"size\":\"138248\",\"name\":\"psb.jpg\",\"id\":\"ea1c1312a52511e8bee28cec4b81c244\",\"warnEditId\":\"ea14754ba52511e8bee28cec4b81c244\",\"url\":\"/warnFile/psb.jpg\"}],\"disasterLevel\":0,\"user\":{\"f95e8bef8f2e11e885e268f7285847c8\":[{\"userGroupId\":\"f95e8bef8f2e11e885e268f7285847c8\",\"altitude\":0.0,\"latitude\":0.0,\"userGroupName\":\"水利局大喇叭群组\",\"channelName\":\"大喇叭\",\"id\":\"51eedc9d8fe411e885e268f7285847c8\",\"userName\":\"大喇叭1\",\"channelId\":\"a142bd608b1511e8b73168f7285847c8\",\"userCode\":\"343464534asdfa\",\"longitude\":0.0}],\"5d7502ffa50d11e8bee28cec4b81c244\":[{\"userGroupId\":\"442661118fe411e885e268f7285847c8\",\"altitude\":23.23,\"latitude\":234.23,\"userGroupName\":\"水利局短信群组\",\"channelName\":\"短信\",\"id\":\"167210008f0511e885e268f7285847c8\",\"userName\":\"xincan\",\"channelId\":\"8d2448858b1511e8b73168f7285847c8\",\"userCode\":\"18501377889\",\"longitude\":23.34},{\"userGroupId\":\"442661118fe411e885e268f7285847c8\",\"altitude\":234.23,\"latitude\":23.234,\"userGroupName\":\"水利局短信群组\",\"channelName\":\"短信\",\"id\":\"57489da6a46911e8bee28cec4b81c244\",\"userName\":\"姜新灿\",\"channelId\":\"8d2448858b1511e8b73168f7285847c8\",\"userCode\":\"18501377889\",\"longitude\":234.234},{\"userGroupId\":\"5d7502ffa50d11e8bee28cec4b81c244\",\"altitude\":12.43,\"latitude\":43.34,\"userGroupName\":\"华池县短信测试群组\",\"channelName\":\"短信\",\"id\":\"73d79240a50d11e8bee28cec4b81c244\",\"userName\":\"xincan\",\"channelId\":\"8d2448858b1511e8b73168f7285847c8\",\"userCode\":\"18501377889\",\"longitude\":123.43}],\"442661118fe411e885e268f7285847c8\":[{\"userGroupId\":\"442661118fe411e885e268f7285847c8\",\"altitude\":23.23,\"latitude\":234.23,\"userGroupName\":\"水利局短信群组\",\"channelName\":\"短信\",\"id\":\"167210008f0511e885e268f7285847c8\",\"userName\":\"xincan\",\"channelId\":\"8d2448858b1511e8b73168f7285847c8\",\"userCode\":\"18501377889\",\"longitude\":23.34},{\"userGroupId\":\"442661118fe411e885e268f7285847c8\",\"altitude\":234.23,\"latitude\":23.234,\"userGroupName\":\"水利局短信群组\",\"channelName\":\"短信\",\"id\":\"57489da6a46911e8bee28cec4b81c244\",\"userName\":\"姜新灿\",\"channelId\":\"8d2448858b1511e8b73168f7285847c8\",\"userCode\":\"18501377889\",\"longitude\":234.234},{\"userGroupId\":\"5d7502ffa50d11e8bee28cec4b81c244\",\"altitude\":12.43,\"latitude\":43.34,\"userGroupName\":\"华池县短信测试群组\",\"channelName\":\"短信\",\"id\":\"73d79240a50d11e8bee28cec4b81c244\",\"userName\":\"xincan\",\"channelId\":\"8d2448858b1511e8b73168f7285847c8\",\"userCode\":\"18501377889\",\"longitude\":123.43}]},\"status\":4}";

        JSONObject param = JSONObject.parseObject(str);

        setRecordXML(param, "d:/");

    }



}
