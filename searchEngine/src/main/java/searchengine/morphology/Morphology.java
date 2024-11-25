package searchengine.morphology;

import java.util.List;
import java.util.Map;

public interface Morphology {
    Map<String, Integer> getLemmaList(String content);
    List<String> getLemma(String word);
    List<Integer> findLemmaIndexInText(String content, String lemma);
}
