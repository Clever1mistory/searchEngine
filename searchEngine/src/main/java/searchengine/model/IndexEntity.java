package searchengine.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "index", indexes = {
        @Index(name = "page_id_list", columnList = "page_id"),
        @Index(name = "lemma_id_list", columnList = "lemma_id")})
@NoArgsConstructor
public class IndexEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", referencedColumnName = "id")
    private PageEntity page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id", referencedColumnName = "id")
    private LemmaEntity lemma;

    @Column(nullable = false, name = "index_rank")
    private float rank;

    public IndexEntity(PageEntity page, LemmaEntity lemma, float rank) {
        this.page = page;
        this.lemma = lemma;
        this.rank = rank;
    }
}
