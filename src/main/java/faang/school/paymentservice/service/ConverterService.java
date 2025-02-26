package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ConverterClient;
import faang.school.paymentservice.dto.payment.Currency;
import faang.school.paymentservice.dto.payment.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConverterService {

    private final ConverterClient converterClient;

    @Value("${open-exchange-rates.key}")
    private String appId;

    @Value("${open-exchange-rates.service-fee}")
    private double serviceFee;


    public ExchangeRateResponse getRates(Currency baseCurrency) {
        return converterClient.getExchangeRate(appId, baseCurrency);
    }

    public BigDecimal convertCurrency(BigDecimal amount, Currency baseCurrency, Currency toCurrency) {
        Map<String, BigDecimal> rates = getRates(baseCurrency).getRates();

        if (rates != null && rates.containsKey(toCurrency.toString())) {
            BigDecimal rate = rates.get(toCurrency.toString());
            BigDecimal convertedAmount = amount.multiply(rate);
            BigDecimal finalAmount = convertedAmount.multiply(new BigDecimal(1.0 + serviceFee));
            return finalAmount.setScale(2, RoundingMode.HALF_UP);
        } else {
            log.error("Не найден код валюты для конвертации: {}", toCurrency);
            return BigDecimal.ZERO;
        }
    }
}
