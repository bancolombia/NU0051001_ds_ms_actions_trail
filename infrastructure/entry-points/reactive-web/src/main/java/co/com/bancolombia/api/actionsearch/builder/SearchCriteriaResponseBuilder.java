package co.com.bancolombia.api.actionsearch.builder;

import co.com.bancolombia.api.actionsearch.dto.response.SearchCriteriaPageDTO;
import co.com.bancolombia.api.actionsearch.dto.response.SearchCriteriaPageDTO.Meta;
import co.com.bancolombia.api.actionsearch.dto.response.SearchCriteriaPageDTO.Links;
import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.commons.PageSummary;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchCriteriaResponseBuilder {

    private static final String BLANK = "";
    private static final String FORMAT_LINK_PAGE = "?pageNumber=%1$s&pageSize=%2$s";

    public static Mono<ServerResponse> paginationResponse(PageSummary<Action> actionPageSummary) {

        return ServerResponse
                .status(getStatus(actionPageSummary))
                .bodyValue(buildBodyResponse(actionPageSummary));
    }

    private static HttpStatus getStatus(PageSummary<Action> actionPageSummary) {
        if (!actionPageSummary.getData().isEmpty()) {
            return HttpStatus.OK;
        }
        return HttpStatus.NO_CONTENT;
    }

    private static SearchCriteriaPageDTO<Object> buildBodyResponse(PageSummary<Action> actionPageSummary) {

        return SearchCriteriaPageDTO.builder()
                .data(actionPageSummary.getData())
                .meta(buildMeta(actionPageSummary.getTotalPages()))
                .links(buildLinksResponse(actionPageSummary))
                .build();
    }

    private static Meta buildMeta(Integer totalPages) {
        return Meta.builder().totalPage(totalPages).build();
    }

    private static Links buildLinksResponse(PageSummary<Action> actionPageSummary) {

        var pageNumber = actionPageSummary.getPageNumber();
        var pageSize = actionPageSummary.getPageRequest().getSize();
        var totalPages = actionPageSummary.getTotalPages();

        return Links.builder()
                .self(getSelfOrLast(pageNumber, pageSize))
                .first(getTextLink(1, pageSize))
                .last(getSelfOrLast(totalPages, pageSize))
                .next(getNext(pageNumber, pageSize, actionPageSummary.hasNext()))
                .prev(getPrev(pageNumber, pageSize, actionPageSummary.hasPrevious()))
                .build();
    }

    private static String getNext(Integer pageNumber, Integer pageSize, boolean hasNext) {
        return hasNext ? getTextLink((pageNumber + 1), pageSize) : BLANK;
    }

    private static String getPrev(Integer pageNumber, Integer pageSize, boolean hasPrevious) {
        return hasPrevious ? getTextLink((pageNumber - 1), pageSize) : BLANK;
    }

    private static String getSelfOrLast(Integer pageNumber, Integer pageSize) {
        return getTextLink(pageNumber < 1 ? 1 : pageNumber, pageSize);
    }

    private static String getTextLink(Integer pageNumber, Integer pageSize) {
        return String.format(FORMAT_LINK_PAGE, pageNumber, pageSize);
    }

}