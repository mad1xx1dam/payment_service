package faang.school.paymentservice.dto.payment;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
public class ExchangeRateResponse {
    private String base;
    private Map<String, BigDecimal> rates;
    private long timestamp;
}
