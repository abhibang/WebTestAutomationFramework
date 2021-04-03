package com.webtestautomationframework.steps;

import com.webtestautomationframework.utils.core.DriverFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentsPage extends BasePage{

    @FindBy(xpath = "//iframe[@id='snap-midtrans']")
    @CacheLookup
    private WebElement IFRAME;

    @FindBy(xpath = "//div[@class='header-content']")
    @CacheLookup
    private WebElement paymentSummary;


    @FindBy(xpath = "//div[@id='payment-list']//div[@class='list-content']")
    @CacheLookup
    private WebElement paymentParent;

    public PaymentsPage() {
        PageFactory.initElements(DriverFactory.getDriver(), this);
    }

    @Given("^payment page with logo \"([^\"]*)\" and title \"([^\"]*)\" is displayed$")
    public void userIsInIframe(String paymentsPageLogoName, String pageName) {
        actions.switchToIframe(IFRAME);
        actions.waitUntilVisible(paymentSummary);
        String actualLogoName = actions.findElement(By.xpath("//h1[@class='logo-store']"), paymentSummary).getText().trim();
        Assert.assertEquals(paymentsPageLogoName +" logo should be displayed on page",paymentsPageLogoName,actualLogoName);
        String actualPageName = actions.findVisibleElement(By.xpath("//p[@class='text-page-title-content']"), paymentSummary).getText().trim();
        Assert.assertEquals(pageName +" summary should be displayed on page",pageName,actualPageName);
    }

    @And("^list of available payment options are displayed$")
    public void listOfAvailablePaymentOptionsIsDisplayed(Map<String,String> expectedTitleVsCaption) {
         Assert.assertEquals(expectedTitleVsCaption,buildTitleVsCaptionMap());
    }

    private Map<String, String> buildTitleVsCaptionMap() {
        Map<String, String> titleVsCaption = new HashMap<>();
        List<WebElement> titles = actions.findElements(By.xpath("//div[@class='list-title text-actionable-bold']"),paymentParent);
        for(WebElement titleElement:titles){
            String title = titleElement.getText().trim();
            String caption = actions.findElement(By.xpath("//div[@class='list-title text-actionable-bold']/following-sibling::div"),titleElement).getText().trim();
            titleVsCaption.put(title,caption);
        }
        return titleVsCaption;
    }
}
