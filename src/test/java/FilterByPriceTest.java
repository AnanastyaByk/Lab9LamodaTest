import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterByPriceTest {
    private static WebDriver driver = new ChromeDriver();

    @BeforeTest
    public void initWebDriver(){
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");
        driver.get("https://www.lamoda.by/c/15/shoes-women/");
    }

    @Test
    public void sortingByPriceTest() throws InterruptedException {
        String resultPrices = "";
        String minPriceForSorting = "2000";
        int lowestPrice = 0;
        List<Integer> allProductPricesList = new ArrayList<>();
        Thread.sleep(3000);

        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        driver.manage().window().maximize();
        javascriptExecutor.executeScript("window.scrollBy(0,500)");

        List<WebElement> sortButtons = driver.findElements(By.className("v-popper-target"));
        for (var i : sortButtons) {
            if (i.getText().equals("Цена")) {
                Actions actions = new Actions(driver);
                actions.moveToElement(i).click().build().perform();
                break;
            }
        }
        Thread.sleep(1500);

        WebElement inputMinPrice = driver.findElement(By.className("input__input"));
        inputMinPrice.sendKeys(minPriceForSorting);
        Thread.sleep(1000);

        WebElement submitButton = driver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[2]/div[1]/div/div/div/div[14]/div[2]/div[1]/div/div/div/div[2]/button"));
        submitButton.click();

        Thread.sleep(3000);
        List<WebElement> shoesPriceList = driver.findElements(By.className("x-product-card-description__microdata-wrap"));

        for (var i : shoesPriceList) {
            String mydata = i.getAttribute("innerHTML");
            if (mydata.contains("price-single")) {
                Pattern pattern = Pattern.compile("[0-9]{1}\s[0-9]{3,4}");
                Matcher matcher = pattern.matcher(i.getText());
                resultPrices = matcher.find() ? matcher.group() : "";
                String finalOldCleanPrice = resultPrices.replace(" ", "");
                allProductPricesList.add(Integer.parseInt(finalOldCleanPrice));
            }
            lowestPrice = allProductPricesList.stream().min(Integer::compare).get();
        }
        Assert.assertTrue(Integer.parseInt(minPriceForSorting) <= lowestPrice);
    }


    @AfterTest
    public void exitDriver(){
        driver.quit();
    }
}
