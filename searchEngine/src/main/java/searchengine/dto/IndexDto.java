package searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndexDto {
    Long pageId;
    Long lemmaId;
    float rank;
}
