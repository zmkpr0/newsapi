package pro.pham.spring.newsapi.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import pro.pham.spring.newsapi.entity.News;
import org.springframework.stereotype.Repository;
import pro.pham.spring.newsapi.util.extractor.NewsResultSerExtractor;

import java.util.*;


@Repository
@Transactional(readOnly = true)
public class NewsManager {

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleJdbcInsert newsSimpleJdbcInsert;

    private final static String TAGS_INSERT_QUERY = "insert into TAGI(tag, newsy_id) values (?,?)";
    private final static String UPDATE_QUERY = "update NEWSY set tytul = :tytul, tresc = :tresc, data = :data where id = :id";
    private final static String DELETE_TAGS = "delete from TAGI where newsy_id = :id";
    private final static String DELETE_QUERY = "delete from NEWSY where id = ?";
    private final static String SELECT_NEWS = "select * from NEWSY n left join TAGI t on n.id = t.newsy_id";
    private final static String SELECT_NEWS_FOR_ID = SELECT_NEWS + " where n.id = ?";
    private final static String SELECT_NEWS_FOR_TAG = SELECT_NEWS + " where exists (select 1 from tagi t2 where t2.newsy_id = n.id and tag = ?)";



    @Autowired
    public NewsManager(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        newsSimpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("NEWSY")
                .usingGeneratedKeyColumns("ID");
    }

    public List<News> getNews() {
        Collection<News> newsList = jdbcTemplate.query(SELECT_NEWS, new NewsResultSerExtractor());
        return new ArrayList<>(newsList);
    }

    public List<News> getNewsForTag(String tag) {
        Object[] args = {tag};
        Collection<News> newsList = jdbcTemplate.query(SELECT_NEWS_FOR_TAG, args, new NewsResultSerExtractor());
        return new ArrayList<>(newsList);
    }

    public Optional<News> getNewsForId(Long id) {
        Object[] args = {id};
        Collection<News> newsList = jdbcTemplate.query(SELECT_NEWS_FOR_ID, args, new NewsResultSerExtractor());
        if (newsList.size() != 1) return Optional.empty();
        return Optional.of(newsList.iterator().next());
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addNews(News news) {
        SqlParameterSource parameterSource = getNewsParameterSource(news);
        Number newsId = newsSimpleJdbcInsert.executeAndReturnKey(parameterSource);
        jdbcTemplate.batchUpdate(TAGS_INSERT_QUERY, getTagsArgs(newsId, news.getTags()));
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void editNews(News news) {
        SqlParameterSource paramSource = getNewsParameterSource(news);
        namedParameterJdbcTemplate.update(UPDATE_QUERY, paramSource);
        namedParameterJdbcTemplate.update(DELETE_TAGS, paramSource);
        jdbcTemplate.batchUpdate(TAGS_INSERT_QUERY, getTagsArgs(news.getId(), news.getTags()));
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void replaceTagsForNews(Long id, Collection<String> tags) {
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id",id);
        namedParameterJdbcTemplate.update(DELETE_TAGS, parameterSource);
        jdbcTemplate.batchUpdate(TAGS_INSERT_QUERY, getTagsArgs(id, tags));
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteNews(Number newsId) {
        jdbcTemplate.update(DELETE_QUERY, newsId);
    }

    private List<Object[]> getTagsArgs(Number newsId, Collection<String> tags) {
        List<Object[]> parameters = new ArrayList();
        for (String tag : tags) {
            parameters.add(new Object[] {tag, newsId});
        }
        return parameters;
    }

    private MapSqlParameterSource getNewsParameterSource(News news) {
        return new MapSqlParameterSource()
                .addValue("tytul", news.getTitle())
                .addValue("tresc", news.getContent())
                .addValue("data", news.getDate())
                .addValue("id", news.getId());
    }

}
