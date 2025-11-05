package com.ssafy.yeobaek.cafe.entity;

import com.ssafy.yeobaek.common.enums.ExhibitionStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "display_slot")
public class DisplaySlot {

    @Id
    private Long id;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private ExhibitionStatus status = ExhibitionStatus.AVAILABLE;

    @Column(name = "commission_rate", nullable = false)
    private Double commissionRate;

    @Column(name = "requirements")
    private String requirements;
}
