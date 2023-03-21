package de.dreipc.xcurator.xcuratorimportservice.wikipedia;

import de.dreipc.xcurator.xcuratorimportservice.wikipedia.fetchers.WikipediaBaseEntityFetcher;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WikiPedia {

    private static final String DEFAULT_LANGUAGE = "de";
    private static final WikiPediaAPI API = new WikiPediaAPI();
    
    public WikiPediaBaseEntity entity(String title) {
        return entity(title, DEFAULT_LANGUAGE);
    }

    public WikiPediaBaseEntity entity(String title, String language) {

        if (title.isEmpty())
            throw new IllegalArgumentException("Title (" + title + ") is not valid");

        return WikipediaBaseEntityFetcher.convert(API.requestQuery(title, language));
    }

    public List<WikiPediaBaseEntity> entities(List<String> titles) {
        return entities(titles, DEFAULT_LANGUAGE);
    }

    @Cacheable("wikipedia")
    public List<WikiPediaBaseEntity> entities(List<String> titles, String language) {
        var responseArray = titles.stream().map(title -> entity(title, language)).toList();
        return responseArray;
    }
}
