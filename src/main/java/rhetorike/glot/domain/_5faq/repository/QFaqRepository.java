package rhetorike.glot.domain._5faq.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import rhetorike.glot.domain._5faq.FaqType;
import rhetorike.glot.domain._5faq.dto.FaqDto;
import rhetorike.glot.domain._5faq.entity.Faq;
import rhetorike.glot.domain._5faq.entity.QFaq;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QFaqRepository {
    private final JPAQueryFactory queryFactory;
    private final QFaq faq = QFaq.faq;

    @Transactional
    public Page<Faq> findAllFaq(FaqDto.GetRequest requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPage(), requestDto.getSize());
        List<Faq> faqs = queryFactory
                .selectFrom(faq)
                .where(
                        eqType(faq, requestDto.getTypeFilter()),
                        containsKeyword(faq, requestDto.getSearchType(), requestDto.getKeyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(faq.count())
                .from(faq)
                .where(
                        eqType(faq, requestDto.getTypeFilter()),
                        containsKeyword(faq, requestDto.getSearchType(), requestDto.getKeyword())
                );
        return PageableExecutionUtils.getPage(faqs, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqType(QFaq faq, FaqType typeFilter) {
        if (typeFilter == null) {
            return null;
        }
        return faq.type.eq(typeFilter);
    }

    private BooleanExpression containsKeyword(QFaq faq, FaqDto.GetRequest.FaqSearchType searchType, String keyword) {
        if (keyword == null) {
            return null;
        }
        return switch (searchType) {
            case TITLE -> faq.title.contains(keyword);
            case CONTENT -> faq.content.contains(keyword);
            case TITLE_AND_CONTENT -> faq.title.contains(keyword).or(faq.content.contains(keyword));
        };
    }
}
