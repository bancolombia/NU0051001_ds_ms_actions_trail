package co.com.bancolombia.source.actions.helper;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.DateCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.source.actions.mapper.SearchCriteriaCodeMapper;
import co.com.bancolombia.source.config.PersistenceProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Query;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.springframework.data.mongodb.core.query.Criteria.where;

class QueryActionHelperTest {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String DATE_RANGE = "date";

    private SearchCriteriaCodeMapper searchCriteriaCodeMapper;

    @BeforeEach
    public void setUp() {
        Map<String, String> searchCriteriaCodesMapping = new HashMap<>();
        searchCriteriaCodesMapping.put(TRANSACTION_ID, TRANSACTION_ID);
        searchCriteriaCodesMapping.put(DATE_RANGE, DATE_RANGE);
        PersistenceProperties persistenceProperties = new PersistenceProperties(searchCriteriaCodesMapping);
        searchCriteriaCodeMapper = new SearchCriteriaCodeMapper(persistenceProperties);
    }

    @Test
    void testGetGenericFilterWithSingleValue() {
        SearchCriteriaCodeMapper searchCriteriaCodeMapperMock = spy(searchCriteriaCodeMapper);

        var query = new Query();
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion(TRANSACTION_ID) {
        };
        Set<AnySearchCriterion<?>> setCriteria = new HashSet<>();
        setCriteria.add(transactionIdCriterion);

        Query result = QueryActionHelper.getGenericFilter(searchCriteriaCodeMapperMock, query, setCriteria);

        verify(searchCriteriaCodeMapperMock).getField(TRANSACTION_ID);
        assertEquals(TRANSACTION_ID, result.getQueryObject().get(TRANSACTION_ID));
    }

    @Test
    void testGetGenericFilterWithRangeValue() {
        SearchCriteriaCodeMapper searchCriteriaCodeMapperMock = spy(searchCriteriaCodeMapper);

        int ZERO = 0;
        int HOUR_MAX = 23;
        int MINUTE_MAX = 59;
        int SECOND_MAX = 59;
        int NANO_OF_SECOND_MAX = 9999;

        var query = new Query();

        var localDateTest = LocalDate.now();
        var expectedQueryResult = new Query().addCriteria(where(DATE_RANGE)
                .gte(localDateTest.atTime(ZERO, ZERO, ZERO, ZERO))
                .lt(localDateTest.atTime(HOUR_MAX, MINUTE_MAX, SECOND_MAX, NANO_OF_SECOND_MAX)));
        DateCriterion dateCriterion = new DateCriterion(localDateTest, localDateTest) {
        };
        Set<AnySearchCriterion<?>> setCriteria = new HashSet<>();
        setCriteria.add(dateCriterion);

        Query result =QueryActionHelper.getGenericFilter(searchCriteriaCodeMapperMock, query, setCriteria);

        verify(searchCriteriaCodeMapperMock).getField(DATE_RANGE);
        assertEquals(expectedQueryResult, result);
    }
}