package com.xincan.utils.xml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Attribute;
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


    public static void setRecordXML(JSONObject json){

        // Document
        Document document = DocumentHelper.createDocument();

        // 创建一级子节点
        Element root = document.addElement("alert");
        root.addNamespace("xmlns", "http://com.icss.amp");

        // （必选）预警消息唯一标识
        root.addElement("identifier").setText("33000041600000_20121025091026");

        // （必选）预警信息发布单位名称
        root.addElement("sender").setText("浙江省气象局");

        // （必选）预警信息发布单位编码
        root.addElement("senderCode").setText("33000041600000");

        // （可选）发布单位手机
        root.addElement("phone").setText("18501377889");

        // （必选）预警信息发布时间
        root.addElement("sendTime").setText("2012-10-25 09:15:58+08:00");

        // （必选）预警信息类型：[Actual（实际）,Exercise（演练）,Test（测试）,Draft（草稿）],目前取值仅使用“Actual”和“Test”，其中 “Test”可用于发布测试预警， Exercise和Draft暂不使用
        root.addElement("status").setText("Actual");

        // （必选）预警信息状态：[Alert（首次）,Update（更新）,Cancel（解除）,Ack（确认）,Error（错误）]，目前只采用“Alert”“Update”“Cancel”三个枚举值，其余枚举值保留，暂不使用。
        root.addElement("msgType").setText("Alert");

        // （必选）发布范围：[Public（公开）,Restricted（限制权限）,Private（特定地址）],固定使用“Public”值，其余两个枚举值保留，暂不使用。
        root.addElement("scope").setText("Public");

        // （必选）预警信息的发布手段和发布对象
        Element code = root.addElement("code");

        // （必选）消息主体
        Element method = code.addElement("method");

        // （必选）发布手段编码： 如短信的是：SMS
        method.addElement("methodName").setText("SMS");

        // （必选）使用该手段发布的发布内容（若为空，则发布手段到<description>字段取完整的预警信息内容）
        method.addElement("message").setText("浙江省气象局于2012年10月25日09时发布台风蓝色预警");

        // 使用该手段发布的对象/对象群组ID
        method.addElement("audienceGrp").setText("G01002,G02003");

        // （可选）使用该手段发布的单独对象
        method.addElement("audiencePrt").setText("");

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

        // （必选）预警事件类型编码：	取值见《国家应急平台体系信息资源分类与编码规范》
        info.addElement("eventType").setText("11B01");

        // （必选）限定的枚举值，预警事件的紧急程度：[Immediate(立即行动), Expected(准备行动), Future(等待行动), Past(已过去), Unknown(未知)], 默认"Unknown"，其它枚举值保留，暂不使用
        info.addElement("urgency").setText("Unknown");

        // （必选）限定的枚举值	预警事件的严重程度：[Red(红色预警/I级/特别重大), Orange(橙色预警/II级/重大), Yellow(黄色预警/III/较大), Blue(蓝色预警/IV/一般), Unknown(未知,9)]
        info.addElement("severity").setText("Blue");

        //（必选）限定的枚举值	预警事件发生的可能程度	：[Observed(确定发生)，VeryLikely(非常可能)，Likely(可能发生)，Unlikely(不大可能)，Unknown(未知)]，默认Unknown，其它枚举值保留，暂不使用。
        info.addElement("certainty").setText("Unknown");

        //（必选）	预警信息生效时间，预警信息在系统中的签发时间
        info.addElement("effective").setText("2012-10-25 09:15:58+08:00");

        //（可选）预警事件的预期发生时间
        info.addElement("onset").setText("2012-10-25 09:15:58+08:00");

        //（可选）预警事件的失效时间
        info.addElement("expires").setText("2012-10-25 09:15:58+08:00");

        //（必选）系统中预警信息的签发员	系统中签发本条预警信息的发布单位签发员
        info.addElement("senderName").setText("浙江省气象局李明");

        //（必选）预警信息的标题
        // 当<msgType>字段值为Alert或Update时，<Headline>字段格式为： <sender>+“发布”+<eventType>+<severity> 例如：浙江省气象局发布台风黄色预警
        // 当<msgType>字段值为Cancel时，       <Headline>字段格式为： <sender>+“解除”+<eventType>+<severity> 例如：浙江省气象局解除台风黄色预警
        //（标题中级别只显示颜色，界面上下拉框选择时显示红色预警（I级/特别重大）
        info.addElement("headline").setText("浙江省气象局发布台风蓝色预警");

        //（必选）预警信息正文
        info.addElement("description").setText("24小时内可能或者已经受热带气旋影响,沿海或者陆地平均风力达6级以上，或者阵风8级以上并可能持续。");

        //（可选）对建议采取措施的描述
        info.addElement("instruction")
        .setText("1、政府及相关部门按照职责做好防台风准备工作；2、停止露天集体活动和高空等户外危险作业；3、相关水域水上作业和过往船舶采取积极的应对措施，如回港避风或者绕道航行等；4、加固门窗、围板、棚架、广告牌等易被风吹动的搭建物,切断危险的室外电源。");

        //（可选）预警信息内容中包含的网址信息	预警信息内容中包含的网址信息，提醒受众可通过网址查看预警详细信息
        info.addElement("web").setText("http://www.cma.gov.cn/qxxw/xw/201010/t20101023_80172.html");

        //（可选）	包含附件的子元素
        //1）指向与本条预警信息<info>元素相应的一个补充文件，可以使图像或者声音等文件。
        //2）可存在多个。
        Element resource = info.addElement("resource");

        //（必选）附件文件名	系统根据用户上传的附件自动生成的与预警信息文件名匹配的附件文件名，参见第8节预警信息附件文件命名。
        resource.addElement("resourceDesc").setText("33000041600000_20121025091026_11B01_BLUE_ALERT_O_01.jpg");

        //（必选）附件文件大小 单位为byte由系统自动生成。
        resource.addElement("size").setText("84k");

        //（可选）附件内容的Hash码	采用SHA-1算法，参照FIPS 180-2。 用于判断附件是否被篡改。
        //SHA-1规范URL：//http://en.wikipedia.org/wiki/SHA-1。 默认为空。
        resource.addElement("digest").setText("");

        //（可选）包含影响区域和发布区域的子元素	，可以存在多个，目标区域为所有描述区域的并集。
        Element area = info.addElement("area");

        //（必选）影响区域的文本描述	对突发事件影响区域的文字描述
        area.addElement("areaDesc").setText("浙江省北部");

        //（可选）用多边形表示预警信息的发布区域，值为多个坐标点对，用空格分隔；
        // 描述多边形时。首尾为同一个点，为封闭图形；描述线状元素时，首尾不封闭。
        area.addElement("polygon").setText("");

        //（可选）用圆形表示预警信息的发布区域，以圆心点和半径表示，值为坐标点对和半径，用空格分隔；点状元素，可用半径为0的circle描述；半径单位为米。
        area.addElement("circle").setText("");

        //（必选）预警信息发布区域的行政区划代码	用行政区划代码表示预警信息的发布区域，多个行政区划代码之间用“，”分隔，行政区划代码参见《国家突发事件预警信息发布系统预警信息地域编码规范》
        area.addElement("geocode").setText("330205004001,330205004002,330205004003,330205004004,330205004005,330205004006,330205004007");

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");

        try {
            File file = new File("d:/aaa.xml");
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

    }



    public static void main(String[] args) {

        String str = "{'organizationName':'华池县水利局','disasterName':'暴雨','disasterId':'71cb388d8e6911e885e268f7285847c8','disasterColor':'0','disasterLevel':'0','title':'华池县水利局发布暴雨红色[Ⅰ级/特别重大]预警','warnType':'Test','editTime':'2018-08-16 14:26:14','forecastTime':'','invalidTime':'','record':'1','measure':'1：请使用滴滴搭船','instruction':'1：请使用滴滴搭船','flow':'0,1,2,3,4','content_8d2448858b1511e8b73168f7285847c8_52db5b81970911e8a5ed8cec4b81c244':'测试：发布暴雨红色预警（红色）','content_a142bd608b1511e8b73168f7285847c8_52db5b81970911e8a5ed8cec4b81c244':'测试：发布暴雨红色预警（红色）','content_f7ce05fb8b1511e8b73168f7285847c8_52db5b81970911e8a5ed8cec4b81c244':'测试：发布暴雨红色预警（红色）','advice':'您好：华池县水利局发布暴雨红色[Ⅰ级/特别重大]预警请您处理','status':'0','channel':[{'channelName':'短信','channelId':'8d2448858b1511e8b73168f7285847c8'},{'channelName':'大喇叭','channelId':'a142bd608b1511e8b73168f7285847c8'},{'channelName':'广播','channelId':'f7ce05fb8b1511e8b73168f7285847c8'}],'area':[{'areaId':'52db5b81970911e8a5ed8cec4b81c244','areaName':'华池县'}],'group':{'f7ce05fb8b1511e8b73168f7285847c8':[],'a142bd608b1511e8b73168f7285847c8':[{'userGroupId':'8b1471f49a0611e8a90d8cec4b81c244','userGroupName':'开封市水利局喇叭群组(0)'}],'8d2448858b1511e8b73168f7285847c8':[{'userGroupId':'78c888d89a0611e8a90d8cec4b81c244','userGroupName':'开封市水利局短信群组(0)'}]},'employeeId':'8d362798956111e8a49f8cec4b81c244','employeeName':'华池县水利局人员','areaId':'52db5b81970911e8a5ed8cec4b81c244','areaName':'华池县','organizationId':'ec936abf987811e8a5ed8cec4b81c244','files':'[\\'/warnFile/11-广播.png\\',\\'/warnFile/09-北斗卫星.png\\']'}";

        JSONObject param = JSONObject.parseObject(str);
        // 获取渠道（将字符串转换为JSONArray数组对象）同key值数据覆盖
        JSONArray channel =  JSONArray.parseArray(param.getString("channel"));
        param.put("channel", channel);

        // 获取地区（将字符串转换为JSONArray数组对象）同key值数据覆盖
        JSONArray area = JSONArray.parseArray(param.getString("area"));
        param.put("area",area);

        // 获取群组（将字符串转换为JSONArray数组对象）同key值数据覆盖
        JSONObject group = JSONObject.parseObject(param.getString("group"));
        param.put("group",group);

        setRecordXML(param);

    }



}
