package searchengine.morphology;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class MorphologyAnalyser implements Morphology{

    private static final RussianLuceneMorphology russianMorph;
    private final static String regex = "\\p{Punct}|[0-9]|@|©|◄|»|«|—|-|№|…";
    private final static Logger logger = LogManager.getLogger(LuceneMorphology.class);
    private final static Marker INVALID_SYMBOL_MARKER = MarkerManager.getMarker("INVALID_SYMBOL");

    static {
        try {
            russianMorph = new RussianLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Integer> getLemmaList(String content) {
        if (content == null || content.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        content = content.toLowerCase(Locale.ROOT).replaceAll(regex, " ");
        Map<String, Integer> lemmas = new HashMap<>();
        String[] elements = content.split("\\s+");

        for (String element : elements) {
            List<String> words = getLemma(element);
            for (String word : words) {
                lemmas.put(word, lemmas.getOrDefault(word, 0) + 1);
            }
        }

        return lemmas;
    }

    @Override
    public List<String> getLemma(String word) {
        List<String> lemmas = new ArrayList<>();
        try {
            List<String> normalizedForms  = russianMorph.getNormalForms(word);
            if (!isServiceWord(word)) {
               lemmas.addAll(normalizedForms);
            }
        } catch (Exception ex) {
            logger.debug(INVALID_SYMBOL_MARKER, "Символ не найден: " + word);
        }
        return lemmas;
    }

    @Override
    public List<Integer> findLemmaIndexInText(String content, String lemma) {
        List<Integer> lemmaIndexList = new ArrayList<>();
        String[] elements = content.toLowerCase(Locale.ROOT).split("\\p{Punct}|\\s");
        int index = 0;

        for (String el : elements) {
            if (el.isEmpty()) { // Пропускаем пустые строки
                index += 1; // Увеличиваем индекс на 1, чтобы учесть пробел
                continue;
            }

            Set<String> lemmas = new HashSet<>(getLemma(el));
            if (lemmas.contains(lemma)) {
                lemmaIndexList.add(index);
            }

            index += el.length() + 1;
        }
        return lemmaIndexList;
    }

    private boolean isServiceWord(String word) {
        List<String> morphForm = russianMorph.getMorphInfo(word);
        for (String l : morphForm) {
            if (l.contains("ПРЕДЛ")
                    || l.contains("СОЮЗ")
                    || l.contains("МЕЖД")
                    || l.contains("МС")
                    || l.contains("ЧАСТ")
                    || l.length() <= 3) {
                return true;
            }
        }
        return false;
    }
}
