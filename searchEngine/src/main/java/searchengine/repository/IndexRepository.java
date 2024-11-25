package searchengine.repository;

import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<IndexEntity, Long> {
    @Query(value = "SELECT i.* FROM Index i WHERE i.lemma_id IN :lemmas AND i.page_id IN :pages", nativeQuery = true)
    List<IndexEntity> findByPagesAndLemmas(@Param("lemmas") List<LemmaEntity> lemmaListId,
                                           @Param("pages") List<PageEntity> pageListId);

    List<IndexEntity> findByLemmaId (long lemmaId);
    List<IndexEntity> findByPageId (long pageId);
    IndexEntity findByLemmaIdAndPageId (long lemmaId, long pageId);
}