package customer;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

	static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:15-alpine");
	CustomerService customerService;

	@BeforeEach
	void setUp() {
		pg.start();
		DBConnectionProvider connectionProvider = new DBConnectionProvider(
				pg.getJdbcUrl(),
				pg.getUsername(),
				pg.getPassword()
		);
		customerService = new CustomerService(connectionProvider);
	}

	@AfterEach
	void tearDown() {
		pg.stop();
	}

	@Test
	void shouldNotHaveCustomer() {
		int totalCustomers = customerService.getAllCustomers().size();

		assertEquals(0, totalCustomers);
	}

	@Test
	void shouldGetCustomers() {
		customerService.createCustomer(new Customer(1L, "George"));
		customerService.createCustomer(new Customer(2L, "John"));

		List<Customer> customers = customerService.getAllCustomers();
		assertEquals(2, customers.size());
	}
}