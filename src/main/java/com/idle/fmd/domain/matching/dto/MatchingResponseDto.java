package com.idle.fmd.domain.matching.dto;

import com.idle.fmd.domain.lol.dto.LolMatchDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MatchingResponseDto {
    private String nickname;
    private String profileImg;
    private String lolNickname;
    private String mode;
    private String myLine;
    private String tier;
    private String rank;
    private String tierImg;
    private String mostOne;
    private String mostTwo;
    private String mostThree;
    private Long totalWins;
    private Long totalLoses;
    private List<LolMatchDto> matchList;
}
