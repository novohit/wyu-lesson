package com.wyu.wyulesson.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zwx
 * @date 2022-11-24 16:14
 */
@Data
public class ExamVO {
    private List<Exam> examList;

    private Integer examCount;

    public ExamVO() {
    }

    public ExamVO(List<Exam> examList, Integer examCount) {
        this.examList = examList;
        this.examCount = examCount;
    }
}
