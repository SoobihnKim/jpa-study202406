package com.spring.jpastudy.chap06_querydsl.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.jpastudy.chap06_querydsl.dto.GroupAverageAgeDto;
import com.spring.jpastudy.chap06_querydsl.entity.Group;
import com.spring.jpastudy.chap06_querydsl.entity.Idol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.spring.jpastudy.chap06_querydsl.entity.QIdol.idol;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class QueryDslGroupingTest {

    @Autowired
    IdolRepository idolRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    JPAQueryFactory factory;


    @BeforeEach
    void setUp() throws Exception {
        //given
        Group leSserafim = new Group("르세라핌");
        Group ive = new Group("아이브");
        Group bts = new Group("방탄소년단");
        Group newjeans = new Group("뉴진스");

        groupRepository.save(leSserafim);
        groupRepository.save(ive);
        groupRepository.save(bts);
        groupRepository.save(newjeans);

        Idol idol1 = new Idol("김채원", 24, "여", leSserafim);
        Idol idol2 = new Idol("사쿠라", 26, "여", leSserafim);
        Idol idol3 = new Idol("가을", 22, "여", ive);
        Idol idol4 = new Idol("리즈", 20, "여", ive);
        Idol idol5 = new Idol("장원영", 20, "여", ive);
        Idol idol6 = new Idol("안유진", 21, "여", ive);
        Idol idol7 = new Idol("카즈하", 21, "여", leSserafim);
        Idol idol8 = new Idol("RM", 29, "남", bts);
        Idol idol9 = new Idol("정국", 26, "남", bts);
        Idol idol10 = new Idol("해린", 18, "여", newjeans);
        Idol idol11 = new Idol("혜인", 16, "여", newjeans);

        idolRepository.save(idol1);
        idolRepository.save(idol2);
        idolRepository.save(idol3);
        idolRepository.save(idol4);
        idolRepository.save(idol5);
        idolRepository.save(idol6);
        idolRepository.save(idol7);
        idolRepository.save(idol8);
        idolRepository.save(idol9);
        idolRepository.save(idol10);
        idolRepository.save(idol11);

    }

    @Test
    @DisplayName("성별, 그룹 별로 그룹화하여 아이돌의 숫자가 3명 이하인 그룹만 조회")
    void groupByGenderTest() {
        //given

        //when
        /*
           SELECT G.group_id, G.group_name( = G.*)
                  I.gender, COUNT(I.idol_id)
           FROM tbl_idol I
           JOIN tbl_group G
           ON I.group_id = G.group_id
           GROUP BY G.group_id, I.gender
         */
        List<Tuple> idolList = factory
                .select(idol.group, idol.gender, idol.count())
                .from(idol)
                .groupBy(idol.gender, idol.group)
                .having(idol.count().loe(3))
                .fetch();
        //then
        System.out.println("\n\n\n");

        for (Tuple tuple : idolList) {
//            System.out.println("tuple = " + tuple);
            Group group = tuple.get(idol.group);
            String gender = tuple.get(idol.gender);
            Long count = tuple.get(idol.count());

            System.out.println(String.format("\n그룹명: %s, 성별: %s, 인원수: %d\n",
                    group.getGroupName(), gender, count));
        }

        System.out.println("idolList = " + idolList);
        System.out.println("\n\n\n");

        /*
        // idolList = [[남, 2], [여, 9]]

    select
        idol0_.group_id as col_0_0_,
        idol0_.gender as col_1_0_,
        count(idol0_.idol_id) as col_2_0_,
        group1_.group_id as group_id1_2_,
        group1_.group_name as group_na2_2_
    from
        tbl_idol idol0_
    inner join
        tbl_group group1_
            on idol0_.group_id=group1_.group_id
    group by
        idol0_.gender ,
        idol0_.group_id

        idolList = [[Group(id=3, groupName=방탄소년단), 남, 2],
                   [Group(id=1, groupName=르세라핌), 여, 3],
                   [Group(id=2, groupName=아이브), 여, 4],
                   [Group(id=4, groupName=뉴진스), 여, 2]]

          select
        idol0_.group_id as col_0_0_,
        idol0_.gender as col_1_0_,
        count(idol0_.idol_id) as col_2_0_,
        group1_.group_id as group_id1_2_,
        group1_.group_name as group_na2_2_
    from
        tbl_idol idol0_
    inner join
        tbl_group group1_
            on idol0_.group_id=group1_.group_id
    group by
        idol0_.gender ,
        idol0_.group_id
    having
        count(idol0_.idol_id)<=?

    idolList = [[Group(id=3, groupName=방탄소년단), 남, 2],
    [Group(id=1, groupName=르세라핌), 여, 3],
    [Group(id=4, groupName=뉴진스), 여, 2]]
         */
    }

    @Test
    @DisplayName("연령대별로 그룹화하여 아이돌 수를 조회")
    void ageGroupTest() {

        /*
          SELECT
              CASE age WHEN BETWEEN 10 AND 19 THEN 10
              CASE age WHEN BETWEEN 20 AND 29 THEN 20
              CASE age WHEN BETWEEN 30 AND 39 THEN 30
              END,
              COUNT(idol_id)
          FROM tbl_idol
          GROUP BY
              CASE age WHEN BETWEEN 10 AND 19 THEN 10
              CASE age WHEN BETWEEN 20 AND 29 THEN 20
              CASE age WHEN BETWEEN 30 AND 39 THEN 30
              END
         */
        //given

        // QueryDSL로 CASE WHEN THEN 표현식 만들기
        NumberExpression<Integer> ageGroupExpression = new CaseBuilder()
                .when(idol.age.between(10, 19)).then(10)
                .when(idol.age.between(20, 29)).then(20)
                .when(idol.age.between(30, 39)).then(30)
                .otherwise(0);

        //when
        List<Tuple> result = factory
                .select(ageGroupExpression, idol.count())
                .from(idol)
                .groupBy(ageGroupExpression)
                .having(idol.count().gt(5)) // 카운트 5보다 큰 것만 조회(Age Group: 20대, Count: 9)
                .fetch();
        //then
        assertFalse(result.isEmpty());
        for (Tuple tuple : result) {
            int ageGroupValue = tuple.get(ageGroupExpression);
            long count = tuple.get(idol.count());

            System.out.println("\n\nAge Group: " + ageGroupValue + "대, Count: " + count);
        }
        // Age Group: 10대, Count: 2
        // Age Group: 20대, Count: 9
    }

    /*
    ### 연습문제
1. 아이돌 그룹별로 아이돌의 그룹명과 평균 나이를 조회하세요.
   평균 나이가 20세와 25세 사이인 그룹만 조회합니다.
2. 힌트:
- `groupBy`와`having`을 사용하여 그룹화하고 조건을 적용하세요.
- `avg`메서드를 사용하여 평균 나이를 계산하세요.
     */

    @Test
    @DisplayName("아이돌 그룹별로 아이돌의 그룹명과 평균 나이를 조회," +
            "평균 나이가 20세와 25세 사이인 그룹만 조회")
    void groupAvgAgeTest() {
        /*
           SELECT G.group_name, AVG(I.age)
           FROM tbl_idol I
           JOIN tbl_group G
           ON I.group_id = G.group_id
           GROUP BY G.group_id
           HAVING AVG(I.age) BETWEEN 20 AND 25
         */

        List<Tuple> result = factory
                .select(idol.group.groupName, idol.age.avg())
                .from(idol)
                .groupBy(idol.group)
                .having(idol.age.avg().between(20, 25))
                .fetch();
        //then
        assertFalse(result.isEmpty());
        for (Tuple tuple : result) {
            String groupName = tuple.get(idol.group.groupName);
            double averageAge = tuple.get(idol.age.avg());

            System.out.println("\n\nGroup: " + groupName + ", Average Age: " + averageAge);
        }

        /*
       select
        group1_.group_name as col_0_0_,
        avg(idol0_.age) as col_1_0_
    from
        tbl_idol idol0_ cross
    join
        tbl_group group1_
    where
        idol0_.group_id=group1_.group_id
    group by
        idol0_.group_id
    having
        avg(idol0_.age) between ? and ?

    Group: 르세라핌, Average Age: 23.6667
    Group: 아이브, Average Age: 20.75
         */
    }

    @Test
    @DisplayName("(결과 DTO 처리)아이돌 그룹별로 아이돌의 그룹명과 평균 나이를 조회," +
            "평균 나이가 20세와 25세 사이인 그룹만 조회")
    void dtoGroupAvgAgeTest() {
        /*
           SELECT G.group_name, AVG(I.age)
           FROM tbl_idol I
           JOIN tbl_group G
           ON I.group_id = G.group_id
           GROUP BY G.group_id
           HAVING AVG(I.age) BETWEEN 20 AND 25
         */

        // Projections: 커스텀 DTO를 포장해주는 객체
        List<GroupAverageAgeDto> result = factory
                .select(
                        Projections.constructor( // 생성자있어야 가능함
                                GroupAverageAgeDto.class,
                                idol.group.groupName,
                                idol.age.avg()
                        )
                )
                .from(idol)
                .groupBy(idol.group)
                .having(idol.age.avg().between(20, 25))
                .fetch();
        //then
        assertFalse(result.isEmpty());
        for (GroupAverageAgeDto dto : result) {
            String groupName = dto.getGroupName();
            double averageAge = dto.getAverageAge();
            System.out.println("\n\nGroup: " + groupName + ", Average Age: " + averageAge);
        }

    }
}