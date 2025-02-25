package faang.school.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.payment.Currency;
import faang.school.paymentservice.dto.payment.ExchangeRateResponse;
import faang.school.paymentservice.dto.payment.ExchangeRates;
import feign.FeignException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final ExchangeRatesClient exchangeRatesClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String redisKey;
    private final CurrencyClient currencyConverterClient;

    @Value("${open-exchange-rates.key}")
    private String appId;

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

    public ExchangeRateResponse getRates(Currency baseCurrency) {
        return currencyConverterClient.getExchangeRate(appId, baseCurrency);
    }

    public double convertCurrency(double amount, Currency baseCurrency, Currency toCurrency) {
        Map<String, Double> rates = getRates(baseCurrency).getRates();

        if (rates != null && rates.containsKey(toCurrency.toString())) {
            double rate = rates.get(toCurrency.toString());
            return amount * rate;
        } else {
            throw new IllegalArgumentException("Не найден код валюты для конвертации: " + toCurrency);
        }
    }
}
