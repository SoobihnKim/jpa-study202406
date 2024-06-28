package com.spring.jpastudy.chap06_querydsl.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
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


}