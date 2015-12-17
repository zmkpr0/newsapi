package pro.pham.spring.newsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pro.pham.spring.newsapi.entity.News;
import pro.pham.spring.newsapi.manager.NewsManager;

import java.util.*;

@RestController
public class NewsController {

    @Autowired
    NewsManager newsManager;

    @RequestMapping(value = "/news", method = RequestMethod.GET)
    public List<News> listNews() {
        return newsManager.getNews();
    }

    @RequestMapping(value = "/news/{tag}", method = RequestMethod.GET)
    public List<News> listNews(@PathVariable("tag") String tag) {
        return newsManager.getNewsForTag(tag);
    }

    @RequestMapping(value = "/news", method = RequestMethod.POST)
    public void addNews(@RequestBody News news) {
        newsManager.addNews(news);
    }

    @RequestMapping(value = "/news/edit/{id}", method = RequestMethod.PUT)
    public void addNews(@PathVariable("id") Long id, @RequestBody News news) {
        news.setId(id);
        newsManager.editNews(news);
    }

    @RequestMapping(value = "/news/delete/{id}", method = RequestMethod.DELETE)
    public void deleteNews(@PathVariable("id") Long id) {
        newsManager.deleteNews(id);
    }

    @RequestMapping(value = "/news/addtags/{id}", method = RequestMethod.POST)
    public void addTags(@PathVariable("id") Long id, @RequestBody List<String> tags) {
        Optional<News> newsForIdOptional = newsManager.getNewsForId(id);
        if (!newsForIdOptional.isPresent()) {
            throw new IllegalArgumentException("No single news for id: " + id);
        }
        News news = newsForIdOptional.get();
        Set<String> newTags = new HashSet<>();
        newTags.addAll(news.getTags());
        newTags.addAll(tags);
        newsManager.replaceTagsForNews(id, newTags);
    }
}
