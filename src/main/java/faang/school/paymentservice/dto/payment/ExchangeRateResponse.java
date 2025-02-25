package faang.school.paymentservice.dto.payment;

import lombok.Getter;

import java.util.Map;

@Getter
public class ExchangeRateResponse {
    private String base;
    private Map<String, Double> rates;
    private long timestamp;
}
