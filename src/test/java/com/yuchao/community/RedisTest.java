package com.yuchao.community;

import org.apache.tomcat.util.net.openssl.OpenSSLUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 蒙宇潮
 * @create 2022-10-20  10:04
 */
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String key = "test:count";
        redisTemplate.opsForValue().set(key, 1);

        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println(redisTemplate.opsForValue().increment(key));
        System.out.println(redisTemplate.opsForValue().decrement(key));
        System.out.println(redisTemplate.opsForValue().increment(key, 100));
    }

    @Test
    public void testHashes() {
        String key = "test:student";

        redisTemplate.opsForHash().put(key, "id", 1);
        redisTemplate.opsForHash().put(key, "name", "yuchao");
        HashMap<String, String> map = new HashMap<>();
        map.put("email", "123@163.com");
        map.put("phone", "131");
        redisTemplate.opsForHash().putAll(key, map);

        System.out.println(redisTemplate.opsForHash().get(key, "name"));
        System.out.println(redisTemplate.opsForHash().get(key, "email"));
    }

    @Test
    public void testList() {
        String key = "test:ids";

        redisTemplate.opsForList().leftPush(key, 101);
        redisTemplate.opsForList().leftPush(key, 102);
        redisTemplate.opsForList().rightPush(key, 100);

        System.out.println(redisTemplate.opsForList().size(key));
        System.out.println(redisTemplate.opsForList().range(key, 0, 2));
        System.out.println(redisTemplate.opsForList().index(key, 1));

        System.out.println(redisTemplate.opsForList().leftPop(key));
        System.out.println(redisTemplate.opsForList().rightPop(key));
        System.out.println(redisTemplate.opsForList().rightPop(key));
    }


    @Test
    public void testSet() {
        String key = "test:teacher";

        redisTemplate.opsForSet().add(key, "刘备", "关羽", "赵云");
        System.out.println(redisTemplate.opsForSet().size(key));
        System.out.println(redisTemplate.opsForSet().pop(key));
        System.out.println(redisTemplate.opsForSet().members(key));
        System.out.println(redisTemplate.opsForSet().isMember(key, "刘备"));
        redisTemplate.opsForSet().remove("赵云");

    }

    @Test
    public void sortedSets() {
        String key = "test:teacher";

        redisTemplate.opsForZSet().add(key, "刘备", 90);
        redisTemplate.opsForZSet().add(key, "张飞", 80);
        redisTemplate.opsForZSet().add(key, "关于", 70);

        System.out.println(redisTemplate.opsForZSet().zCard(key));
        System.out.println(redisTemplate.opsForZSet().score(key, "刘备"));

        System.out.println(redisTemplate.opsForZSet().score(key, "刘备aa"));
        redisTemplate.opsForZSet().remove("关于");
        //获取倒序排列的索引值
        System.out.println(redisTemplate.opsForZSet().reverseRank(key, "关于"));
        //返回倒序指定区间元素 set redis自己实现了
        Set set = redisTemplate.opsForZSet().reverseRange(key, 0, 2);
        System.out.println();

    }

    @Test
    public void testKeys() {
        System.out.println(redisTemplate.keys("*"));

        redisTemplate.delete("test:teacher");

        System.out.println(redisTemplate.hasKey("test:teacher"));
        System.out.println(redisTemplate.hasKey("test:student"));

        redisTemplate.expire("test:student", 10, TimeUnit.MINUTES);

    }

    // 批量发送命令,节约网络开销.
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    //编程式事务
    @Test
    public void testTransaction() {
        Object redult = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String key = "test:tx";
                //开启事务
                redisOperations.multi();
                redisOperations.opsForSet().add(key, "zhansan");
                redisOperations.opsForSet().add(key, "yuchao");
                redisOperations.opsForValue().set("aa","aa");
                redisOperations.opsForValue().increment("aa");//出错不影响执行
                redisOperations.opsForSet().add(key, "wangwu");
                //不要在事务中进行查询，不会立即返回结果
                System.out.println(redisOperations.opsForSet().members(key));

                //提交事务
                return redisOperations.exec();
            }
        });
        System.out.println(redult);
    }


}
