package org.hisoka.common.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public class BusinessException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6971716908203238516L;

    private String code;

    private String description;

    /**
     * 自定义属性
     */
    private Map<String, Object> resultMap = new HashMap<String, Object>();

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
        this.description = this.getMessage();
    }

    public BusinessException(Throwable cause) {
        super(cause);
        this.description = this.getMessage();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.description = this.getMessage();
    }

    public BusinessException(String code, String description) {
        super(description);
        this.code = code;
        this.description = description;
    }

    public BusinessException(String message, Map<String, Object> resultMap) {
        super(message);
        this.resultMap = resultMap;
        this.description = this.getMessage();
    }

    public BusinessException(String code, String description, Throwable cause) {
        super(description, cause);
        this.code = code;
        this.description = description;
    }

    public BusinessException(String code, String description, Map<String, Object> resultMap) {
        super(description);
        this.code = code;
        this.description = description;
        this.resultMap = resultMap;
    }

    public BusinessException(String message, Map<String, Object> resultMap, Throwable cause) {
        super(message, cause);
        this.resultMap = resultMap;
        this.description = this.getMessage();
    }

    public BusinessException(String code, String description, Map<String, Object> resultMap, Throwable cause) {
        super(description, cause);
        this.code = code;
        this.description = description;
        this.resultMap = resultMap;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    @Override
    public String toString() {
        return "BusinessException [code=" + code + ", description=" + description + ", resultMap=" + resultMap + "]";
    }

}