package searchengine.services;

import searchengine.dto.SearchData;

import java.util.List;

public interface SearchService {
    List<SearchData> allSiteSearch(String text, int offset, int limit);
    List<SearchData> siteSearch(String searchText, String url, int offset, int limit);
}
