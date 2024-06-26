package com.spring.jpastudy.chap02.repository;

import com.spring.jpastudy.chap02.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {


    // 쿼리 메서드: 메서드 이름에 특별한 규칙을 적용하면 SQL이 규칙에 맞게 생성됨
    List<Student> findByName(String name); // findBy+필드명

    List<Student> findByCityAndMajor(String city, String major); // findBy+필드명

    // WHERE major like '%major%'
    List<Student> findByMajorContaining(String major);

    // WHERE major like 'major%'
    List<Student> findByMajorStartingWith(String major);

    // WHERE major like '%major'
    List<Student> findByMajorEndingWith(String major);

    // WHERE age <= ?
//    List<Student> findByAgeLessThanEqual(int age);

    // native sql 사용하기
    @Query(value = "SELECT * FROM tbl_student WHERE stu_name = :snm OR city = :city", nativeQuery = true)
    List<Student> getStudentByNameOrCity(@Param("snm") String name, @Param("city") String city);

    @Query(value = "SELECT * FROM tbl_student WHERE stu_name = ?1 OR city = ?2", nativeQuery = true)
    List<Student> getStudentByNameOrCity2(String name, String city);

    /*
        - JPQL

        SELECT 엔터티별칭
        FROM 엔터티클래스명 AS 엔터티별칭
        WHERE 별칭.필드명

        ex) native - SELECT * FROM tbl_student WHERE stu_name = ?
            JPQL   - SELECT st(전체) FROM Student(엔터티클래스명) AS st WHERE st.name(필드명) = ?

     */

    // 도시명으로 학생 1명을 단일 조회
    // null 방지하기위해  Optional<Student>
    // JPQL 쓸 때는 nativeQuery = true 안써도됨
    @Query(value = "SELECT st FROM Student st WHERE st.city = ?1" )
    Optional<Student> getByCityWithJPQL(String city);

    // 특정 이름이 포함된 학생 리스트 조회하기
    @Query("SELECT stu FROM Student stu WHERE stu.name LIKE %?1%")
    List<Student> searchByNameWithJPQL(String name);

    // JPQL로 갱신 처리하기
    // 에러 - Not supported for DML operations [DELETE FROM com.spring.jpastudy.chap02.entity.Student s WHERE s.name = ?1 AND s.city = ?2]
    @Modifying // SELECT 아니면 무조건 추가, 그래서 이걸 추가해줘야함
    @Query("DELETE FROM Student s WHERE s.name = ?1 AND s.city = ?2")
    void deleteByNameAndCityWithJPQL(String name, String city);

}
