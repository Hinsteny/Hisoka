package org.hisoka.common.constance;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public enum ResultCode {

    /**
     * 通用的返回码
     */
    COMMON_SUCCESS("J000000", "成功"),
    COMMON_BUSINESS_EXCEPTION("J000997", "业务异常"),
    COMMON_SYSTEM_EXCEPTION("J000998", "系统异常"),
    COMMON_SYSTEM_ERROR("J000999", "系统错误");

    /**
     * 结果码
     */
    private String code;

    /**
     * 描述
     */
    private String description;

    private ResultCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
