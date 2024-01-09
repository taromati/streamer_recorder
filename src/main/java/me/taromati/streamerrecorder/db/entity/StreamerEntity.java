package me.taromati.streamerrecorder.db.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

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
}
