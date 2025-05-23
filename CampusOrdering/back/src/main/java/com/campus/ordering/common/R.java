package com.campus.ordering.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结果类
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code; // 状态码
    private String msg; // 消息
    private T data; // 数据

    /**
     * 成功返回结果
     */
    public static <T> R<T> success() {
        return success(null);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static <T> R<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param msg  提示信息
     */
    public static <T> R<T> success(T data, String msg) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /**
     * 失败返回结果
     */
    public static <T> R<T> error() {
        return error("操作失败");
    }

    /**
     * 失败返回结果
     *
     * @param msg 提示信息
     */
    public static <T> R<T> error(String msg) {
        return error(500, msg);
    }

    /**
     * 失败返回结果
     *
     * @param code 错误码
     * @param msg  提示信息
     */
    public static <T> R<T> error(Integer code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
} 