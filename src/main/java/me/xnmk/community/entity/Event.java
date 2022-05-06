package me.xnmk.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:xnmk_zhan
 * @create:2022-05-05 15:13
 * @Description: 事件
 */
public class Event {

    // 事件类型
    private String topic;

    // 事件触发人
    private int userId;

    // 事件发生实体
    private int entityType;
    private int entityId;

    // 实体作者是谁
    private int entityUserId;

    // 扩展
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
