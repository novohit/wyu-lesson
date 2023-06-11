package com.wyu.wyulesson.enums;


/**
 * @author zwx
 * @date 2022/1/11-21:00
 */

public enum ResultEnum {

    ERROR(-1, "服务端异常"),

    SUCCESS(0, "成功"),

    PARAM_ERROR(3, "参数错误"),

    //1000 用户相关

    REGISTERED_ERROR(1001, "该手机号已经注册"),

    DECODE_ERROR(1002, "密码解密失败"),

    USER_NOT_EXIST(1003, "该用户不存在"),


    PASSWORD_ERROR(1004, "密码错误"),

    TOKEN_EXPIRED(1005, "token已过期"),

    TOKEN_ERROR(1006, "非法token"),

    USERNAME_EXIST(2, "用户名已存在"),



    EMAIL_EXIST(4, "邮箱已存在"),

    NEED_LOGIN(10, "用户未登录，请先登录"),

    USERNAME_OR_PASSWORD_ERROR(11, "用户名或密码错误"),//不能给具体错误 安全为题

    PRODUCT_OFF_SALE_OR_DELETE(12,"商品下架或删除"),

    PRODUCT_NOT_EXIST(13,"商品不存在"),

    OUT_OF_STOCK(14,"库存不足"),

    DELETE_FAIL(15,"删除失败"),

    UPDATE_FAIL(16,"更新失败"),

    SHIPPING_NOT_EXIST(17,"地址不存在"),

    CART_SELECT_IS_EMPTY(18,"请选择商品后再下单"),

    ORDER_NOT_EXIST(19,"订单不存在"),

    ORDER_NOT_CANCEL(20,"该订单不可取消"),

    ORDER_STATUS_ERROR(21,"订单状态错误"),
    ;

    Integer code;

    String desc;

    ResultEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
