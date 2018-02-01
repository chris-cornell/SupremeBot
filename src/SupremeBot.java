/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.jsoup.Jsoup;
import org.openqa.selenium.interactions.Actions;

/**
 *
 * @author Christopher
 */
public class SupremeBot 

{
    //=================================================================
    /*
    FINALS
    */
    private final String URL_BASE = "http://supremenewyork.com/shop/all/";
    private final String S_ITEM_NOT_FOUND_ERROR = "ERROR: ITEM NOT FOUND";
    private final String S_SOLD_OUT = "ERROR: SOLD OUT";
    private final String S_NO_CHECKOUT = "ERROR: CAN'T CHECKOUT";
    
    private Config cfg;
    /*
    VARIABLE FINALS
    */
    private String PROD_SECTION;
    private String PROD_NAME;
    private String PROD_COLOR;
    private String PROD_SIZE;
    
    private String CC_NUMBER, CVV_NUMBER, CC_MONTH, CC_YEAR;
    private String BILLING_ADDRESS, BILLING_ZIP, BILLING_CITY;
    private String BILLING_NAME, EMAIL, TELNUM;
    private String aRetry;
    private int version;
    private final File f;
    /*
    VARIABLES
    */
    private WebDriver driver;
    
    //=================================================================
    public SupremeBot(Config config)
    {
        cfg=config;        
        PROD_SECTION=cfg.getValue("bot.prod_section");
        PROD_NAME=cfg.getValue("bot.prod_name");
        PROD_COLOR=cfg.getValue("bot.prod_color");
        PROD_SIZE=cfg.getValue("bot.prod_size");
        driver = new ChromeDriver();
        f = new File("chromedriver.exe");
        if(!f.exists())
            error("chromedriver.exe not in directory...closing");
        try{
            aRetry=cfg.getValue("bot.AUTORETRY");
        } catch (NullPointerException e) { aRetry="0"; }
        version=0;
    }
    public SupremeBot
                   (String c_billingname, String c_email, String c_tel, 
                    String c_billing_address, String c_billingzip, String c_billingcity, 
                    String c_ccnumber, String c_cvv, String c_month, String c_year,
                    String c_prodsection, String c_prodname, String c_prodcolor, String c_prodsize, String c_autoretry)
            {
                PROD_SECTION = c_prodsection; PROD_NAME = c_prodname;
                PROD_COLOR = c_prodcolor;
                PROD_SIZE = c_prodsize;
                CC_NUMBER = c_ccnumber; CVV_NUMBER = c_cvv; CC_MONTH = c_month; CC_YEAR = c_year;
                BILLING_ADDRESS = c_billing_address; BILLING_ZIP = c_billingzip; BILLING_CITY = c_billingcity;
                BILLING_NAME = c_billingname; EMAIL = c_email; TELNUM = c_tel;
                aRetry="0";
                version=1;
                
                if(PROD_COLOR.equals("")) PROD_COLOR="NONE";
                if(PROD_SIZE.equals("")) PROD_SIZE = "NONE";
                
                f = new File("chromedriver.exe");
                if(!f.exists())
                    error("chromedriver.exe not in directory...closing");
                driver = new ChromeDriver();
            }
    private String notFound(int i) { return "Item " + PROD_NAME + " not found on Supreme yet...\nRetrying...\nAttempt #" + i; }
    public void start()
    {
        int count=0;
        if (version==1) 
        {
            if( aRetry.equals("0") ){
                while(!itemOnPage())
                {
                    count++;
                    JOptionPane.showMessageDialog(null, notFound(count));
                }
            }
        }
        try
        {
            buy();
        } 
        catch (InterruptedException e) { JOptionPane.showMessageDialog(null,"ERROR"); System.exit(0); } catch (Exception ex) {}
    }
    private void buy() throws InterruptedException, Exception
    {
        navItemPage();
        if(!PROD_COLOR.equalsIgnoreCase("NONE")) checkAllColors(PROD_COLOR);
        if(!PROD_SIZE.equalsIgnoreCase("NONE")) selectSize(PROD_SIZE);
        addCart();
        checkout();
        fillInfo();
        clickCheck();
        process();
    }
    private void navItemPage() throws InterruptedException
    {
        driver.get(URL_BASE + PROD_SECTION);
        WebElement itemLink;
        try{
        itemLink=driver.findElement(By.linkText(PROD_NAME));
        itemLink.click();
        
        } catch (org.openqa.selenium.NoSuchElementException e) { error(S_ITEM_NOT_FOUND_ERROR);}
        
        //========================================================================================
        //========================================================================================
    }
    private void addCart()
    {
        try{
                if(!PROD_COLOR.equals("NONE") || !PROD_SIZE.equals("NONE"))
                    Thread.sleep(1);
                else
                    Thread.sleep(500);
                driver.findElement(By.name("commit")).click();
            }   catch (org.openqa.selenium.NoSuchElementException e) { error(S_SOLD_OUT); } catch (InterruptedException ex) { error(S_SOLD_OUT); }
    }
    private void checkout() throws InterruptedException
    {
        try{
            Thread.sleep(250);
            driver.findElement(By.partialLinkText("checkout now")).click();
        } catch (org.openqa.selenium.NoSuchElementException e) { error(S_NO_CHECKOUT); }
    }
    private void fillInfo() throws InterruptedException
    {
        if(version==0)
        {
            try
            {
                elementByName("order[billing_name]").sendKeys(cfg.getValue("bot.billing_name"));
                elementByName("order[email]").sendKeys(cfg.getValue("bot.email"));
                elementByName("order[tel]").sendKeys(cfg.getValue("bot.tel"));
                elementByName("order[billing_address]").sendKeys(cfg.getValue("bot.billing_address"));
                elementByName("order[billing_zip]").sendKeys(cfg.getValue("bot.billing_zip"));
                elementByName("order[billing_city]").sendKeys(cfg.getValue("bot.billing_city"));
                elementByName("credit_card[cnb]").clear();
                elementByName("credit_card[cnb]").sendKeys(cfg.getValue("bot.cc_number"));
                elementByName("credit_card[month]").sendKeys(cfg.getValue("bot.cc_month"));
                //elementByName("credit_card[month]").sendKeys(cfg.get);
                elementByName("credit_card[year]").sendKeys(cfg.getValue("bot.cc_year"));
                elementByName("credit_card[vval]").sendKeys(cfg.getValue("bot.cvv_code"));
            } catch ( org.openqa.selenium.NoSuchElementException e) { error(S_ITEM_NOT_FOUND_ERROR);}  
        }
        else if (version==1)
        {
            try
            {
                elementByName("order[billing_name]").sendKeys(BILLING_NAME);
                elementByName("order[email]").sendKeys(EMAIL);
                elementByName("order[tel]").sendKeys(TELNUM);
                elementByName("order[billing_address]").sendKeys(BILLING_ADDRESS);
                elementByName("order[billing_zip]").sendKeys(BILLING_ZIP);
                elementByName("order[billing_city]").sendKeys(BILLING_CITY);
                elementByName("credit_card[cnb]").clear();
                elementByName("credit_card[cnb]").sendKeys(CC_NUMBER);
                elementByName("credit_card[month]").sendKeys(CC_MONTH);
                elementByName("credit_card[year]").sendKeys(CC_YEAR);
                elementByName("credit_card[vval]").sendKeys(CVV_NUMBER);
            } catch (org.openqa.selenium.NoSuchElementException e) { error(S_ITEM_NOT_FOUND_ERROR);}
        }
        else
            error("VERSION ERROR!");
    }
    private void error(String s)
    {
        JOptionPane.showMessageDialog(null, s);
        driver.quit();
        System.exit(0);
    }
    private WebElement elementByName(String n)
    {
        return driver.findElement(By.name(n));
    }
    public boolean itemOnPage()
    {
        try{
            return Jsoup.connect(URL_BASE + PROD_SECTION).get().text().contains(PROD_NAME);
        } catch (IOException e) { return false; }
    }
    private void checkAllColors (String color) throws InterruptedException
    {
        Thread.sleep(500);
        boolean foundColor=false;
        List<WebElement> lists=driver.findElements(By.tagName("ul"));
        {
            for(WebElement list:lists)
                {
                    try{
                        if(!list.getAttribute("id").startsWith("nav"))
                        {
                            List<WebElement> items = list.findElements(By.tagName("li"));
                            for (WebElement li : items)
                            {
                                List<WebElement> linksInItem = li.findElements(By.tagName("a"));
                                for(WebElement aLink : linksInItem)
                                {
                                    if(aLink.getAttribute("data-style-name").equals(color))
                                    {
                                        
                                        aLink.click();
                                        foundColor=true;
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (org.openqa.selenium.ElementNotVisibleException e) {}
                }
        }
        if(!foundColor)
        {
            error("COLOR NOT FOUND");
            driver.quit();
        }
    }
    private void selectSize(String size)
    {
        WebElement sizeSelect = driver.findElement(By.id("size"));
        sizeSelect.sendKeys(size);
    }
    private void clickCheck() throws InterruptedException
    {
        Actions a = new Actions(driver);
        WebElement box = driver.findElements(By.className("icheckbox_minimal")).get(1);
        a.moveToElement(box);
        a.perform();
        driver.findElement(By.cssSelector(".icheckbox_minimal.hover")).click();
    }
    private void process()
    {
        driver.findElement(By.name("commit")).click();
        JOptionPane.showMessageDialog(null, "Close this window once processed...");
        driver.quit();
        System.exit(0);
    }
    public void closeDriver() { driver.quit(); }
    
    
}
