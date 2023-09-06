package co.com.bancolombia.api.products.dto.response;
import co.com.bancolombia.api.products.builder.ProductData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;
@RequiredArgsConstructor
@Getter
public class ProductResponseDTO {
    private final List<ProductData> data;
}
