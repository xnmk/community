package me.xnmk.community.vo.param;

/**
 * @author:xnmk_zhan
 * @create:2022-04-14 21:12
 * @Description: 封装分页相关信息
 */
public class PageParams {

    // 当前页码
    private int current = 1;

    // 显示上限
    private int limit = 10;

    // 页的起始行
    // private int offset = 0;

    // 数据总数
    private int rows;

    // 查询路径（用于复用分页路径）
    private String path;


    public PageParams() {
    }

    public PageParams(int current, int limit, int rows, String path) {
        this.current = current;
        this.limit = limit;
        this.rows = rows;
        this.path = path;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取总页数
     * @return int
     */
    public int getTotal(){
        if (rows % limit == 0){
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     * @return int
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页码
     * @return int
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }

    /**
     * 获得页的起始行
     * @return int
     */
    public int getOffset() {
        return (current - 1) * limit;
    }
}
