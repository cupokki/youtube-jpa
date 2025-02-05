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
public class Admin  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminSeq;

    private String adminId;
    private String adminPw;
    private String adminName;
    private String useYn;

    private String regSeq;

    @OneToMany(mappedBy = "memberSeq", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> Member;

    @Transient
    private String regName;

    private String regDate;
}
