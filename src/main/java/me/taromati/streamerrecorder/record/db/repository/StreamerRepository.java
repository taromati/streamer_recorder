package me.taromati.streamerrecorder.record.db.repository;

import me.taromati.streamerrecorder.record.db.entity.StreamerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreamerRepository extends JpaRepository<StreamerEntity, Integer>, JpaSpecificationExecutor<StreamerEntity> {

    Optional<StreamerEntity> findByPlatformAndAccountId(String platform, String accountId);
    Optional<StreamerEntity> findByPlatformAndUserName(String platform, String userName);
}
