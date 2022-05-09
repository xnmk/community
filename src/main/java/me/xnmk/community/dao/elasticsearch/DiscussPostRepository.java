package me.xnmk.community.dao.elasticsearch;

import me.xnmk.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author:xnmk_zhan
 * @create:2022-05-08 22:03
 * @Description: Elasticsearch-DiscussPost-Repository
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
