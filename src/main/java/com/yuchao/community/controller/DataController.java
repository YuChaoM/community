package com.yuchao.community.controller;

import com.yuchao.community.service.DataService;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;

/**
 * @author 蒙宇潮
 * @create 2022-11-10  18:09
 */
@Controller
public class DataController implements CommunityConstant {

    @Resource
    private DataService dataService;

    @GetMapping("/data")
    public String getDataPage() {
        return "site/admin/data";
    }

    @PostMapping("/data/uv")
    @ResponseBody
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        long res = dataService.calculateUV(start, end);
        HashMap<String, Object> map = new HashMap<>();
        map.put("res", res);
        return CommunityUtil.getJSONString(SUCCESS, null, map);
    }

    @PostMapping("/data/dau")
    @ResponseBody
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        long res = dataService.calculateDAU(start, end);
        HashMap<String, Object> map = new HashMap<>();
        map.put("res", res);
        return CommunityUtil.getJSONString(SUCCESS, null, map);
    }
}
