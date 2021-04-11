Feature: Purchasing Pillow
  Scenario: 1_1.Credit Card Successful payment
    Given user is on Coco store Home page
    Given advertisement box is displayed with text "Midtrans Pillow" and price "Rp 20,000" with "BUY NOW" button
    Given user clicks on "BUY NOW" button
    Then Shopping cart page is displayed
    Then cost of "1" "Midtrans Pillow" is diplayed as "20000" in Amount field
    Then Total cost is is diplayed as "20,000"
    When user types "MyName" in "Name" input field on "Shopping cart" page
    And user types "MyEmail@somemail.com" in "Email" input field on "Shopping cart" page
    And user types "0981234567" in "Phone no" input field on "Shopping cart" page
    And user types "MyCity" in "City" input field on "Shopping cart" page
    And user types "MyAddress" in "Address" input text area on "Shopping cart" page
    And user types "123456" in "Postal Code" input field on "Shopping cart" page
    Then user clicks on "CHECKOUT" button
    Then summary page with logo "COCO STORE" and title "Order Summary" is displayed
    Then amount section in order summary page displays "amount" "Rp" "20,000"
    When user click on "CONTINUE" button on summary page
    Then payment page with logo "COCO STORE" and title "Order Summary" is displayed
    And list of available payment options are displayed
      |OCTO Clicks|Pay with Visa, MasterCard, JCB, or Amex|
      |Telkomsel Cash|Pay with Visa, MasterCard, JCB, or Amex|
      |Akulaku|Pay with Visa, MasterCard, JCB, or Amex|
      |KlikBCA|Pay with Visa, MasterCard, JCB, or Amex|
      |ShopeePay/other e-Wallets|Pay with Visa, MasterCard, JCB, or Amex|
      |BCA KlikPay|Pay with Visa, MasterCard, JCB, or Amex|
      |Alfa Group|Pay with Visa, MasterCard, JCB, or Amex|
      |Credit/Debit Card|Pay with Visa, MasterCard, JCB, or Amex|
      |GoPay/other e-Wallets|Pay with Visa, MasterCard, JCB, or Amex|
      |Indomaret|Pay with Visa, MasterCard, JCB, or Amex|
      |Danamon Online Banking|Pay with Visa, MasterCard, JCB, or Amex|
      |e-Pay BRI|Pay with Visa, MasterCard, JCB, or Amex|
      |ATM/Bank Transfer|Pay with Visa, MasterCard, JCB, or Amex|
      |LINE Pay e-cash \| mandiri e-cash|Pay with Visa, MasterCard, JCB, or Amex|