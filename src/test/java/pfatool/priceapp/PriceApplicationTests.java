package pfatool.priceapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = PriceApplicationTestConfig.class)
@TestPropertySource("classpath:test.properties")
class PriceApplicationTests {

	@Test
	void contextLoads() {
	}

}
