package com.wyu.wyulesson.vo;

import lombok.Data;

/**
 * @author zwx
 * @date 2022-11-24 16:04
 */
@Data
public class Exam {
    private String term;

    private String teacher;

    private String date;

    private String section;

    private String time;

    private String studentNum;

    private String username;

    private String courseName;

    private String location;

    private String arrange;

    public Exam() {
    }

    public Exam(String term, String teacher, String date, String section, String time, String studentNum, String username, String courseName, String location, String arrange) {
        this.term = term;
        this.teacher = teacher;
        this.date = date;
        this.section = section;
        this.time = time;
        this.studentNum = studentNum;
        this.username = username;
        this.courseName = courseName;
        this.location = location;
        this.arrange = arrange;
    }
}
