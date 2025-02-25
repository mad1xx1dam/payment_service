package faang.school.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.payment.ExchangeRates;
import feign.FeignException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CurrencyService {
    private final ExchangeRatesClient exchangeRatesClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String redisKey;

    @Retry(name = "exchangeRatesRetry")
    public void fetchCurrencyRates() {
        try {
            ExchangeRates exchangeRates = exchangeRatesClient.getExchangeRates();
            if (exchangeRates != null) {
                redisTemplate.opsForValue().set(redisKey, exchangeRates);
            }
        } catch (FeignException e) {
            log.error("Ошибка при получении информации о курсах валют", e);
        }
    }

    public ExchangeRates getCurrencyRates() {
        return Optional.ofNullable(redisTemplate.opsForValue().get(redisKey))
                .map(cachedRates -> objectMapper.convertValue(cachedRates, ExchangeRates.class))
                .orElseGet(() -> {
                    fetchCurrencyRates();
                    return objectMapper.convertValue(redisTemplate
                            .opsForValue()
                            .get(redisKey), ExchangeRates.class);
                });
    }

}
