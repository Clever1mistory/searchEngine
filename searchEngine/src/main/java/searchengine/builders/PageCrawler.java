package searchengine.builders;

import org.springframework.beans.factory.annotation.Autowired;
import searchengine.dto.PageDto;
import searchengine.config.ConnectionProperties;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.utils.RandomUserAgent;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;


@Slf4j
public class PageCrawler extends RecursiveTask<List<PageDto>> {
    private final String url;
    private final List<String> urlList;
    private final List<PageDto> pageDtoList;
    @Autowired
    private ConnectionProperties connectionProperties;

    public PageCrawler(String url, List<PageDto> pageDtoList, List<String> urlList) {
        this.url = url;
        this.pageDtoList = pageDtoList;
        this.urlList = urlList;
    }

    @Override
    protected List<PageDto> compute() {
        try {
            Thread.sleep(100);
            Document doc = getConnect(url);
            String html = doc.outerHtml();
            Connection.Response response = doc.connection().response();
            int status = response.statusCode();
            PageDto pageDto = new PageDto(url, html, status);
            pageDtoList.add(pageDto);
            Elements elements = doc.select("body").select("a");
            List<PageCrawler> taskList = new ArrayList<>();
            for (Element el : elements) {
                String link = el.attr("abs:href");
                if (link.startsWith(el.baseUri())
                        && !link.equals(el.baseUri())
                        && !link.contains("#")
                        && !link.contains(".pdf")
                        && !link.contains(".jpg")
                        && !link.contains(".JPG")
                        && !link.contains(".png")
                        && !urlList.contains(link)) {
                    urlList.add(link);
                    PageCrawler task = new PageCrawler(link, pageDtoList, urlList);
                    task.fork();
                    taskList.add(task);
                }
            }
            taskList.forEach(ForkJoinTask::join);
        } catch (Exception e) {
            log.debug("Ошибка парсинга - " + url);
            PageDto pageDto = new PageDto(url, "", 500);
            pageDtoList.add(pageDto);
        }
        return pageDtoList;
    }

//    public Document getConnect(String url) {
//        Document doc = null;
//        try {
//            Thread.sleep(150);
//            doc = Jsoup.connect(connectionProperties.getUrl())
//                    .userAgent(connectionProperties.getUserAgent())
//                    .referrer(connectionProperties.getReferrer())
//                    .get();
//        } catch (Exception e) {
//            log.debug("Не удалось установить подключение с " + connectionProperties.getUrl());
//        }
//        return doc;
//    }
    public Document getConnect(String url) {
        Document doc = null;
        try {
            Thread.sleep(150);
            doc = Jsoup.connect(url).userAgent(RandomUserAgent.getRandomUserAgent()).referrer("http://www.google.com").get();
        } catch (Exception e) {
            log.debug("Не удалось установить подключение с " + url);
        }
        return doc;
    }

}