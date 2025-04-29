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

    private int limit = 10;

    /**
     * 커서 값 (정렬 기준의 마지막 값)
     */
    private String cursor;

    /**
     * 보조 커서(createdAt) 값
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant after;


}
