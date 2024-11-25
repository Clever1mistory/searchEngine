package searchengine.repository;

import searchengine.model.SiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<SiteEntity, Long> {
    SiteEntity findByUrl(String url);
    SiteEntity findByUrl(long id);
    SiteEntity findByUrl(SiteEntity site);
}
