package com.spring.jpastudy.chap01.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = "id") // 필드명
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_id")
    private Long id; // PK

    @Column(name = "prod_nm", length = 30, nullable = false)
    private String name; // 상품명
//     prod_nm varchar(30) not null,

    @Column(name = "price")
    private int price; // 상품 가격

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // 문자열 그대로 사용
    protected Category category; // 상품 카테고리

    @CreationTimestamp // INSERT 시 자동으로 서버시간 저장
    @Column(updatable = false) // 수정 불가
    protected LocalDateTime createdAt; // 상품 등록시간
//    created_at datetime(6), 자동으로 케이스 바뀜

    @UpdateTimestamp // UPDATE문 실행 시 자동으로 시간 저장
    private LocalDateTime updatedAt; // 상품 수정시간

    // 데이터베이스에는 저장안하고 클래스 내부에서만 사용할 필드
    @Transient
    protected String nickName;


    public enum Category {
        FOOD, FASHION, ELECTRONIC
    }

    // 컬럼 기본값 설정
    @PrePersist
    public void prePersist() {
        if(this.price == 0) {
            this.price = 10000;
        } if (this.category == null) {
            this.category = Category.FOOD;
        }
    }

}
