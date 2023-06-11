package com.wyu.wyulesson.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author zwx
 * @date 2022-11-23 13:27
 */
@Data
public class Course {
    private String id;

    private String name;

    private String teacher;

    private String type;

    private String dayOfWeek;

    private String date;

    private String section;

    private String week;

    private String location;

    private String grade;

    private String stuCount;

    private String content;

    private Integer count;

    public Course() {
    }

    public Course(String id, String name, String teacher, String type, String dayOfWeek, String date, String section, String week, String location, String grade, String stuCount, String content) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.type = type;
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.section = section;
        this.week = week;
        this.location = location;
        this.grade = grade;
        this.stuCount = stuCount;
        this.content = content;
    }
}
