package me.cocoblue.springrestdocssample.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.id > :cursor ORDER BY u.id ASC")
    List<UserEntity> findNextPage(@Param("cursor") Long cursor, Pageable pageable);
    Optional<UserEntity> findByEmail(String email);
}
