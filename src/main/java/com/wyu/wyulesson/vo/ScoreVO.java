package com.wyu.wyulesson.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zwx
 * @date 2022-11-24 15:24
 */
@Data
public class ScoreVO {
    private List<Score> scoreList;

    private Integer scoreCount;

    public ScoreVO() {
    }

    public ScoreVO(List<Score> scoreList, Integer scoreCount) {
        this.scoreList = scoreList;
        this.scoreCount = scoreCount;
    }
}
