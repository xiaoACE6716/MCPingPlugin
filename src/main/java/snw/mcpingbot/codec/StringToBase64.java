package snw.mcpingbot.codec;

import cn.hutool.core.codec.Base64;
import snw.mcpingbot.Main;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

public class StringToBase64 {

    public static File base64StringToFile(String base64String) {

        int index = base64String.indexOf("base64,")+7;
        String serverIcon = base64String.substring(index);

        //创建文件
        File file;
        file = new File(Main.getInstance().getDataFolder(), randomString() + ".png");
        Base64.decodeToFile(serverIcon,file);
        return file;
    }


    private static String randomString() {
        char[] chars = "abcdef0123456789".toCharArray(); // I like 16 radix style.
        int size = 15; // actually, it is length
        StringBuilder builder = new StringBuilder();
        while (size-- > 0) {
            builder.append(chars[new SecureRandom().nextInt(chars.length)]);
        }
        return builder.toString();
    }
}


