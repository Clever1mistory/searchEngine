package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "page", indexes = {@Index(name = "path_list", columnList = "path")})
@Setter
@Getter
@NoArgsConstructor
public class PageEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "site_id", referencedColumnName = "id")
    private SiteEntity siteId;
    @Column(length = 1000, columnDefinition = "VARCHAR(255)", nullable = false)
    private String path;

    private int code;
    @Column(length = 1_000_000, columnDefinition = "text")
    private String content;


    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<IndexEntity> index = new ArrayList<>();

    public PageEntity(SiteEntity siteId, String path, int code, String content) {
        this.siteId = siteId;
        this.path = path;
        this.code = code;
        this.content = content;
    }
}
