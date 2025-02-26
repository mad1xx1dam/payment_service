package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.payment.Currency;
import faang.school.paymentservice.dto.payment.ExchangeRateResponse;
import faang.school.paymentservice.dto.payment.ExchangeRates;
import faang.school.paymentservice.service.ConverterService;
import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/currency")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;
    private final ConverterService converterService;

    @GetMapping
    public ResponseEntity<ExchangeRates> getCurrentCurrencyExchangeRate() {
        return ResponseEntity.status(HttpStatus.OK).body(currencyService.getCurrencyRates());
    }

    @GetMapping("/rates")
    public ExchangeRateResponse getRates(@RequestParam(defaultValue = "USD") Currency baseCurrency) {
        return converterService.getRates(baseCurrency);
    }

    @GetMapping("/convertor")
    public BigDecimal convertCurrency(@RequestParam BigDecimal amount,
                                  @RequestParam Currency baseCurrency,
                                  @RequestParam Currency toCurrency) {
        return converterService.convertCurrency(amount, baseCurrency, toCurrency);
    }

}
