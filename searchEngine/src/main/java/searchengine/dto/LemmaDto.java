package searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LemmaDto {
    String lemma;
    Integer frequency;
}
