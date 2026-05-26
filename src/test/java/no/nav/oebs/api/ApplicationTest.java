package no.nav.oebs.api;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Enhetstest for loading av applikasjonskonteksten.
 */
@SpringBootTest
class ApplicationTest {

	@Test
	void applicationContextShouldLoad() {
		assertNotNull("Application context loaded successfully");
	}

}