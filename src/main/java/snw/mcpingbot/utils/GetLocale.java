package snw.mcpingbot.utils;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import snw.mcpingbot.Main;

public class GetLocale {

    private static final String apiUrl = "http://ip-api.com/json/?lang=zh-CN&fields=status,message,country,regionName,city,query,continent";

    public static Object regionResult = null;

    public static Object getRegion(){

        if (regionResult == null) {
            try {
                String httpResult = HttpUtil.get(apiUrl);
                StringBuilder rawJSON = new StringBuilder(httpResult);
                JSON infoJSON = JSONUtil.parseObj(rawJSON);

                Object continent = infoJSON.getByPath("continent");
                Object country = infoJSON.getByPath("country");
                Object regionName = infoJSON.getByPath("regionName");
                Object city = infoJSON.getByPath("city");

                regionResult = continent.toString() + country.toString() + regionName.toString() + city.toString();
                return regionResult;
            } catch (HttpException e) {
                regionResult = "未知地区";
                Main.getInstance().getLogger().error("连接ip-api.com失败");
                Main.getInstance().getLogger().debug("连接ip-api.com失败", e);
            }catch (Exception e){
                regionResult = "未知地区";
                Main.getInstance().getLogger().error("我也不知道发生了什么错误，丢到debug.log里吧");
                Main.getInstance().getLogger().debug("我也不知道发生了什么错误，丢到debug.log里吧", e);
            }
        }
            return regionResult;

    }


}
