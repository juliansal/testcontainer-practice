package selenium;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.wait.strategy.DockerHealthcheckWaitStrategy;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TheInternetTest implements TestWatcher {
	public static RemoteWebDriver driver;
	public static DockerImageName image = DockerImageName.parse("selenium/standalone-chrome:4.21.0");

	@Container
	public static BrowserWebDriverContainer chrome = null;

	@BeforeAll
	public static void beforeAll() {
		chrome = new BrowserWebDriverContainer<>(image)
				.withCapabilities(getOptions())
				.withExposedPorts(8080, 8081)
				.waitingFor(new DockerHealthcheckWaitStrategy())
				.waitingFor(new HttpWaitStrategy());
		chrome.start();
//		driver = new RemoteWebDriver(chrome.getSeleniumAddress(), getOptions());
		driver = new ChromeDriver(ChromeDriverService.createDefaultService(), getOptions());
	}

	@AfterAll
	public static void afterAll() {
		if (driver != null) {
			driver.quit();
		}
		chrome.close();
	}

	public static ChromeOptions getOptions() {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");

		return chromeOptions;
	}

	@Test
	public void shouldVisitTheInternet() {
		driver.navigate().to("http://the-internet.herokuapp.com");

		assertThat(driver.getTitle())
				.endsWith("The Internet");
	}

	@Test
	public void shouldLogIntoFormAuth() {
		driver.navigate().to("http://the-internet.herokuapp.com");

		driver
				.findElement(By.linkText("Form Authentication"))
				.click();

		assertThat(driver.findElement(By.tagName("h2")).getText())
				.isEqualTo("Login Page");
	}

	@Test
	void shouldClickOnCheckboxes() {
		driver.navigate().to("http://the-internet.herokuapp.com");

		driver
				.findElement(By.linkText("Checkboxes"))
				.click();

		assertThat(driver.findElement(By.tagName("h3")).getText())
				.isEqualTo("Checkboxes!!");
	}

	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		System.out.println("This test failed!!!");

		File screenFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-HH:mm:ssms");
		try {
			FileUtils.copyFile(screenFile, new File("screenshot-" + dateFormat.format(new Date()) + ".png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void testSuccessful(ExtensionContext context) {
		System.out.println("This test pass.");
	}

}
