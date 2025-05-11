package team03.monew.dto.comments;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
public class CommentListRequest {

    @NotNull(message = "articleId는 필수값입니다.")
    private UUID articleId;

    private String orderBy = "createdAt";

    private Sort.Direction direction = Sort.Direction.DESC;

    private Integer limit;

    private String cursor;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant after;

    public int getLimit() {
        return (limit == null) ? 10 : limit;
    }
}
