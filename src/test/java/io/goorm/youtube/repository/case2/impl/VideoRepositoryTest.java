package io.goorm.youtube.repository.case2.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.youtube.commom.util.FileUploadUtil;
import io.goorm.youtube.config.TestConfig;
import io.goorm.youtube.domain.Member;
import io.goorm.youtube.domain.Video;
import io.goorm.youtube.repository.case1.VideoSearchCondition;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // JPA 테스트를 위한 어노테이션
@Import(TestConfig.class)  // 테스트 설정 클래스 임포트
@ImportAutoConfiguration(exclude = {  // 보안 관련 자동 설정 제외
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
public class VideoRepositoryTest {
    @Autowired
    private EntityManager entityManager;  // JPA EntityManager 주입

    @Autowired
    private JPAQueryFactory queryFactory;  // QueryDSL 쿼리 생성을 위한 팩토리

    private VideoRepositoryImpl videoRepository;  // 테스트 대상 리포지토리
    private Member testMember;  // 테스트용 멤버 객체

    @Autowired
    private FileUploadUtil fileUploadUtil;  // 파일 업로드 유틸리티

    @BeforeEach  // 각 테스트 전 실행되는 설정
    void setUp() {
        // 테스트용 파일 업로드 디렉토리 설정
        fileUploadUtil.setUploadDir("src/test/resources/uploads");
        // 비디오 리포지토리 초기화
        videoRepository = new VideoRepositoryImpl(queryFactory);
        // 테스트용 멤버 생성
        testMember = createMember("testUser", "password", "테스터");
    }

    // 테스트용 멤버 생성 헬퍼 메서드
    private Member createMember(String id, String password, String nickname) {
        Member member = new Member();
        member.setMemberId(id);
        member.setMemberPw(password);
        member.setMemberNick(nickname);
        member.setUseYn("Y");  // 사용 가능 상태로 설정
        entityManager.persist(member);  // DB에 저장
        return member;
    }

    // 테스트용 비디오 생성 헬퍼 메서드
    private Video createVideo(String title, String content, Long memberSeq, int publishYn) {
        Video video = new Video();
        video.setTitle(title);
        video.setContent(content);
        video.setMemberSeq(memberSeq);
        video.setPublishYn(publishYn);  // 공개 여부 설정
        video.setDeleteYn("N");  // 삭제되지 않은 상태로 설정
        entityManager.persist(video);  // DB에 저장
        return video;
    }

    @Test  // 조건에 따른 비디오 검색 테스트
    void findVideosByCondition() {
        // given: 테스트 데이터 설정
        createVideo("테스트 비디오1", "테스트 내용1", testMember.getMemberSeq(), 1);
        createVideo("테스트 비디오2", "테스트 내용2", testMember.getMemberSeq(), 0);
        entityManager.flush();  // DB에 변경사항 반영

        // 디버깅을 위한 모든 비디오 조회 및 출력
        List<Video> allVideos = entityManager
                .createQuery("SELECT v FROM Video v", Video.class)
                .getResultList();
        System.out.println("All Videos in DB: " + allVideos.size());
        allVideos.forEach(v -> {
            System.out.println("Video: " + v.getTitle()
                    + ", publishYn: " + v.getPublishYn()
                    + ", deleteYn: " + v.getDeleteYn()
                    + ", memberSeq: " + v.getMemberSeq());
        });

        // 검색 조건 설정
        VideoSearchCondition condition = new VideoSearchCondition();
        condition.setPublishYn(1);  // 공개된 비디오만 검색
        condition.setMemberSeq(testMember.getMemberSeq());  // 특정 멤버의 비디오만 검색

        // when: 검색 실행
        List<Video> videos = videoRepository.findVideosByCondition(condition);

        // then: 검증
        assertThat(videos).hasSize(1);  // 검색 결과가 1개인지 확인
        assertThat(videos.get(0).getTitle()).isEqualTo("테스트 비디오1");  // 제목 확인
    }

    @Test  // 공개된 멤버 비디오 검색 테스트
    void findPublicVideosByMember() {
        // given: 테스트 데이터 설정
        createVideo("공개 비디오", "공개 내용", testMember.getMemberSeq(), 1);
        createVideo("비공개 비디오", "비공개 내용", testMember.getMemberSeq(), 0);
        entityManager.flush();

        // when: 공개 비디오 검색
        List<Video> publicVideos = videoRepository.findPublicVideosByMember(1L);

        // then: 검증
        assertThat(publicVideos).hasSize(1);  // 공개 비디오가 1개인지 확인
        assertThat(publicVideos.get(0).getTitle()).isEqualTo("공개 비디오");  // 제목 확인
    }

    @Test  // 비디오 검색 기능 테스트
    void searchVideos() {
        // given: 테스트 데이터 설정
        createVideo("스프링 강좌", "스프링 부트 내용", testMember.getMemberSeq(), 1);
        createVideo("JPA 강좌", "하이버네이트 내용", testMember.getMemberSeq(), 1);
        entityManager.flush();

        // when: "스프링" 키워드로 검색
        List<Video> videos = videoRepository.searchVideos("스프링");

        // then: 검증
        assertThat(videos).hasSize(1);  // 검색 결과가 1개인지 확인
        assertThat(videos.get(0).getTitle()).contains("스프링");  // 제목에 "스프링"이 포함되어 있는지 확인
    }
}

