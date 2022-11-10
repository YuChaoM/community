package com.yuchao.community;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;

import java.util.HashMap;
import java.util.Random;
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

    //    统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog() {
        String redisKey = "test:hll:01";

        //十万个不重复的
        for (int i = 1; i <= 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }
        for (int i = 1; i <= 100000; i++) {
//            int r = (int) (Math.random() * 100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey, new Random().nextInt(100000) + 1);
        }

        //结果应该是10w
        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    /**
     * 可以统计网站的uv 是指通过互联网访问、浏览这个网页的自然人。是指通过互联网访问、浏览这个网页的自然人。
     * pv 用户每1次对网站中的每个网页访问均被记录1个PV
     */
    @Test
    public void testHyperLogLogUnion() {
        String redisKey = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }
        String redisKey2 = "test:hll:03";
        for (int i = 1; i <= 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }

        String redisKey3 = "test:hll:04";
        for (int i = 1; i <= 20000; i++) {;
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }
        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey, redisKey2, redisKey3);

        Long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);//19833,准确的值是2w
    }

    @Test
    public void testBitMap() {
        String redisKey = "test:bm:01";

        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);
        redisTemplate.opsForValue().setBit(redisKey, 8, true);

        //查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,4));

        //统计 ，需要通过执行命令的方式
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.bitCount(redisKey.getBytes());//返回1的个数
            }
        });

        System.out.println(obj);
    }

    //位运算
    @Test
    public void testBitMapOperation() {
        String redisKey1 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey1, 0, true);
        redisTemplate.opsForValue().setBit(redisKey1, 1, true);
        redisTemplate.opsForValue().setBit(redisKey1, 2, true);

        String redisKey2 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);
        redisTemplate.opsForValue().setBit(redisKey2, 3, true);
        redisTemplate.opsForValue().setBit(redisKey2, 4, true);

        String redisKey3 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey3, 4, true);
        redisTemplate.opsForValue().setBit(redisKey3, 5, true);
        redisTemplate.opsForValue().setBit(redisKey3, 6, true);

        String redisKey = "test:bm:03";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), redisKey1.getBytes(), redisKey2.getBytes(), redisKey3.getBytes());
                return redisConnection.bitCount(redisKey.getBytes());

            }
        });
        System.out.println(obj);//or 有一个1，就是1

        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,6));


    }

}
