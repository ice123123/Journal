/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 *
 * @author Justin
 */
public class JournalTest {
    
    public JournalTest() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
    
    @Test //test Entry class
    public void testEntry1() throws Exception {
        Entry entry = new Entry("10-29-2014", "Testing content");
        
        Assert.assertEquals(entry.getText(), "Testing content");
        Assert.assertEquals(entry.getDate(), "10-29-2014");
        Assert.assertEquals(entry.getTopic(), "");
    }

    @Test //test Entry class
    public void testEntry2() throws Exception {
        Entry entry = new Entry();
        
        Assert.assertEquals(entry.getText(), "");
        Assert.assertEquals(entry.getDate(), "");
        Assert.assertEquals(entry.getTopic(), "");
    }
    
    @Test //test Entry class
    public void testEntry3() throws Exception {
        Entry entry = new Entry("","3 nephi 24:3 testing scripture");
        
        Assert.assertEquals(entry.getText(), "3 nephi 24:3 testing scripture");
        Assert.assertEquals(entry.getDate(), "");
        Assert.assertEquals(entry.getScripture().size(), "1");
    }
    
    @Test //test scripture class
    public void testScripture1() throws Exception {
       Scripture scripture = new Scripture("alma", "1", "2", "");
       
       Assert.assertEquals(scripture.getBook(), "alma");
       Assert.assertEquals(scripture.getChapter(), "1");
       Assert.assertEquals(scripture.getStartVerse(), "2");
    }
    
    @Test //test Journal class
    public void testJournal() throws Exception {
       SpirtualJournal journal = new SpirtualJournal();
       
       journal.addEntry("10-29-2014", "Testing content");
       
        Assert.assertEquals(journal.getEntryByIndex(0).getText(), "Testing content");
        Assert.assertEquals(journal.getEntryByIndex(0).getDate(), "10-29-2014");
    }
}
