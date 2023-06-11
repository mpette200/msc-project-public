package pfatool.priceapp;

import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pfatool.forecaster.ForecastInfo;
import pfatool.forecaster.PriceForecaster;

import java.time.OffsetDateTime;

import org.hamcrest.Matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = PriceApplicationTestConfig.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
public class WebApiTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private PriceForecaster mockForecaster;

    @MockBean
    private MarkdownRenderer mockMarkdownRenderer;

    @Test
    void testForecastCallsGetSupportedCategories() throws Exception {
        mockMvc.perform(get(
                "/api/forecast"));
        verify(mockForecaster).getSupportedCategories();
    }

    @Test
    void testForecastCallsIsSupportedCategory() throws Exception {
        mockMvc.perform(get(
                "/api/forecast?category={cat}",
                "any__category"));
        verify(mockForecaster).isSupportedCategory("any__category");
    }

    @Test
    void testForecastCallsInvalidCategory() throws Exception {
        when(mockForecaster
                .isSupportedCategory("unsupported__category"))
                .thenReturn(false);
        mockMvc.perform(get(
                "/api/forecast?category={cat}",
                "unsupported__category"));
        verify(mockForecaster).isSupportedCategory("unsupported__category");
        verify(mockForecaster, never()).makeForecast("unsupported__category");
    }

    @Test
    void testForecastCallsWithValidCategory() throws Exception {
        when(mockForecaster
                .isSupportedCategory("valid__category")
        ).thenReturn(true);
        when(mockForecaster
                .makeForecast("valid__category")
        ).thenReturn(new ForecastInfo(1.1, 2.2, OffsetDateTime.parse("2022-04-04T00:00+00:00"),
                7, "valid__category"));
        mockMvc.perform(get(
                "/api/forecast?category={cat}",
                "valid__category"));
        verify(mockForecaster).isSupportedCategory("valid__category");
        verify(mockForecaster).makeForecast("valid__category");
    }

    @Test
    void testIndexLoadsDummy() throws Exception {
        mockMvc.perform(get("/"));
        verify(mockMarkdownRenderer).render("Dummy index.md");
    }

    @Test
    void testDocsLoadsOthers() throws Exception {
        mockMvc.perform(get(
                "/docs/{x}",
                "others.md"));
        verify(mockMarkdownRenderer).render("Dummy others.md");
    }

    @Test
    void testDocsListsDirectory() throws Exception {
        mockMvc.perform(get("/docs"));
        ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        verify(mockMarkdownRenderer).render(arg.capture());
        assertThat(arg.getValue(), Matchers.containsString("(/docs/index.md)"));
        assertThat(arg.getValue(), Matchers.containsString("(/docs/others.md)"));
    }

}
