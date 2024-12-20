package searchengine.builders;

import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.IndexDto;
import searchengine.dto.LemmaDto;
import searchengine.dto.PageDto;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
@Slf4j
public class SiteIndexer implements Runnable {

    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmaParser lemmaParser;
    private final IndexParser indexParser;
    private final String url;
    private final SitesList sitesList;


    @Override
    public void run() {
        if (siteRepository.findByUrl(url) != null) {
            log.info("Запущен процесс удаления данных сайта - " + url);
            deleteDataFromSite();
        }
        log.info("Запущен процесс индексации - " + url + " " + getName());
        saveDateSite();

        try {
            List<PageDto> pageDtoList = getPageDtoList();
            saveToBase(pageDtoList);
            getLemmasPage();
            indexingWords();

        } catch (InterruptedException e) {
            log.error("Индексация остановлена - " + url);
            errorSite();
        }
    }

    private List<PageDto> getPageDtoList() throws InterruptedException {
        if (!Thread.interrupted()) {
            String urlFormat = url + "/";
            List<PageDto> pageDtoVector = new Vector<>();
            List<String> urlList = new Vector<>();
            ForkJoinPool forkJoinPool = new ForkJoinPool(processorCoreCount);
            List<PageDto> pages = forkJoinPool.invoke(new PageCrawler(urlFormat, pageDtoVector, urlList));
            return new CopyOnWriteArrayList<>(pages);
        } else throw new InterruptedException();
    }

    private void saveToBase(List<PageDto> pages) throws InterruptedException {
        if (!Thread.interrupted()) {
            List<PageEntity> pageList = new CopyOnWriteArrayList<>();
            SiteEntity site = siteRepository.findByUrl(url);

            for (PageDto page : pages) {
                int start = page.getUrl().indexOf(url) + url.length();
                String pageFormat = page.getUrl().substring(start);
                pageList.add(new PageEntity(site, pageFormat, page.getCode(),
                        page.getContent()));
            }
            pageRepository.flush();
            pageRepository.saveAll(pageList);
        } else {
            throw new InterruptedException();
        }
    }

    private void getLemmasPage() {
        if (!Thread.interrupted()) {
            SiteEntity siteEntity = siteRepository.findByUrl(url);
            siteEntity.setStatusTime(new Date());
            lemmaParser.run(siteEntity);
            List<LemmaDto> lemmaDtoList = lemmaParser.getLemmaDtoList();
            List<LemmaEntity> lemmaList = new CopyOnWriteArrayList<>();

            for (LemmaDto lemmaDto : lemmaDtoList) {
                lemmaList.add(new LemmaEntity(lemmaDto.getLemma(), lemmaDto.getFrequency(), siteEntity));
            }
            lemmaRepository.flush();
            lemmaRepository.saveAll(lemmaList);
        } else {
            throw new RuntimeException();
        }
    }

    private void indexingWords() throws InterruptedException {
        if (!Thread.interrupted()) {
            SiteEntity site = siteRepository.findByUrl(url);
            indexParser.run(site);
            List<IndexDto> indexDtoList = new CopyOnWriteArrayList<>(indexParser.getIndexList());
            List<IndexEntity> indexList = new CopyOnWriteArrayList<>();
            site.setStatusTime(new Date());
            for (IndexDto indexDto : indexDtoList) {
                PageEntity page = pageRepository.getById(indexDto.getPageId());
                LemmaEntity lemma = lemmaRepository.getById(indexDto.getLemmaId());
                indexList.add(new IndexEntity(page, lemma, indexDto.getRank()));
            }
            indexRepository.flush();
            indexRepository.saveAll(indexList);
            log.info("Индексация завершена - " + url);
            site.setStatusTime(new Date());
            site.setStatus(Status.INDEXED);
            siteRepository.save(site);

        } else {
            throw new InterruptedException();
        }
    }

    private void deleteDataFromSite() {
        SiteEntity site = siteRepository.findByUrl(url);
        site.setStatus(Status.INDEXING);
        site.setName(getName());
        site.setStatusTime(new Date());
        siteRepository.save(site);
        siteRepository.flush();
        siteRepository.delete(site);
    }

    private void saveDateSite() {
        SiteEntity site = new SiteEntity();
        site.setUrl(url);
        site.setName(getName());
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteRepository.flush();
        siteRepository.save(site);
    }

    private void errorSite() {
        SiteEntity site = new SiteEntity();
        site.setLastError("Индексация остановлена");
        site.setStatus(Status.FAILED);
        site.setStatusTime(new Date());
        siteRepository.save(site);
    }

    private String getName() {
        List<Site> sites = sitesList.getSites();
        for (Site map : sites) {
            if (map.getUrl().equals(url)) {
                return map.getName();
            }
        }
        return "";
    }
}

