package com.yuchao.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @author 蒙宇潮
 * @create 2022-10-04  17:27
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    private Trie root = new Trie();

    /**
     * 读取文件的敏感词
     *
     * @param
     * @return
     * @date 2022/10/4 17:32
     */
    @PostConstruct
    public void init() {

        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String word;
            while ((word = reader.readLine()) != null) {
                //添加敏感词进前缀树
                root.insert(word);
            }
        } catch (Exception e) {
            logger.error("敏感词文件加载失败:" + e.getMessage());
        }
    }


    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        int left = 0;//指向敏感词的开头
        int right = 0;
        Trie tempNode = root;
        char[] chars = text.toCharArray();
        StringBuilder sb = new StringBuilder();
        //跳过符号
        while (left < chars.length) {
            char c = chars[right];
            //跳过符号
            if (isSymbol(c)) {
                //敏感词前的符号
                if (tempNode == root) {
                    sb.append(c);
                    left++;
                }
                //敏感词中间的符号直接忽略
                right++;
                continue;
            }
            tempNode = tempNode.children.get(c);
            if (tempNode == null) {
                //是敏感词的前缀，但是到结束也拼不出一个敏感词
                if(root.startsWith(text.substring(left,right+1))){
                    sb.append(chars[left]);
                    right = ++left;
                }else {
                    //不是敏感词的前缀
                    sb.append(chars,left,right-left+1);
                    left = ++right;
                }

                //重新指向根节点
                tempNode = root;
            } else if (tempNode.isEnd) {
                //找到一个敏感词
                sb.append(REPLACEMENT);
                //接着找下一个
                left = ++right;
                tempNode = root;
            } else {
                //检查下一个字符
                if (right< chars.length-1)
                    right++;
            }
        }

        return sb.toString();
    }

    private boolean isSymbol(char c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    private class Trie {
        private HashMap<Character, Trie> children;
        private boolean isEnd;

        public Trie() {
            children = new HashMap<>();
            this.isEnd = false;
        }

        public void insert(String word) {
            Trie node = this;
            for (char c : word.toCharArray()) {
                if (!node.children.containsKey(c)) {
                    node.children.put(c, new Trie());
                }
                node = node.children.get(c);
            }
            node.isEnd = true;
        }
        public boolean startsWith(String prefix) {
            Trie node = this;
            for (int i = 0; i < prefix.length(); i++) {
                char ch = prefix.charAt(i);
                if (node.children.get(ch) == null) {
                    return false;
                }
                node = node.children.get(ch);
            }
            return true;
        }
    }
}
