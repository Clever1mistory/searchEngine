package searchengine.builders;

import searchengine.model.SiteEntity;
import searchengine.dto.IndexDto;
import java.util.List;

public interface IndexParser {
    void run(SiteEntity site);
    List<IndexDto> getIndexList();
}
