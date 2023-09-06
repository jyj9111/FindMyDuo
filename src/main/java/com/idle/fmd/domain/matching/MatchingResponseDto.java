package com.idle.fmd.domain.matching;

import com.idle.fmd.domain.lol.dto.LolMatchDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MatchingResponseDto {
    private String nickname;
    private String lolNickname;
    private String mode;
    private String myLine;
    private String tier;
    private String rank;
    private Long mostOne;
    private Long mostTwo;
    private Long mostThree;
    private Long totalWins;
    private Long totalLoses;
    private List<LolMatchDto> matchList;
}
