package com.yuchao.community.entity;



/**
 * @author 蒙宇潮
 * @create 2022-09-21  17:42
 */

public class Page {

    //当前页码
    private Integer current = 1;
    private Integer limit = 10;
    private Integer rows;
    private String path;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        if (rows >= 0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取总页数
     *
     * @return int
     * @author yuchao
     * @date 2022/9/21 17:49
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页的偏移量
     *
     * @return int
     * @author yuchao
     * @date 2022/9/21 17:52
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取起始页码
     *
     * @return int
     * @author yuchao
     * @date 2022/9/21 17:55
     */
    public int getFrom() {
        int from = current - 2;
        return from <= 0 ? 1 : current - 2;
    }

    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }
}
