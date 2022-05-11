package me.xnmk.community.enumeration;

/**
 * @author:xnmk_zhan
 * @create:2022-05-10 22:16
 * @Description: 用户权限
 */
public enum UserPermissions {

    /**
     * 权限：用户
     */
    AUTHORITY_USER("user"),
    /**
     * 权限：管理员
     */
    AUTHORITY_ADMIN("admin"),
    /**
     * 权限：版主
     */
    AUTHORITY_MODERATOR("moderator"),
    ;

    private final String code;

    UserPermissions(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
