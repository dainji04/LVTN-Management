package group_10.group._0.repository;

import group_10.group._0.entity.Invalidatedtoken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface InvalidatedTokenRepository extends JpaRepository<Invalidatedtoken, String> {

    // DELETE FROM invalidatedtoken WHERE expirationtime < ?1
    void deleteByExpirationtimeBefore(Date now);
}
