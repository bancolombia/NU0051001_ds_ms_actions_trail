package co.com.bancolombia.event.search.dto.criteria;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class SearchCriteriaDTO implements Serializable {

    @NonNull private final Map<String, String> criteria;

}