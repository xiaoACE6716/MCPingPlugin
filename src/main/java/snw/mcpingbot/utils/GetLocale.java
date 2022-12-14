package snw.mcpingbot.utils;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import snw.mcpingbot.Main;

import java.util.logging.Logger;

public class GetLocale {

    private static final String apiUrl = "http://ip-api.com/json/?lang=zh-CN&fields=status,message,country,regionName,city,query,continent";

    public static String getRegion(){

        String result = null;
        try {
            String httpResult = HttpUtil.get(apiUrl);
            StringBuilder rawJSON = new StringBuilder(httpResult);
            JSON infoJSON = JSONUtil.parseObj(rawJSON);

            Object continent = infoJSON.getByPath("continent");
            Object country = infoJSON.getByPath("country");
            Object regionName = infoJSON.getByPath("regionName");
            Object city = infoJSON.getByPath("city");

            result = continent.toString() + country.toString() + regionName.toString() + city.toString();
            return result;
        }
        catch (HttpException e){
            result = "未知地区";
            Main.getInstance().getLogger().error("连接ip-api.com失败");
            Main.getInstance().getLogger().debug("连接ip-api.com失败",e);
        }
            return result;
    }


}
