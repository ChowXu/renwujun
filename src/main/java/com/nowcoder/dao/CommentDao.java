package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Project: wenda
 * Author: Chow xi
 * Email: zhouxu_1994@163.com
 * Time: 17/2/15 下午8:34
 */

@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " content, entity_id, entity_type, created_date, user_id, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{content},#{entityId},#{entityType},#{createdDate},#{userId}, #{status})"})
    int addComment(Comment comment);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where entity_id = #{entityId} and entity_type = #{entityType}"})
    List<Comment> selectComments(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from", TABLE_NAME, "where entity_id = #{entityId} and entity_type = #{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"update", TABLE_NAME, "set status = #{status} and id = #{id}"})
    int updateCommentStatus(@Param("status") int status, @Param("id") int id);

    @Select({"select count(id) from", TABLE_NAME, "where user_id = #{uid}"})
    int getUserCommentCount(int uid);
}
