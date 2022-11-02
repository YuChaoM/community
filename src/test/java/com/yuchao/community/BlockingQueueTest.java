package com.yuchao.community;

import com.sun.javafx.image.impl.IntArgb;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author 蒙宇潮
 * @create 2022-10-25  10:14
 */


public class BlockingQueueTest {

    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();

    }

    @Test
    public void test() {

        threeSumClosest(new int[]{-1, 2, 1, -4}, 1);
    }

    public int threeSumClosest(int[] nums, int target) {
        int ans  = 0;
        int pre = Integer.MAX_VALUE;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            int sum = nums[i];
            if(i > 0 && nums[i] == nums[i-1]) continue;
            int j = i + 1;
            int k = nums.length - 1;
            while (j < k) {
                int temp = nums[j] + nums[k];
                if (sum + temp < target) {
                    j++;
                }else if(sum + temp > target) {
                    k--;
                }else {
                    return sum + temp;
                }
                if(pre > Math.abs(sum + temp - target)) {
                    ans = sum + temp;
                    pre = Math.abs(sum + temp - target);
                }
            }
        }
        return ans;
    }
}

class Producer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产了:" + queue.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
       try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费了:" + queue.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}