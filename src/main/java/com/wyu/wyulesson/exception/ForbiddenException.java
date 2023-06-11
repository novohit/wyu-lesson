package com.wyu.wyulesson.exception;

import com.wyu.wyulesson.enums.ResultEnum;

/**
 * @author zwx
 * @date 2022-11-24 12:55
 */
public class ForbiddenException extends RuntimeException{
    protected Integer code;
    protected Integer httpStatusCode = 500;

    public Integer getCode() {
        return code;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public ForbiddenException(){

    }

    public ForbiddenException(Integer code, String desc) {
        super(desc);
        this.code = code;
    }

    public ForbiddenException(Integer code, String desc, Integer httpStatusCode) {
        super(desc);
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    public ForbiddenException(ResultEnum resultEnum, Integer httpStatusCode) {
        super(resultEnum.getDesc());
        this.code = resultEnum.getCode();
        this.httpStatusCode = httpStatusCode;
    }

    public ForbiddenException(ResultEnum resultEnum) {
        super(resultEnum.getDesc());
        this.code = resultEnum.getCode();
    }
}
