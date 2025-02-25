package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.payment.Currency;
import faang.school.paymentservice.dto.payment.ExchangeRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "currency",
        url = "https://openexchangerates.org/api/latest.json")
public interface ConverterClient {

    @GetMapping
    ExchangeRateResponse getExchangeRate(@RequestParam String app_id, @RequestParam Currency base);
}
