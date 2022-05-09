package me.xnmk.community;

import me.xnmk.community.dao.DiscussPostMapper;
import me.xnmk.community.dao.elasticsearch.DiscussPostRepository;
import me.xnmk.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author:xnmk_zhan
 * @create:2022-05-08 22:06
 * @Description: Elasticsearch-Test
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Test
    public void testInsert() {
        // List<DiscussPost> list = new ArrayList<>();
        List<DiscussPost> list = discussPostMapper.selectList(null);
        discussPostRepository.saveAll(list);
    }

    @Test
    public void testSearch() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> search = elasticsearchTemplate.search(searchQuery, DiscussPost.class);
        // 全部匹配数据
        System.out.println(search.getTotalHits());
        List<SearchHit<DiscussPost>> hits = search.getSearchHits();
        List<DiscussPost> discussPosts = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : hits) {
            // 获得高亮内容
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            // 将高亮内容填充到 content 中
            hit.getContent().setTitle(highlightFields.get("title") == null ? hit.getContent().getTitle() : highlightFields.get("title").get(0));
            hit.getContent().setContent(highlightFields.get("content") == null ? hit.getContent().getContent() : highlightFields.get("content").get(0));
            discussPosts.add(hit.getContent());
        }
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
    }
}
