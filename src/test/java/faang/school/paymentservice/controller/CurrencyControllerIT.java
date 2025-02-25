package faang.school.paymentservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CurrencyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Container
    public static PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        POSTGRES_CONTAINER.start();
        POSTGRES_CONTAINER.waitingFor(Wait.forListeningPort());
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }

    @Test
    public void testGetRatesSuccessCase() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/rates")
                        .param("baseCurrency", "USD"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.base").value("USD"));
    }

    @Test
    public void testGetRatesMissingCurrencyType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/rates")
                        .param("baseCurrency", "WRONG"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void testGetRatesWithDefaultParam() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/rates"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.base").value("USD"));
    }

    @Test
    public void testConvertCurrencySuccessCase() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/convertor")
                        .param("amount", "10")
                        .param("baseCurrency", "USD")
                        .param("toCurrency", "EUR"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    public void testConvertCurrencyWithWrongType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/convertor")
                        .param("amount", "10")
                        .param("baseCurrency", "WRONG")
                        .param("toCurrency", "WRONG"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void testConvertCurrencyWithNoAmount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/convertor")
                .param("baseCurrency", "WRONG")
                .param("toCurrency", "WRONG"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void testConvertCurrencyWithNoBase() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/convertor")
                        .param("amount", "10")
                        .param("toCurrency", "WRONG"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void testConvertCurrencyWithNoSource() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/convertor")
                        .param("amount", "10")
                        .param("baseCurrency", "WRONG"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }
}
