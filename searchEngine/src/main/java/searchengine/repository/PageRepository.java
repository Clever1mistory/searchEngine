package searchengine.repository;

import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, Long> {
    long countBySiteId(SiteEntity siteId);

    Iterable<PageEntity> findBySiteId(SiteEntity site);

    @Query(value = "SELECT p.* FROM Page p JOIN Index i ON p.id = i.page_id WHERE i.lemma_id IN :lemmas", nativeQuery = true)
    List<PageEntity> findByLemmaList(@Param("lemmas") Collection<LemmaEntity> lemmaListId);
}
