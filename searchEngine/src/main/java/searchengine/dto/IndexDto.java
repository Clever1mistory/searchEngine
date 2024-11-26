package searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndexDto {
    private Long pageId;
    private Long lemmaId;
    private float rank;
}
