package io.github.vikij.ordermanagement.auth.repository;

import io.github.vikij.ordermanagement.auth.entity.RefreshToken;
import io.github.vikij.ordermanagement.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(AppUser user);
}
