package searchengine.dto.responses;

import searchengine.dto.SearchData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResult {
    private boolean result;
    private int count;
    private List<SearchData> data;

    public SearchResult(boolean result) {
        this.result = result;
    }

    public SearchResult(boolean result, int count, List<SearchData> data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }
}
