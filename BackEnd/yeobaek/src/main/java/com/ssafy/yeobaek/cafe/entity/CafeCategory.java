package com.ssafy.yeobaek.cafe.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cafe_category")
public class CafeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cateCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Column(name = "category_name", nullable = false)
    private String categoryName;
}
