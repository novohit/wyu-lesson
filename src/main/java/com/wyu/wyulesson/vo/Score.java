package com.wyu.wyulesson.vo;

import lombok.Data;

/**
 * @author zwx
 * @date 2022-11-24 15:09
 */
@Data
public class Score {
    private String username;

    private String studentNum;

    private String term;

    private String category;

    private String subCategory;

    private String courseName;

    private String studyType;

    private String score;

    private String credit;

    public Score() {
    }

    public Score(String username, String studentNum, String term, String category, String subCategory, String courseName, String studyType, String score, String credit) {
        this.username = username;
        this.studentNum = studentNum;
        this.term = term;
        this.category = category;
        this.subCategory = subCategory;
        this.courseName = courseName;
        this.studyType = studyType;
        this.score = score;
        this.credit = credit;
    }
}
