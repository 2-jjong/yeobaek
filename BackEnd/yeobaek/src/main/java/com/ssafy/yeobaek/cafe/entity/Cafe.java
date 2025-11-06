package com.ssafy.yeobaek.cafe.entity;

import com.ssafy.yeobaek.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cafe")
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User cafeOwner;

    @Column(name = "cafe_name", nullable = false)
    private String cafeName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "business_start_time", nullable = false)
    private String businessStartTime;

    @Column(name = "business_end_time", nullable = false)
    private String businessEndTime;

    @Column(name = "daily_visitors", nullable = false)
    private String dailyVisitors;

    @Column(name = "view_count", nullable = false)
    @Builder .Default
    private Integer viewCount = 0;

    @Column(name = "bookmark_count", nullable = false)
    @Builder .Default
    private Integer bookmarkCount = 0;

    @ElementCollection
    @CollectionTable(
            name = "cafe_image",
            joinColumns = @JoinColumn(name = "cafe_id"),
            foreignKey = @ForeignKey(
                    name = "fk_cafe_image_cafe_id",
                    foreignKeyDefinition = "FOREIGN KEY (cafe_id) REFERENCES cafe(id)"
            )
    )
    @Column(name = "image_url")
    @OrderColumn(name = "display_order")
    private List<String> images = new ArrayList<>();
}
