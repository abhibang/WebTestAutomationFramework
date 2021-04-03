package com.webtestautomationframework.steps;

import com.webtestautomationframework.utils.Timeout;
import com.webtestautomationframework.utils.core.DriverFactory;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShoppingCart extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCart.class);


    private static final String XPATH_CART = "//div[@class='cart-content buying']//div[@class='cart-section']";

    private static final String XPATH_CART_HEADER = "//div[@class='cart-content buying']//div[@class='cart-head']//span[contains(normalize-space(),'Shopping Cart')]";


    public ShoppingCart() {
        PageFactory.initElements(DriverFactory.getDriver(), this);
    }

    @Given("^Shopping cart page is displayed$")
    public void userIsInShoppingCartPage() {
        actions.waitUntilPageLoad(Timeout.THREE_SECONDS);
        //Assert.assertTrue("Shopping cart page should be displayed",actions.isVisible(shoppingCartPage));
        Assert.assertTrue("Shopping cart page should be displayed",actions.isVisible(By.xpath(XPATH_CART_HEADER),Timeout.VERY_SHORT));
    }

    @Then("^cost of \"([^\"]*)\" \"([^\"]*)\" is diplayed as \"([^\"]*)\" in Amount field$")
    public void costOfIsDiplayedAsInAmountField(String quantity, String productLabel, String amount){
        String quantityLocator =   XPATH_CART + "//td[normalize-space()='× "+quantity+"']";
        Assert.assertTrue("Quantity of product should be displayed as: "+quantity,actions.isVisible(By.xpath(quantityLocator),Timeout.VERY_SHORT));

        String productLocator = XPATH_CART + "//td[normalize-space()='"+productLabel+"']";
        Assert.assertTrue("Name of product should be displayed as: "+productLabel,actions.isVisible(By.xpath(productLocator),Timeout.VERY_SHORT));

        // this should never be a input filed as the user can change the amount, as its a demo site I'm skipping this assertion to check if hte field is non-editable
        String amountLocator  = XPATH_CART + "//input[@value='"+amount+"']";
        Assert.assertTrue("Amount of product should be displayed as: "+amount,actions.isVisible(By.xpath(amountLocator),Timeout.VERY_SHORT));

    }


    @Then("^Total cost is is diplayed as \"([^\"]*)\"$")
    public void totalCostIsIsDiplayedAs(String price) {
        String totalAmountLocator  =XPATH_CART+"//td[@class='total']/following-sibling::*[@class='amount']";
        String expectedPrice = DriverFactory.getDriver().findElement(By.xpath(totalAmountLocator)).getText();
        Assert.assertEquals("Total amount should be displayed as: " + price, expectedPrice, price);
    }


}
