package searchengine.builders;

import searchengine.dto.LemmaDto;
import searchengine.model.SiteEntity;

import java.util.List;

public interface LemmaParser {
    void run(SiteEntity site);
    List<LemmaDto> getLemmaDtoList();
}
