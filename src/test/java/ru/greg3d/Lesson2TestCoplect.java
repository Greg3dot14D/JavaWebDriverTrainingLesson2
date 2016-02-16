package ru.greg3d;

import org.testng.annotations.*;

import ru.greg3d.browsers.BrowserDriver;
import ru.greg3d.util.WaitUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

public class Lesson2TestCoplect extends TestBase {
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = new StringBuffer();
	private final String _CLEAR_KEYS = Keys.CONTROL + "a" + Keys.DELETE;
	
	public static Logger LOG = LoggerFactory.getLogger(Lesson2TestCoplect.class);

	private HashMap<String, fieldLocator> _FilmFields = new HashMap<String, fieldLocator>();
	
	// типы полей и поля, с которыме будем работать при заполнении карточки
	private enum typeof {
		date, string, time, num;
		
		public static typeof typeofForName(String type) throws IllegalArgumentException {
			for (typeof t : values()) {
				if (t.toString().equalsIgnoreCase(type.toString())) {
					return t;
				}
			}
			throw typeofNotFound(type);
		}

		private static IllegalArgumentException typeofNotFound(String outcome) {
			return new IllegalArgumentException(("Invalid typeof [" + outcome + "]"));
		}
	};
	
	private class fieldLocator {
		private By by;
		private typeof type;

		public fieldLocator(By by, typeof type) {
			this.by = by;
			this.type = type;
		}

		public By getBy() {
			return this.by;
		}

		public typeof getType() {
			return this.type;
		}
	}

	// перечень полей (ключ / [By / тип поля]), которые скрипт будет заполнять, при добавлении новой карточки
	private void initFilmFields() {
		_FilmFields.put("Tytle", new fieldLocator(By.name("name"), typeof.string));
		_FilmFields.put("Year", new fieldLocator(By.name("year"), typeof.date));
		_FilmFields.put("Duration", new fieldLocator(By.name("duration"), typeof.time));
		_FilmFields.put("Rating", new fieldLocator(By.name("rating"), typeof.num));
	}

	
	// Перечень полей (ключей), необходимых для заполнения формы с новой карточкой фильма 
	@DataProvider
    private Object[][] necessaryFilmFieldsDataProvider() {
        return new Object[][]{
            {new String ("Tytle")},
            {new String ("Year")}
        	};
        };	
	
	@BeforeClass
	public void DoLoginTest() throws Exception {
		driver.get(baseUrl + "/php4dvd/");

		WebElement userNameElement = driver.findElement(By.id("username"));
		// userNameElement.clear();
		userNameElement.sendKeys(_CLEAR_KEYS);
		userNameElement.sendKeys("admin");

		WebElement passwordElement = driver.findElement(By.name("password"));
		passwordElement.sendKeys(_CLEAR_KEYS);
		passwordElement.sendKeys("admin");

		WebElement submitElement = driver.findElement(By.name("submit"));
		submitElement.click();

		initFilmFields();
	}

	// выходим по href в основное окно
	// выполняем перед всеми нашими тестами
	@BeforeMethod
	private void goHome() {
		// выходим по href в основное окно
		// #movie>.maininfo_full>h2
		WebElement homeHref = driver.findElement(By.xpath("//a[contains(text(),'Home')]"));
		homeHref.click();
	}

	// Проверка добавления записи только с необходимыми полями
	@Test
	public void addCorrectRecord() {
		// Сохранили число записей до добавления фильма
		int recordsCount = driver.findElement(By.id("results")).findElements(By.cssSelector("a")).size();
		// добавляем новую запись
		addRecord(_FilmFields);
		
		// проверяем - перешли ли с ссылки ? http://localhost/php4dvd/?go=add
		Assert.assertNotEquals(BrowserDriver.getCurrentUrl(), baseUrl + "/php4dvd/?go=add", "Record with Correct data was not added");

		// выходим по href в основное окно
		goHome();

		// сравнили новое число записей с recordsCount
		Assert.assertEquals(driver.findElement(By.id("results")).findElements(By.cssSelector("a")).size(),
				recordsCount + 1, "records count was not changed");
	}

	// Проверка добавления записи только с незаполненными необходимыми полями
	@Test(dataProvider = "necessaryFilmFieldsDataProvider")
    public void addWrongRecord(final String fieldToRemove) {
		
		// удаляем из списка необходимых для заполнения полей, одно из полей
		HashMap<String, fieldLocator> tempFieldMap = ((HashMap<String, fieldLocator>) _FilmFields.clone());
		tempFieldMap.remove(fieldToRemove);
		
		// добавляем запись без одного необходимого поля
		addRecord(tempFieldMap);
		// проверяем - не перешли ли с ссылки ? http://localhost/php4dvd/?go=add
		Assert.assertEquals(BrowserDriver.getCurrentUrl(), baseUrl + "/php4dvd/?go=add", "Wrong Record was added");
	}
        
	public void addRecord(HashMap<String, fieldLocator> fieldsMap) {

		// нажали кнопку Добавить
		driver.findElement(By.cssSelector("img[title='Add movie']")).click();

		// Заполнили необходимые поля
		for (String key : fieldsMap.keySet()) {
			WebElement element = driver.findElement(fieldsMap.get(key).getBy());

			switch (fieldsMap.get(key).getType().toString()) {
			case "string":
				element.sendKeys("new " + element.getAttribute("name").toString() + "_"
						+ Calendar.getInstance().getTimeInMillis());
				break;
			case "date":
				element.sendKeys("" + (1950 + new Random().nextInt(65)));
				break;
			case "time":
				element.sendKeys("" + (60 + new Random().nextInt(60)));
				break;
			case "num":
				element.sendKeys("" + (new Random().nextInt(10)));
				break;
			}
		}

		// сохранили запись
		driver.findElement(By.cssSelector("img[title='Save']")).click();
	}

	// Проверка удаления записи
	@Test
	public void deleteRecord() {
		// Сохранили число записей до удаления фильма
		int recordsCount = driver.findElements(By.xpath("//div[@id='results']/a")).size();
		// Assert.assertEquals(0, recordsCount, "nothin to delete");
		if (recordsCount == 0) {
			LOG.warn("nothin to delete");
			return;
		}

		// выбираем первую запись в списке
		WebElement firstRecord = driver.findElement(By.xpath("//div[@id='results']/a/div"));
		firstRecord.click();

		// удаляем запись
		WebElement deleteButton = driver.findElement(By.cssSelector("img[title='Remove']"));
		deleteButton.click();

		// Ловим аллерт
		closeAlertAndGetItsText();

		// проверяем число записей в гриде
		Assert.assertEquals(driver.findElements(By.xpath("//div[@id='results']/a")).size(), recordsCount - 1,
				"record was not delete");
		LOG.info("Done");
	}

	@Test
	public void testSearchFirstRecord(){
		// взять название первой записи
		if(driver.findElements(By.cssSelector(".title")).size() == 0){
			LOG.warn("grid is empty? nothing to search");
			return;
		}
		String firstRecordTitle = driver.findElements(By.cssSelector(".title")).get(0).getText();
				
		// ввести название первой записи в поле поиска
		driver.findElement(By.id("q")).sendKeys(_CLEAR_KEYS);
		driver.findElement(By.id("q")).sendKeys(firstRecordTitle + Keys.RETURN);
		WaitUtils.WaitPageIsNotActive(driver);
		WaitUtils.WaitPageIsActive(driver);
		// проверить, что число записей > 0
		Assert.assertNotEquals(driver.findElements(By.cssSelector(".title")).size(),0);
		// проверить, что первая запись = искомой
		Assert.assertEquals(driver.findElement(By.cssSelector(".title")).getText(),firstRecordTitle);
		LOG.info("Done");
	}
	
	@Test
	public void testUnSearchFirstRecord(){
		// взять название первой записи
		if(driver.findElements(By.cssSelector(".title")).size() == 0){
			LOG.warn("grid is empty? nothing to search");
			return;
		}
		String firstRecordTitle = driver.findElements(By.cssSelector(".title")).get(0).getText();
		
		String reveseTitle = new StringBuilder(firstRecordTitle).reverse().toString();
		
		// ввести инвертированное название первой записи в поле поиска
		driver.findElement(By.id("q")).sendKeys(_CLEAR_KEYS);
		driver.findElement(By.id("q")).sendKeys(reveseTitle + Keys.RETURN);
		WaitUtils.WaitPageIsNotActive(driver);
		WaitUtils.WaitPageIsActive(driver);
		// проверить, что число записей = 0
		if(driver.findElements(By.cssSelector(".title")).size() != 0)
		// проверить, что первая запись = искомой
			Assert.assertEquals(driver.findElements(
					By.cssSelector(".title")).get(0).getText(),
					reveseTitle,
					"filter do not works on wrong data"
					);
	}	
	
	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private String closeAlertAndGetItsText() {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			if (acceptNextAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			LOG.debug("closeAlertAndGetItsText -" + alertText);
			return alertText;
		} 
		finally {
			acceptNextAlert = true;
			// дожидаемся обновления страницы
			WaitUtils.WaitPageIsNotActive(driver);
			WaitUtils.WaitPageIsActive(driver);
		}
	}
}
