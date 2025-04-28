package team03.monew.dto.comments;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public class CommentListRequest {

    @NotNull(message = "articleId는 필수값입니다.")
    private UUID articleId;

    private String orderBy = "createdAt";

    private Sort.Direction direction = Sort.Direction.DESC;

    private int limit = 10;

    public UUID getArticleId() {
        return articleId;
    }

    public void setArticleId(UUID articleId) {
        this.articleId = articleId;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Sort.Direction getDirection() {
        return direction;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}