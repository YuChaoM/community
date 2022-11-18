package com.yuchao.community.controller;

import com.yuchao.community.entity.Event;
import com.yuchao.community.event.EventProducer;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author 蒙宇潮
 * @create 2022-11-15  10:51
 */
@Controller
public class ShareController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);
    @Resource
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @GetMapping("/share")
    @ResponseBody
    public String share(String htmlUrl) {
        //文件名
        String fileName = CommunityUtil.generateUUID();

        HashMap<String, Object> map = new HashMap<>();
        map.put("htmlUrl", htmlUrl);
        map.put("fileName", fileName);
        map.put("suffix", ".png");
        Event event = Event.builder()
                .topic(TOPIC_SHARE)
                .data(map)
                .build();

        //异步执行
        eventProducer.fireEvent(event);

        //返回访问路径
        HashMap<String, Object> url = new HashMap<>();
        url.put("shareUrl", domain + contextPath + "/share/image/" + fileName);
        return CommunityUtil.getJSONString(SUCCESS, null, url);
    }

    //获取长图
    @GetMapping("/share/image/{fileName}")
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response) {

        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        response.setContentType("image/png");
        File file = new File(wkImageStorage + "/" + fileName + ".png");
        try {
            ServletOutputStream os = response.getOutputStream();
            FileInputStream is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = is.read(buffer)) != -1) {
                os.write(buffer,0,b);//读多少写多少
            }
        } catch (IOException e) {
            logger.error("获取长图失败: " + e);
        }


    }
}
