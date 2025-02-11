package me.taromati.streamerrecorder.record.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StreamerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String platform;
    @Column(nullable = false)
    private String accountId;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String useYn;
}
