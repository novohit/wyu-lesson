package com.wyu.wyulesson.exception;

import com.wyu.wyulesson.enums.ResultEnum;
import com.wyu.wyulesson.vo.ResultVO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * @author zwx
 * @date 2022-06-27 17:08
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理未知异常 都是无意义的 不需要提示message 返回模糊信息或记录日志
     *
     * @param request
     * @param e
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO exceptionHandler(HttpServletRequest request, Exception e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        e.printStackTrace();
        return ResultVO.error(ResultEnum.ERROR.getCode(), ResultEnum.ERROR.getDesc());
    }

    /**
     * 处理已知异常
     * 这里要灵活地处理响应的http状态码 不能写死500 而是返回e里的httpStatus
     * 所以不用@ResponseBody注解 自己设置返回的响应体，所以要设置比较多的类型
     *
     * @param request
     * @param e
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResultVO> httpExceptionHandler(HttpServletRequest request, ForbiddenException e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();

        ResultVO unifyResponse = new ResultVO(e.getCode(), e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus httpStatus = HttpStatus.resolve(e.getHttpStatusCode());

        return new ResponseEntity<>(unifyResponse, headers, httpStatus);
    }


    /**
     * @param request
     * @param e
     * @return
     * @RequestBody注解、Java bean中参数错误产生的异常、表单(Content-Type: application/json、Content-Type: application/xml)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST) // 参数错误 固定返回400
    public ResultVO methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();

        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String errorMsg = this.formatAllErrorMessages(errors);
        return ResultVO.error(ResultEnum.PARAM_ERROR.getCode(), errorMsg);
    }

    /**
     * 非Java bean(如路径参数等)参数错误产生的异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST) // 参数错误 固定返回400
    public ResultVO constraintViolationExceptionHandler(HttpServletRequest request, ConstraintViolationException e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();

        // ConstraintViolationException自带的getMessage()也是可以用的，如果对错误信息没有严格的格式要求可以不用通过这种循环来自定义拼接
        StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation<?> error : e.getConstraintViolations()) {
            errorMsg.append(error.getMessage()).append(";");
        }

        return ResultVO.error(ResultEnum.PARAM_ERROR.getCode(), errorMsg.toString());
    }

    /**
     * Java bean 、表单(Content-Type: multipart/form-data)参数错误产生的异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST) // 参数错误 固定返回400
    public ResultVO bindExceptionExceptionHandler(HttpServletRequest request, BindException e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        String errorMsg = formatAllErrorMessages(e.getAllErrors());
        return ResultVO.error(ResultEnum.PARAM_ERROR.getCode(), errorMsg);
    }


    /**
     * 拼接所有参数错误信息
     *
     * @param errors
     * @return
     */
    private String formatAllErrorMessages(List<ObjectError> errors) {
        StringBuilder errorMsg = new StringBuilder();
        errors.forEach(objectError ->
                errorMsg.append(objectError.getDefaultMessage()).append(";")
        );
        return errorMsg.toString();
    }
}
