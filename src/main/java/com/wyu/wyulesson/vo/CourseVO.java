package com.wyu.wyulesson.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zwx
 * @date 2022-11-23 14:48
 */

@Data
public class CourseVO {

    private List<Course> courseList;

    private Map<String, String> date;


    private Integer courseCount;

    public CourseVO(List<Course> courseList, Map<String, String> date) {
        this.date = date;
        this.courseList = courseList;
        this.courseCount = courseList.stream()
                .mapToInt(Course::getCount).sum();
    }

    public CourseVO() {

    }
}
