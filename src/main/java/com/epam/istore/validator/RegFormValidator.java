package com.epam.istore.validator;


import com.epam.istore.bean.RegFormBean;
import com.epam.istore.captcha.Captcha;
import com.epam.istore.messages.Messages;
import com.epam.istore.service.CaptchaService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.epam.istore.messages.Messages.CAPTCHA_SERVICE;

public class RegFormValidator {
    private static final int MINIMUM_ALLOWED_PASSWORD_LENGTH = 8;
    private static final int MAX_ALLOWED_NAME_LENGTH = 15;
    private Map<String, String> errorMap = new HashMap<String, String>();
    private final static String NAME_REG = "(^[A-z]*$)";
    private final static String EMAIL_REG = "[\\w]+@gmail.com";
    private final static String GENDER_TRUE = "male";
    private final static String GENDER_FALSE = "female";
    private final static Logger LOGGER = Logger.getRootLogger();

    private void validateName(String name, String errorMessage, String key) {
        Pattern pattern = Pattern.compile(NAME_REG, Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(name);
        if (!matcher.find() || name.length() > MAX_ALLOWED_NAME_LENGTH || name.equals("")) {
            LOGGER.error(errorMessage);
            errorMap.put(key, errorMessage);
        }
    }

    private void validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REG);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.find() || email.equals(StringUtils.EMPTY)) {
            LOGGER.error("Email should be written like example.email@gmail.com!");
            errorMap.put(Messages.EMAIL, Messages.INVALID_EMAIL);
        }
    }

    private void validatePassword(String password) {
        if (password.length() < MINIMUM_ALLOWED_PASSWORD_LENGTH || password.equals("")) {
            LOGGER.error("Password should contains more than 8 symbols!");
            errorMap.put(Messages.PASSWORD, Messages.INVALID_PASSWORD);
        }
    }

    private void validateConfirmPassword(String password, String confirmPassword) {
        if (!confirmPassword.equals(password) || confirmPassword.equals("")) {
            LOGGER.error("Passwords don't match");
            errorMap.put(Messages.CONFIRM_PASSWORD, Messages.INVALID_CONFIRM_PASSWORD);
        }
    }

    private void validateGender(String gender) {
        if (!(gender.equals(GENDER_TRUE) || gender.equals(GENDER_FALSE))) {
            LOGGER.error("Invalid gender");
            errorMap.put(Messages.GENDER, Messages.INVALID_GENDER);
        }
    }

    private void validateCaptcha(HttpServletRequest request, RegFormBean regFormBean) {
        CaptchaService captchaService = (CaptchaService) request.getServletContext().getAttribute(CAPTCHA_SERVICE);
        String captchaId = captchaService.getId(request);
        Captcha captcha = captchaService.getCaptchaById(captchaId, request);
        if (captcha == null || captcha.isExpired()) {
            LOGGER.error("Captcha time expired");
            errorMap.put(Messages.CAPTCHA, Messages.CAPTCHA_TIME_EXPIRED);
        } else if (!captcha.getCaptchaNumber().equals(regFormBean.getCaptchaNumbers())) {
            LOGGER.error("Invalid captcha");
            errorMap.put(Messages.CAPTCHA, Messages.INVALID_CAPTCHA);
        }
    }

    public Map<String, String> validate(RegFormBean regFormBean, HttpServletRequest request) {
        errorMap.clear();
        validateName(regFormBean.getName(), Messages.INVALID_NAME, Messages.NAME);
        validateName(regFormBean.getSurname(), Messages.INVALID_SURNAME, Messages.SURNAME);
        validateEmail(regFormBean.getEmail());
        validatePassword(regFormBean.getPassword());
        validateConfirmPassword(regFormBean.getConfirmPassword(), regFormBean.getPassword());
        validateGender(regFormBean.getGender());
        validateCaptcha(request, regFormBean);
        return errorMap;
    }
}
