package com.tunisianet.iso;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;
import java.util.ArrayList;

public class TunisianetTest {

    private WebDriver driver;

    /**
     * Finds an element then clicks on it
     * @param selector
     */
    private void findAndClick(By selector) {
        WebElement element = driver.findElement(selector);
        element.click();
    }

    /**
     * Finds an input and types the text
     * @param selector
     * @param input
     */
    private void findAndSendKeys(By selector, String input) {
        WebElement element= this.driver.findElement(selector);
        element.sendKeys(input);
    }

    /**
     * Finds an input, types the text and submits
     * @param selector
     * @param input
     * @param submit
     */
    private void findAndSendKeys(By selector, String input, boolean submit) {
        WebElement element= this.driver.findElement(selector);
        element.sendKeys(input);
        if (submit)
            element.submit();
    }

    /**
     * Select an option from a list of options
     * @param optionSelector Identifies the row containing the option
     * @param checkboxSelector identifies the option
     * @param n the position of the element ( 1-indexed )
     * @throws NotFoundException
     */
    private void findNthOptionAndCheck(By optionSelector, By checkboxSelector,  int n) throws NotFoundException {
        ArrayList<WebElement> elements = (ArrayList<WebElement>) this.driver.findElements(optionSelector);
        if (elements.size() < n)
            throw new NotFoundException("Element not found");
        WebElement element = elements.get(n-1);
        element.findElement(checkboxSelector).click();
    }

    /**
     * -------------------------------- Start Test --------------------------------------------
     */

    /**
     * initializes the driver
     */
    public void init() {
        WebDriverManager.firefoxdriver().setup();
        this.driver = new FirefoxDriver();
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(12));
        this.driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
        this.driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(12));
    }

    /**
     * Login to Tunisianet account
     * @param email
     * @param password
     */
    private void login(String email, String password) {
        try {
            Thread.sleep(2000);
            this.findAndClick(By.cssSelector(".user-info"));
            this.findAndClick(By.cssSelector("a[title='Identifiez-vous']"));
            this.findAndSendKeys(By.name("email"), email);
            this.findAndSendKeys(By.name("password"), password);
            this.findAndClick(By.id("submit-login"));
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Search for an article
     * @param text
     */
    private void search(String text) {
        this.findAndSendKeys(By.id("search_query_top"), text, true);
    }

    /**
     * Starts the checkout process after selecting the first article
     */
    private void startCheckout() {
        this.findAndClick(By.cssSelector(".product-title a")); // select the first article
        this.findAndClick(By.cssSelector(".btn.btn-primary.add-to-cart")); // add it to cart
        this.findAndClick(By.linkText("Commander")); // starting the checkout
        this.findAndClick(By.cssSelector(".checkout.cart-detailed-actions.card-block"));
    }

    /**
     * Fills up addresses (if none already exist)
     * @param company
     * @param vat
     * @param address1
     * @param address2
     * @param postalCode
     * @param city
     * @param phone
     */
    private void fillAddresses(String company, int vat, String address1,
                                String address2, String postalCode, String city, int phone) {
        try {
            this.findAndSendKeys(By.name("company"), company);
            this.findAndSendKeys(By.name("vat_number"), String.valueOf(vat));
            this.findAndSendKeys(By.name("address1"), address1);
            this.findAndSendKeys(By.name("address2"), address2);
            this.findAndSendKeys(By.name("postcode"), postalCode);
            this.findAndSendKeys(By.name("city"), city);
            this.findAndSendKeys(By.name("phone"), String.valueOf(phone));
        }
        catch(NoSuchElementException e) {
            System.out.println("Address already exists, continuing...");
        }
        this.findAndClick(By.name("confirm-addresses")); // continue
    }

    /**
     * Fills up delivery
     * @param deliveryOption
     * @param message
     */
    private void fillDelivery(int deliveryOption, String message) {
        try {
            this.findNthOptionAndCheck(By.cssSelector(".row.delivery-option"), By.tagName("input"), deliveryOption); // select a delivery option
            if (message != null) {
                this.findAndSendKeys(By.id("delivery_message"), message); // input message
            }
            this.findAndClick(By.name("confirmDeliveryOption")); // continue
        }
        catch(NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fills up payment
     * @param paymentMethod
     */
    private void fillPayment(int paymentMethod) {
        try {
            this.findNthOptionAndCheck(By.cssSelector(".payment-option"), By.tagName("input"), paymentMethod); // select payment method
            this.findAndClick(By.cssSelector("#conditions-to-approve input")); // accept conditions
            this.findAndClick(By.cssSelector("#payment-confirmation button")); // finish checkout
        }
        catch(NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shuts the Driver down
     */
    private void shutdown() {
        try {
            Thread.sleep(3000); // wait a little before shutting down
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.driver.quit();
    }

    /**
     * Starts the Test
     */
    public void start() {
        this.init();
        this.driver.get("https://www.tunisianet.com.tn/");
        this.login("saief_zaneti@yahoo.com","saiefsaief");
        this.search("PC portable MacBook M1 13.3");
        this.startCheckout();
        this.fillAddresses("INSAT", 15, "Somewhere", "Somewhere again", "1150", "Ariana", 11223355);
        this.fillDelivery(2, "This is an optional message");
        this.fillPayment(2);
        this.shutdown();
    }
    public static void main(String[] args) {

        TunisianetTest test = new TunisianetTest();
        test.start();
    }
}
