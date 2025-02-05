package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;


@Setter
@Getter
@Entity
@DynamicInsert
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoSeq;

    private String video;
    private String videoThumnail;

    private String title;
    private String content;

    @OneToMany(mappedBy = "memberSeq", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> Member;

    @Transient
    private String channelName;

    private int publishYn;
    private String deleteYn;

    private String memberSeq;

    private String regDate;


}
