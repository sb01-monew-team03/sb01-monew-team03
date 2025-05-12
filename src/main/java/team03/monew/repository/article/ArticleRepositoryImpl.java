package team03.monew.repository.article;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team03.monew.entity.article.Article;
import team03.monew.entity.article.QArticle;
import team03.monew.entity.comments.QComment;
import team03.monew.entity.interest.Interest;
import team03.monew.entity.interest.Keyword;
import team03.monew.entity.interest.QInterest;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Article> findAllByCursor(
        String keyword, UUID interestId, List<String> sourceIn,
        LocalDateTime publishDateFrom, LocalDateTime publishDateTo,
        String orderBy, String direction, String cursor, LocalDateTime after, int limit) {

        QArticle article = QArticle.article;
        QComment comment = QComment.comment;
        QInterest interest = QInterest.interest;

        BooleanBuilder where = new BooleanBuilder();
        where.and(article.deletedAt.isNull());

        if (keyword != null && !keyword.isBlank()) {
            where.and(article.title.containsIgnoreCase(keyword)
                .or(article.summary.containsIgnoreCase(keyword)));
        }

        if (sourceIn != null && !sourceIn.isEmpty()) {
            where.and(article.source.in(sourceIn));
        }

        if (publishDateFrom != null && publishDateTo != null) {
            where.and(article.publishedAt.between(publishDateFrom, publishDateTo));
        } else if (publishDateFrom != null) {
            where.and(article.publishedAt.goe(publishDateFrom));
        } else if (publishDateTo != null) {
            where.and(article.publishedAt.loe(publishDateTo));
        }

        if (interestId != null) {
            Interest targetInterest = em.find(Interest.class, interestId);
            if (targetInterest != null) {
                List<String> keywordNames = targetInterest.getKeywords().stream()
                    .map(Keyword::getName)
                    .toList();

                BooleanBuilder keywordBuilder = new BooleanBuilder();
                for (String k : keywordNames) {
                    keywordBuilder.or(article.title.containsIgnoreCase(k));
                    keywordBuilder.or(article.summary.containsIgnoreCase(k));
                }
                where.and(keywordBuilder);
            }
        }

        BooleanExpression cursorCondition = buildCursorCondition(article, comment, orderBy,
            direction, cursor, after);
        if (cursorCondition != null) {
            where.and(cursorCondition);
        }

        JPAQuery<Article> query = queryFactory
            .selectFrom(article)
            .distinct()
            .leftJoin(article.interests, interest);

        if ("commentCount".equalsIgnoreCase(orderBy)) {
            query.leftJoin(comment).on(comment.article.eq(article));
            query.groupBy(article.id);
        }

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(article, comment, orderBy,
            direction);

        return query
            .where(where)
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .limit(limit + 1)
            .fetch();
    }

    @Override
    public long countAllByCondition(
        String keyword,
        UUID interestId,
        List<String> sourceIn,
        LocalDateTime publishDateFrom,
        LocalDateTime publishDateTo) {

        QArticle article = QArticle.article;
        BooleanBuilder where = new BooleanBuilder();

        where.and(article.deletedAt.isNull());

        if (keyword != null && !keyword.isBlank()) {
            where.and(article.title.containsIgnoreCase(keyword)
                .or(article.summary.containsIgnoreCase(keyword)));
        }

        if (sourceIn != null && !sourceIn.isEmpty()) {
            where.and(article.source.in(sourceIn));
        }

        if (publishDateFrom != null && publishDateTo != null) {
            where.and(article.publishedAt.between(publishDateFrom, publishDateTo));
        } else if (publishDateFrom != null) {
            where.and(article.publishedAt.goe(publishDateFrom));
        } else if (publishDateTo != null) {
            where.and(article.publishedAt.loe(publishDateTo));
        }

        return queryFactory
            .select(article.count())
            .from(article)
            .where(where)
            .fetchOne();
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(QArticle article, QComment comment,
        String orderBy, String direction) {

        Order order = "ASC".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;

        if ("commentCount".equalsIgnoreCase(orderBy)) {
            return List.of(
                new OrderSpecifier<>(order, comment.count()),
                new OrderSpecifier<>(order, article.id)
            );
        } else if ("viewCount".equalsIgnoreCase(orderBy)) {
            return List.of(
                new OrderSpecifier<>(order, article.viewCount),
                new OrderSpecifier<>(order, article.id)
            );
        } else {
            return List.of(
                new OrderSpecifier<>(order, article.publishedAt),
                new OrderSpecifier<>(order, article.id)
            );
        }
    }

    private BooleanExpression buildCursorCondition(QArticle article, QComment comment,
        String orderBy, String direction, String cursor, LocalDateTime after) {

        if (cursor == null || after == null) {
            return null;
        }

        Order dir = "DESC".equalsIgnoreCase(direction) ? Order.DESC : Order.ASC;

        if ("commentCount".equalsIgnoreCase(orderBy)) {
            long count = Long.parseLong(cursor);
            return (dir == Order.DESC) ?
                comment.count().lt(count)
                    .or(comment.count().eq(count).and(article.publishedAt.lt(after))) :
                comment.count().gt(count)
                    .or(comment.count().eq(count).and(article.publishedAt.gt(after)));

        } else if ("viewCount".equalsIgnoreCase(orderBy)) {
            long views = Long.parseLong(cursor);
            return (dir == Order.DESC) ?
                article.viewCount.lt(views)
                    .or(article.viewCount.eq(views).and(article.publishedAt.lt(after))) :
                article.viewCount.gt(views)
                    .or(article.viewCount.eq(views).and(article.publishedAt.gt(after)));

        } else {
            LocalDateTime pub = LocalDateTime.parse(cursor);
            return (dir == Order.DESC) ?
                article.publishedAt.lt(pub)
                    .or(article.publishedAt.eq(pub).and(article.publishedAt.lt(after))) :
                article.publishedAt.gt(pub)
                    .or(article.publishedAt.eq(pub).and(article.publishedAt.gt(after)));
        }
    }
}
