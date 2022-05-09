package me.xnmk.community.service.impl;

import me.xnmk.community.dao.elasticsearch.DiscussPostRepository;
import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.service.ElasticsearchService;
import me.xnmk.community.service.LikeService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.vo.DiscussPostVo;
import me.xnmk.community.vo.param.PageParams;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author:xnmk_zhan
 * @create:2022-05-09 16:32
 * @Description: Elasticsearch-service-impl
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Override
    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    @Override
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    @Override
    public List<DiscussPostVo> searchDiscussPost(String keyword, int current, int limit) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> search = elasticsearchTemplate.search(searchQuery, DiscussPost.class);
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
        return copyList(discussPosts);
    }

    @Override
    public long getDiscussPostCount(String keyword) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content")).build();
        SearchHits<DiscussPost> search = elasticsearchTemplate.search(searchQuery, DiscussPost.class);
        return search.getTotalHits();
    }

    private List<DiscussPostVo> copyList(List<DiscussPost> discussPosts) {
        List<DiscussPostVo> discussPostVoList = new ArrayList<>();
        for (DiscussPost discussPost : discussPosts) {
            discussPostVoList.add(copy(discussPost));
        }
        return discussPostVoList;
    }

    private DiscussPostVo copy(DiscussPost discussPost) {
        DiscussPostVo discussPostVo = new DiscussPostVo();
        BeanUtils.copyProperties(discussPost, discussPostVo);
        // 添加作者信息
        discussPostVo.setUser(userService.findUserById(discussPost.getUserId()));
        // 添加点赞数
        discussPostVo.setLikeCount(likeService.findEntityLikeCount(EntityTypes.ENTITY_TYPE_POST.getCode(), discussPost.getId()));

        return discussPostVo;
    }
}
