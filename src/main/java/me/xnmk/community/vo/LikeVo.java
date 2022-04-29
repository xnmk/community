package me.xnmk.community.vo;

/**
 * @author:xnmk_zhan
 * @create:2022-04-28 10:20
 * @Description: LikeVo
 */
public class LikeVo {

    private long likeCount;

    private int likeStatus;

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }
}
