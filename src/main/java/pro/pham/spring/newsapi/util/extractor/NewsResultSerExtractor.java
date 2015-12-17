package pro.pham.spring.newsapi.util.extractor;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import pro.pham.spring.newsapi.entity.News;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewsResultSerExtractor implements ResultSetExtractor<Collection<News>> {

    @Override
    public Collection<News> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<Long, News> newsMap = new HashMap<>();
        while (resultSet.next()) {
            Long id = resultSet.getLong("ID");
            News news = mapAndPutIfAbsent(newsMap, id, resultSet);
            String tag = resultSet.getString("TAG");
            if (tag != null) {
                news.addTag(tag);
            }
        }
        return newsMap.values();
    }

    private News mapAndPutIfAbsent(Map<Long, News> newsMap, Long id, ResultSet resultSet) throws SQLException {
        if (newsMap.containsKey(id)) {
            return newsMap.get(id);
        } else {
            String title = resultSet.getString("TYTUL");
            String content = resultSet.getString("TRESC");
            Date date = resultSet.getDate("DATA");
            News news = new News(id, title, content, date);
            newsMap.put(id, news);
            return news;
        }
    }
}
