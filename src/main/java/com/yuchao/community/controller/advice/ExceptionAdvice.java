package com.yuchao.community.controller.advice;

import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 蒙宇潮
 * @create 2022-10-14  10:42
 */
@ControllerAdvice(annotations = ControllerAdvice.class)
public class ExceptionAdvice implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handelException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        //记录日志
        logger.error("服务器发生异常:" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        //如果是异步请求返回json
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(SERVER_ERROR, "服务器异常!"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }

    }
}
