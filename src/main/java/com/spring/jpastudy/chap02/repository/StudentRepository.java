package com.spring.jpastudy.chap02.repository;

import com.spring.jpastudy.chap02.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

}
