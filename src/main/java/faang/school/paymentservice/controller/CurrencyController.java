package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.payment.ExchangeRates;
import faang.school.paymentservice.dto.payment.Currency;
import faang.school.paymentservice.dto.payment.ExchangeRateResponse;
import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/currency")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<ExchangeRates> getCurrentCurrencyExchangeRate() {
        return ResponseEntity.status(HttpStatus.OK).body(currencyService.getCurrencyRates());
    }

    @GetMapping
    public ExchangeRateResponse getRates(@RequestParam(defaultValue = "USD") Currency baseCurrency) {
        return currencyService.getRates(baseCurrency);
    }

    @GetMapping("/convertor")
    public double convertCurrency(@RequestParam double amount,
                                  @RequestParam Currency baseCurrency,
                                  @RequestParam Currency toCurrency) {
        return currencyService.convertCurrency(amount, baseCurrency, toCurrency);
    }

}
