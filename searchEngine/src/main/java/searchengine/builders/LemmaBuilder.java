package searchengine.builders;

import searchengine.dto.LemmaDto;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.morphology.Morphology;
import searchengine.repository.PageRepository;
import searchengine.utils.HtmlTextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class LemmaBuilder implements LemmaParser {
    private final PageRepository pageRepository;
    private final Morphology morphology;
    private List<LemmaDto> lemmaDtoList;

    @Override
    public void run(SiteEntity site) {
        lemmaDtoList = new CopyOnWriteArrayList<>();
        Iterable<PageEntity> pageList = pageRepository.findAll();
        TreeMap<String, Integer> lemmaList = new TreeMap<>();
        for (PageEntity page : pageList) {
            String content = page.getContent();
            String title = HtmlTextExtractor.extractText(content, "title");
            String body = HtmlTextExtractor.extractText(content, "body");
            HashMap<String, Integer> titleList = (HashMap<String, Integer>) morphology.getLemmaList(title);
            HashMap<String, Integer> bodyList = (HashMap<String, Integer>) morphology.getLemmaList(body);
            Set<String> allWords = new HashSet<>();
            allWords.addAll(titleList.keySet());
            allWords.addAll(bodyList.keySet());
            for (String word : allWords) {
                int frequency = lemmaList.getOrDefault(word, 0) + 1;
                lemmaList.put(word, frequency);
            }
        }
        for (String lemma : lemmaList.keySet()) {
            Integer frequency = lemmaList.get(lemma);
            lemmaDtoList.add(new LemmaDto(lemma, frequency));
        }
    }

    @Override
    public List<LemmaDto> getLemmaDtoList() {
        return lemmaDtoList;
    }
}
