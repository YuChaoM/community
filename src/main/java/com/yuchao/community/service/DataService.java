package com.yuchao.community.service;

import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import com.yuchao.community.util.RedisKeyUtil;
import javafx.scene.input.DataFormat;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 蒙宇潮
 * @create 2022-11-10  16:46
 */
@Service
public class DataService {

    @Resource
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    public long calculateUV(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        ArrayList<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)) {
            list.add(RedisKeyUtil.getUVKey(df.format(calendar.getTime())));
            calendar.add(Calendar.DATE, 1);
        }
        //合并数据
        String redisKey = RedisKeyUtil.getUVKey(df.format(startDate), df.format(endDate));
        redisTemplate.opsForHyperLogLog().union(redisKey, list.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //记录活跃用户
    public void recordDAU(int userId) {
        String daUkey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(daUkey, userId, true);
    }


    // 统计指定日期范围内的DAU
    public long calculateDAU(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        ArrayList<byte[]> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)) {
            list.add(RedisKeyUtil.getDAUKey(df.format(calendar.getTime())).getBytes());
            calendar.add(Calendar.DATE, 1);
        }
        //进行or运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String dauKey = RedisKeyUtil.getDAUKey(df.format(startDate), df.format(endDate));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        dauKey.getBytes(), list.toArray(new byte[0][0]));
                return connection.bitCount(dauKey.getBytes());
            }
        });

    }
}
