package me.taromati.streamerrecorder.db.repository;

import me.taromati.streamerrecorder.db.entity.StreamerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreamerRepository extends JpaRepository<StreamerEntity, Integer>, JpaSpecificationExecutor<StreamerEntity> {

    Optional<StreamerEntity> findByPlatformAndAccountId(String platform, String accountId);
}
