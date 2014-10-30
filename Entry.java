/* 
 * Class:
 *    Entry
 * Author:
 *    Justin Hurley
 * Summary: 
 *    Holds a single entry in a Journal
 */   

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * Class: ScriptureFinder
 */
public class Entry {
       
    //private variables
    private ArrayList <Scripture> scriptures;
    private ArrayList <String> topic;
    private String date;
    private String text;
   
    /*
     * Check if an entry contains a specific topic
     */
    public boolean containsTopic(String testTopic){
        for(int i = 0; i < topic.size(); i++){
            if(testTopic.equals(topic.get(i))) {
                return true;                
            }
        }
        
        return false;
    }

    /*
     * Defualt Constructor
     */
    Entry() {
        scriptures = new ArrayList <Scripture>();
        topic = new ArrayList <String>();
	text = new String("");
    }

    /*
     * set up the Entry class
     */
    Entry(String date, String content){
        scriptures = new ArrayList <Scripture>();
        topic = new ArrayList <String>();
      
        this.date = new String(date);
        text = new String (content);
    }
   
    /*
     * returns true if the entry uses the scripture
     */
    boolean useScripture(String book) {
        for (int i = 0; i < scriptures.size(); i++) {
            if (scriptures.get(i).getBook().equals(book)){
                return true;
            }
        }
        return false;
    }
   
    /*
     * Scripture getter
     */
    List<Scripture> getScripture(){
        return scriptures;
    }
   
    /*
     * Topic getter
     */
    List<String> getTopic(){
        return topic;
     }
   
    /*
     * add content to an Entry
     */
    public void addContent(String content) {
        if (text.equals("")) {
            text = content;
        } else if(!content.equals("")){
            text = text + " " + content;
        }
    }

    /*
     * set date
     */
    public void setDate(String date) {
        this.date = date;
    }
	
    /*
     * Date getter
     */
    public String getDate(){
        return date;
    }
    
    /*
     * Text getter
     */
    public String getText(){
        return text;
    }

    /* 
     * add a topic to the Entry
     */
    public void addTopic(String topic) { 
       this.topic.add(topic);
    }
   
    /*
     * add a Entry to the scripture.
     */
    public void addScripture(String book, String chapter, String startVerse, String endVerse) {
        scriptures.add(new Scripture(book, chapter, startVerse, endVerse));
    }
   
    /*
     * write to an XML 
     */
    void writeXML(Element rootElement, Document doc) throws IOException {
        Element eEntry = doc.createElement("entry");
        eEntry.setAttribute("date", date);
		
        //write out scriptures
        for (int i = 0; i < scriptures.size(); i++) {
            Element eScripture = doc.createElement("scripture");
            eScripture.setAttribute("book", scriptures.get(i).getBook());
            eScripture.setAttribute("chapter", scriptures.get(i).getChapter());
			
            //special cases of no verse or no end verse
            if (!scriptures.get(i).getStartVerse().equals("")) {
                eScripture.setAttribute("startverse", scriptures.get(i).getStartVerse());
            }
		
            if (!scriptures.get(i).getEndVerse().equals("")) {
                eScripture.setAttribute("endverse", scriptures.get(i).getEndVerse());
            }

            eEntry.appendChild(eScripture);
        }
	
        //write out topics
        for (int i = 0; i < topic.size(); i++) {
            Element eTopic = doc.createElement("topic");
            eTopic.appendChild(doc.createTextNode(topic.get(i)));
            eEntry.appendChild(eTopic);
        }
	
        //write content
        Element eContent = doc.createElement("content");
        eContent.appendChild(doc.createTextNode(text));
        eEntry.appendChild(eContent);
		
        //attach node to root
        rootElement.appendChild(eEntry);
    }
	
    /*
     * write to a text file
     */
    void writeDoc(BufferedWriter bw) throws IOException {
        bw.write("-----" + "\n");
        bw.write(date + "\n");
        bw.write(text + "\n");
    }
	
    /*
     *old display function
     */
    void display(){
        System.out.println("Entry:");
        System.out.println("Date: " + date);
        System.out.println("Topic: ");
      
        for (int i = 0; i < topic.size(); i++)
            System.out.println(topic.get(i));
      
        System.out.println("scripture: ");
      
        for (int i = 0; i < scriptures.size(); i++) {
            System.out.println(scriptures.get(i).getScripture());
        }
        
        System.out.println("content:");
        System.out.println(text + "\n");
    }
}