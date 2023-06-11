package com.wyu.wyulesson;

import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author zwx
 * @date 2022-11-23 12:22
 */
@Data
public class LoginDTO {
    @NotBlank(message = "account不能为空")
    private String account;

    @NotBlank(message = "password不能为空")
    private String password;

    @NotBlank(message = "captcha不能为空")
    private String captcha;

    @NotBlank(message = "captchaId不能为空")
    private String captchaId;
}
