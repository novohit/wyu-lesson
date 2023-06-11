package com.wyu.wyulesson.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wyu.wyulesson.AesEncrypt;
import com.wyu.wyulesson.LoginDTO;
import com.wyu.wyulesson.exception.ForbiddenException;
import com.wyu.wyulesson.util.uuid.Base64Utils;
import com.wyu.wyulesson.util.uuid.IdUtils;
import com.wyu.wyulesson.util.uuid.RedisCache;
import com.wyu.wyulesson.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Pattern;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zwx
 * @date 2022-11-23 11:28
 */
@RestController
@RequestMapping("/user")
@Validated
@CrossOrigin(allowCredentials = "true", originPatterns = {"http://localhost:8000", "http://116.62.108.144:8080"})
@Slf4j
public class UserController {

    RestTemplate restTemplate = new RestTemplate();
    Map<String, List<String>> cookiesMap = new HashMap<>();

    //@Autowired
    //private RedisCache redisCache;

    @PostMapping("/login")
    public ResultVO test(@RequestBody @Validated LoginDTO loginDTO, @RequestHeader HttpHeaders headers) {
        log.info("登录===========[{}]", JSONObject.toJSONString(loginDTO));
        String url = "https://jxgl.wyu.edu.cn/new/login/";
        //设置Http的Header
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 登录获取Cookie 这里是直接给Cookie，可使用下方的login方法拿到Cookie给入
        // 把上一步获取的cookie，在请求header里面传入
        // 将cookie存入头部
       /* List<String> cookies = redisCache.getCacheObject(loginDTO.getCaptchaId());
        if (CollectionUtils.isEmpty(cookies)) {
            return ResultVO.successByMsg("验证码已过期或不存在");
        }*/
        //设置访问参数
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("account", loginDTO.getAccount());
        params.add("pwd", AesEncrypt.getEncryptPassword(loginDTO.getPassword(), loginDTO.getCaptcha()));
        params.add("verifycode", loginDTO.getCaptcha());

        //设置访问的Entity
        HttpEntity entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = null;
        try {
            //发起一个POST请求
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JSONObject jsonObject = JSON.parseObject(responseEntity.getBody());
            Integer code = (Integer) jsonObject.get("code");
            String msg = (String) jsonObject.get("message");
            //redisCache.deleteObject(loginDTO.getCaptchaId());
            return ResultVO.error(code, msg);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error");
            throw e;
        }
    }


    @GetMapping("/captcha")
    public ResponseEntity<ResultVO> getCaptchaImg() throws IOException {
        log.info("获取验证码===========");
        String url = "https://jxgl.wyu.edu.cn/yzm?d=" + System.currentTimeMillis();
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
        //获取entity中的数据
        System.out.println(responseEntity);
        byte[] body = responseEntity.getBody();
        List<String> cookies = responseEntity.getHeaders().get("Set-Cookie");

        String captchaId = IdUtils.randomUUID();
        //redisCache.setCacheObject(captchaId, cookies, 5, TimeUnit.MINUTES);
        //创建输出流  输出到本地
        //FileOutputStream fileOutputStream = new FileOutputStream(new File("F:\\1.jpg"));
        //fileOutputStream.write(body);
        //关闭流
        //fileOutputStream.close();

        HttpHeaders headers = new HttpHeaders();
        headers.put("Set-Cookie", cookies);
        Map<String, String> map = new HashMap<>();
        map.put("img", Base64Utils.encode(body));
        map.put("captchaId", captchaId);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ResultVO.success(map));
        //return ResultVO.success(map);
    }


    @GetMapping("/courses")
    public ResultVO<CourseVO> getCourses(@RequestParam @Pattern(regexp = "20[0-9][0-9]0[12]", message = "学期非法") String term,
                                         @RequestParam(required = true, defaultValue = "1") @Range(min = 1, max = 20) Integer week,
                                         @RequestHeader HttpHeaders headers2) {
        log.info("获取课程列表===========term：[{}], week：[{}]", term, week);
        String url = "https://jxgl.wyu.edu.cn/xsgrkbcx!getKbRq.action?xnxqdm=" + term + "&zc=" + week;
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, Objects.requireNonNull(headers2.get(HttpHeaders.COOKIE)));
        headers.setOrigin("https://jxgl.wyu.edu.cn");
        headers.add("Referer", "https://jxgl.wyu.edu.cn/login!welcome.action");
        HttpEntity entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String dataStr = responseEntity.getBody();
        if (dataStr.contains("<html>")) {
            throw new ForbiddenException(403, "用户未登录");
        }
        // System.out.println(dataStr);
        List<String> params = getParams(dataStr);
        CourseVO courseVO = getCourseList(params);
        return ResultVO.success(courseVO);
    }


    @GetMapping("/score")
    public ResultVO<Object> getScore(@RequestParam @Pattern(regexp = "20[0-9][0-9]0[12]", message = "学期非法") String term, @RequestHeader HttpHeaders headers2) {
        log.info("获取成绩列表===========term：[{}]", term);
        String url = "https://jxgl.wyu.edu.cn/xskccjxx!getDataList.action";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.put(HttpHeaders.COOKIE, Objects.requireNonNull(headers2.get(HttpHeaders.COOKIE)));
        headers.setOrigin("https://jxgl.wyu.edu.cn");
        headers.add("Referer", "https://jxgl.wyu.edu.cn/login!welcome.action");

        //设置访问参数
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("xnxqdm", term);
        params.add("jhlxdm", "");
        params.add("page", "1");
        params.add("rows", "20");
        params.add("sort", "zcj");
        params.add("order", "desc");

        //设置访问的Entity
        HttpEntity entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String dataStr = responseEntity.getBody();

        if (dataStr.contains("<html>")) {
            throw new ForbiddenException(403, "用户未登录");
        }
        JSONObject jsonObject = JSONObject.parseObject(dataStr);
        Integer total = (Integer) jsonObject.get("total");

        JSONArray dataArray = (JSONArray) jsonObject.get("rows");
        List<Score> scoreList = new ArrayList<>();
        for (Iterator iterator = dataArray.iterator(); iterator.hasNext(); ) {
            JSONObject jsonScore = (JSONObject) iterator.next();
            String username = (String) jsonScore.get("xsxm");
            String studentNum = (String) jsonScore.get("xsbh");
            String term1 = (String) jsonScore.get("xnxqmc");
            String category = (String) jsonScore.get("kcdlmc");
            String subCategory = (String) jsonScore.get("kcflmc");
            String courseName = (String) jsonScore.get("kcmc");
            String studyType = (String) jsonScore.get("xdfsmc");
            String score1 = (String) jsonScore.get("zcj");
            String credit = (String) jsonScore.get("cjjd");
            Score score = new Score(username, studentNum, term1, category, subCategory, courseName, studyType, score1, credit);
            scoreList.add(score);
        }

        List<Score> scores = scoreList.stream()
                .sorted(new Comparator<Score>() {
                    @Override
                    public int compare(Score o1, Score o2) {

                        boolean isScore1Text = isTextScore(o1.getScore());
                        boolean isScore2Text = isTextScore(o2.getScore());

                        if (isScore1Text && isScore2Text) {
                            // Both scores are text scores, keep the original order
                            return 0;
                        } else if (isScore1Text) {
                            // Score 1 is text score, it should be placed last
                            return 1;
                        } else if (isScore2Text) {
                            // Score 2 is text score, it should be placed last
                            return -1;
                        } else {
                            // Both scores are numerical, compare them as integers in descending order
                            int numericScore1 = Integer.parseInt(o1.getScore());
                            int numericScore2 = Integer.parseInt(o2.getScore());
                            return Integer.compare(numericScore2, numericScore1);
                        }
                    }
                }).collect(Collectors.toList());
        return ResultVO.success(new ScoreVO(scores, total));
    }

    private boolean isTextScore(String score) {
        try {
            int i = Integer.parseInt(score);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }


    @GetMapping("/exam")
    public ResultVO<Object> getExam(@RequestParam @Pattern(regexp = "20[0-9][0-9]0[12]", message = "学期非法") String term, @RequestHeader HttpHeaders headers2) {
        log.info("获取考试列表===========term：[{}]", term);
        String url = "https://jxgl.wyu.edu.cn/xsksap!getDataList.action";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.put(HttpHeaders.COOKIE, Objects.requireNonNull(headers2.get(HttpHeaders.COOKIE)));
        headers.setOrigin("https://jxgl.wyu.edu.cn");
        headers.add("Referer", "https://jxgl.wyu.edu.cn/login!welcome.action");

        //设置访问参数
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("xnxqdm", term);
        params.add("jhlxdm", "");
        params.add("page", "1");
        params.add("rows", "20");
        params.add("sort", "zc,xq,jcdm2");
        params.add("order", "asc");

        //设置访问的Entity
        HttpEntity entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String dataStr = responseEntity.getBody();

        if (dataStr.contains("<html>")) {
            throw new ForbiddenException(403, "用户未登录");
        }
        JSONObject jsonObject = JSONObject.parseObject(dataStr);
        Integer total = (Integer) jsonObject.get("total");

        JSONArray dataArray = (JSONArray) jsonObject.get("rows");
        List<Exam> examList = new ArrayList<>();
        for (Iterator iterator = dataArray.iterator(); iterator.hasNext(); ) {
            JSONObject jsonScore = (JSONObject) iterator.next();
            String username = (String) jsonScore.get("xsxm");
            String studentNum = (String) jsonScore.get("xsbh");
            String term1 = (String) jsonScore.get("xnxqdm");
            String teacher = (String) jsonScore.get("jkteaxms");
            String date = (String) jsonScore.get("ksrq");
            String section = (String) jsonScore.get("jcdm2");
            String time = (String) jsonScore.get("kssj");
            String courseName = (String) jsonScore.get("kcmc");
            String location = (String) jsonScore.get("kscdmc");
            String arrange = (String) jsonScore.get("ksaplxmc");
            examList.add(new Exam(term1, teacher, date, section, time, studentNum, username, courseName, location, arrange));
        }

        return ResultVO.success(new ExamVO(examList, total));
    }


    private CourseVO getCourseList(List<String> list) {
        if (list.size() != 2) {
            //return new ArrayList<>();
            return new CourseVO();
        }
        String courseStr = list.get(0);
        String dateStr = list.get(1);
        List<Course> courseList = new ArrayList<>();

        //转换成数组数据并遍历
        JSONArray dataArray = JSONArray.parseArray(dateStr); //JSON.parseArray(jsonStr);
        Map<String, String> dateMap = new HashMap<>();

        for (Iterator iterator = dataArray.iterator(); iterator.hasNext(); ) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            String dayOfWeek = (String) jsonObject.get("xqmc");
            String date = (String) jsonObject.get("rq");
            dateMap.put(dayOfWeek, date);
        }

        //转换成数组数据并遍历
        JSONArray jsonArray = JSONArray.parseArray(courseStr);//JSON.parseArray(jsonStr);
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext(); ) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            String id = (String) jsonObject.get("kcbh");
            String name = (String) jsonObject.get("kcmc");
            String teacher = (String) jsonObject.get("teaxms");
            String type = (String) jsonObject.get("jxhjmc");
            String dayOfWeek = (String) jsonObject.get("xq");
            String section = (String) jsonObject.get("jcdm2");
            String week = (String) jsonObject.get("zc");
            String location = (String) jsonObject.get("jxcdmc");
            String grade = (String) jsonObject.get("jxbmc");
            String stuCount = (String) jsonObject.get("pkrs");
            String content = (String) jsonObject.get("sknrjj");
            Integer count = section.length() > 6 ? 2 : 1;
            String date = dateMap.get(dayOfWeek);
            Course course = new Course(id, name, teacher, type, dayOfWeek, date, section, week, location, grade, stuCount, content);
            course.setCount(count);
            courseList.add(course);
        }

        CourseVO courseVO = new CourseVO(courseList, dateMap);
        return courseVO;
    }

    private List<String> getParams(String str) {
        str = Objects.requireNonNull(str).substring(1, str.length() - 1);
        List<String> list = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if ((str.charAt(i) == ']' && str.charAt(i - 1) == '}') || (str.charAt(i) == ']' && str.charAt(i - 1) == '[')) {
                String subStr = str.substring(j, i + 1);
                //System.out.println(subStr);
                //System.out.println("============================");
                list.add(subStr);
                j = i + 2;
            }
        }
        return list;
    }
}
