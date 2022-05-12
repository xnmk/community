package me.xnmk.community.service;

import java.util.Date;

/**
 * @author:xnmk_zhan
 * @create:2022-05-12 22:42
 * @Description: Data-Service
 */
public interface DataService {

    /**
     * 将指定 IP 计入 UV
     *
     * @param ip ip
     */
    void recordUV(String ip);

    /**
     * 统计指定日期范围的 UV
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return long
     */
    long calculateUV(Date start, Date end);

    /**
     * 将指定用户计入 DAU
     *
     * @param userId 用户 id
     */
    void recordDAU(int userId);

    /**
     * 统计指定日期范围的 DAU
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return long
     */
    long calculateDAU(Date start, Date end);
}
