package com.yuchao.community;

import java.io.IOException;

/**
 * @author 蒙宇潮
 * @create 2022-11-15  10:35
 */
public class WkTests {

    public static void main(String[] args) {
        String cmd = "D:/app/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com E:/Users/Desktop/community/image/3.png";

        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
