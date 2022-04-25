package me.xnmk.community.vo.param;

/**
 * @author:xnmk_zhan
 * @create:2022-04-25 15:54
 * @Description: CommentParams
 */
public class CommentParams {

    private int entityType;
    private int entityId;
    private int targetId;
    private String content;

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
