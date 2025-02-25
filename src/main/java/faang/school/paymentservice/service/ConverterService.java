package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ConverterClient;
import faang.school.paymentservice.dto.payment.Currency;
import faang.school.paymentservice.dto.payment.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConverterService {

    private final ConverterClient converterClient;

    @Value("${open-exchange-rates.key}")
    private String appId;


    public ExchangeRateResponse getRates(Currency baseCurrency) {
        return converterClient.getExchangeRate(appId, baseCurrency);
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
