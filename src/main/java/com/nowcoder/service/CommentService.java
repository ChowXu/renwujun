package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Project: wenda
 * Author: Chow xi
 * Email: zhouxu_1994@163.com
 * Time: 17/2/15 下午8:50
 */
@Service
public class CommentService {

    @Autowired
    CommentDAO commentDao;

    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> selectComments(int entityId, int entityType) {
        return commentDao.selectComments(entityId, entityType);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDao.getCommentCount(entityId, entityType);
    }

    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment) > 0 ? comment.getId() : 0;
    }

    public boolean deleteComment(int EntityId) {
        return commentDao.updateCommentStatus(1, EntityId) > 0;
    }

    public int getUserCommentCount(int uid) {
        return commentDao.getUserCommentCount(uid);
    }
}
