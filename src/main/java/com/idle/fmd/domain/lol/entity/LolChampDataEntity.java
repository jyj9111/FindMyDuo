package com.idle.fmd.domain.lol.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "champ_info")
public class LolChampDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long champCode;
    private String champName;
}
