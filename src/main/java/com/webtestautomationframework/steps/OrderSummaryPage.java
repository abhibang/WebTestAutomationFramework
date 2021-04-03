package com.webtestautomationframework.steps;

import com.webtestautomationframework.utils.core.DriverFactory;
import cucumber.api.java.en.Given;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
public class OrderSummaryPage extends BasePage{

    @FindBy(xpath = "//iframe[@id='snap-midtrans']")
    @CacheLookup
    private WebElement ORDER_SUMMARY_IFRAME;

    @FindBy(xpath = "//div[@class='amount']//span[@class='text-amount-title']")
    @CacheLookup
    private WebElement AMOUNT_TITLE;

    @FindBy(xpath = "//div[@class='amount']//span[@class='text-amount-rp']")
    @CacheLookup
    private WebElement AMOUNT_SYMBOL;

    @FindBy(xpath = "//div[@class='amount']//span[@class='text-amount-amount']")
    @CacheLookup
    private WebElement AMOUNT;

    @FindBy(xpath = "//a[@class='button-main-content']")
    @CacheLookup
    private WebElement CONTINUE;

    @FindBy(xpath = "//div[@class='header-content']")
    @CacheLookup
    private WebElement HEADER_SUMMARY;
    //private static final String HEADER_SUMMARY = "//div[@class='header-content']";

    public OrderSummaryPage() {
        PageFactory.initElements(DriverFactory.getDriver(), this);
    }

    @Given("^summary page with logo \"([^\"]*)\" and title \"([^\"]*)\" is displayed$")
    public void userIsInIframe(String summaryPageLogoName, String pageName) {
        actions.switchToIframe(ORDER_SUMMARY_IFRAME);
        actions.waitUntilVisible(HEADER_SUMMARY);
        String actualLogoName = actions.findElement(By.xpath("//h1[@class='logo-store']"), HEADER_SUMMARY).getText().trim();
        //String actualLogoName = actions.waitUntilVisible(By.xpath(HEADER_SUMMARY+"//h1[@class='logo-store']")).getText().trim();
        Assert.assertEquals(summaryPageLogoName +" logo should be displayed on page",summaryPageLogoName,actualLogoName);
        String actualPageName = actions.findVisibleElement(By.xpath("//p[@class='text-page-title-content']"), HEADER_SUMMARY).getText().trim();
        //String actualPageName = actions.waitUntilVisible(By.xpath(HEADER_SUMMARY+"//p[@class='text-page-title-content']")).getText().trim();
        Assert.assertEquals(pageName +" summary should be displayed on page",pageName,actualPageName);
    }

    @Given("^amount section in order summary page displays \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
    public void adBoxIsDisplayed(String amountTitle, String amountSymbol, String amount) {
        //actions.switchToIframe(ORDER_SUMMARY_IFRAME);
        String expectedValues = amountTitle + " " + amountSymbol + " " + amount;
        String actualValues = actions.waitUntilVisible(AMOUNT_TITLE).getText().trim() + " " + actions.waitUntilVisible(AMOUNT_SYMBOL).getText().trim() + " " + actions.waitUntilVisible(AMOUNT).getText().trim() ;
        Assert.assertEquals("Amount section in Order summary page should display: "+expectedValues,expectedValues,actualValues);
    }

    @Given("^user click on \"([^\"]*)\" button on summary page$")
    public void clickOnNextButton(String buttonName) {
        //actions.switchToIframe(ORDER_SUMMARY_IFRAME);
        actions.click(CONTINUE);
        actions.switchToDefaultContent();
    }

}
