package co.com.bancolombia.api.products.builder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import java.util.List;
@Value
@Builder(toBuilder=true)
public class ProductData {
    @NonNull String type;
    @NonNull List<String> numbers;
}
