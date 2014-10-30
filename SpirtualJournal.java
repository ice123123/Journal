/* 
 * Class:
 *    SpirtualJournal
 * Author:
 *    Justin Hurley
 * Summary: 
 *    Stores Journal entries and displays entries.
 *    collaborated with Sam hibbard
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

/*
 * Class: SpirtualJournal
 */
public class SpirtualJournal {
    
    private String journalTitle;           /* Holds the tile of the journal */
    private StandardWorks standardWorks;   /* Contains all scriptures */
    private GospelTopic gospelTopics;      /* Contains all topics */
    private ArrayList<Entry> entry;        /* holds all Entries */
    
    /*
     * Default Constructor
     */
    SpirtualJournal() {
        Properties properties = new Properties();
        
        //open properties file
        try {
            InputStream inputStream = getClass().getResourceAsStream("/resources/path.properties");
            properties.load(inputStream);
        } catch (IOException ex) {
            System.out.println("Error opening file: path.properties");
            return;
        }
   
        //initialize private variables
        standardWorks = new StandardWorks(properties.getProperty("scriptures"));
        gospelTopics = new GospelTopic(properties.getProperty("topics")); 
        entry = new ArrayList<> ();
        journalTitle = "Title unknown";
   }

    /*
     * deletes a pattern from a string 
     */
    private String deleteExpression(String line, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(line);   
        return m.replaceAll("");
    }
    
    /*
     * Parse and add topics
     */
    private void parseAndAddTopics(Entry entry) {
        List <String> terms = gospelTopics.getTermsList();
	Properties topicAndTerms = gospelTopics.getTopicAndTerms();
		
        //parse topics
	for (int i = 0; i < terms.size(); i++) {
            if (entry.getText().contains(terms.get(i)) 
               && !entry.containsTopic(topicAndTerms.getProperty(terms.get(i)))) {
		entry.addTopic(topicAndTerms.getProperty(terms.get(i)));
            }
        }
    }
    
    /*
     * parse and add scriptures from Entry content
     */
    private void parseAndAddScripture(Entry entry) {
        String scripture;
        String book;
        String chapter;
        String verse;
		
        //pattern to find scriptures
        Pattern p = Pattern.compile("((\\d )?\\w+ chapter \\d+)|" 
            + "(\\d\\s)?[\\w&]+ \\d+[:]\\d+(-\\d+)?", Pattern.CASE_INSENSITIVE);
      
        //patterns to break up the scriptures into components.
        Pattern pBook = Pattern.compile("(\\w\\&\\w)|((\\d )?\\w+)");
        Pattern pChapter = Pattern.compile("(?<=((\\w\\&\\w)|((\\d )?\\w+))\\s)\\d+");
        Pattern pVerse = Pattern.compile("(?<=\\:)\\d+");
	
        Matcher m = p.matcher(entry.getText());				
		
        //search file for scriptures
        while (m.find()) {		
            //break up scriptures into book/chapter/verse
            scripture = deleteExpression(m.group(), "chapter ");
            Matcher mBook = pBook.matcher(scripture);
            Matcher mChapter = pChapter.matcher(scripture);
            Matcher mVerse = pVerse.matcher(scripture);
			
            mBook.find();
			
            //fix odd ball cases of no chapter or verse.
            if (mChapter.find()) {
            	chapter = mChapter.group();
            } else {
                chapter = "";
            }
			
            if (mVerse.find()) {
                verse = mVerse.group();
            } else {
                verse = "";
            }
			
            entry.addScripture(mBook.group(),chapter, verse, "");
        }
    }
	
    /*
     * parse text from a file into the program, populating the classes
     */
    public void parseTxt(String file){
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            Entry newEntry = null;
			
            //read in Entries
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                //start the new Entry
                if (line.contains("-----")) {
                    if (newEntry != null){
                        parseAndAddTopics(newEntry);
                        parseAndAddScripture(newEntry);

                        entry.add(newEntry);
                        newEntry = null;
                    }
					
                    newEntry = new Entry();
                    line = in.readLine();
                    newEntry.setDate(line);
                } else if (newEntry != null) {
                    //add the content to the entry
                    newEntry.addContent(line);				
                }
            }
			
            //catch any left over entries
            if (newEntry != null){
                parseAndAddTopics(newEntry);
                parseAndAddScripture(newEntry);

                entry.add(newEntry);
                newEntry = null;
            }
        } catch (IOException ex) {
            System.out.println("Error in reading file: " + file);
        }
    }
    
    /*
     * parses the XML file
     */
    public void parseXML(String sfile) {
        try {
            File fXmlFile = new File(sfile);
            System.out.println("Loading file \"" + sfile +"\"...\n");
   
            //set up to read in XML file
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
         
            //get the title
            journalTitle = new String(doc.getDocumentElement().getNodeName());
         
            //set up node list of entries
            NodeList nEntryList = doc.getElementsByTagName("entry");
            //String content;
      
            //go through all the entries
            for (int i = 0 ; i < nEntryList.getLength(); i++){
                
                Node nNode = nEntryList.item(i);
                Element eElement = (Element) nNode;

                Entry newEntry = new Entry();
                
                //set date
                String date = eElement.getAttribute("date");
                newEntry.setDate(date);
                
                //set up the node list of scriptures
                NodeList nChildNodes = nNode.getChildNodes();
            
                //go through all the child nodes
                for (int j = 0; j < nChildNodes.getLength(); j++) {
                    Node nChildNode = nChildNodes.item(j);
                    
                    if (nChildNode.getNodeName().equals("content")) {
                        String content = nChildNode.getTextContent();
                        content = content.trim();
                        content = content.replaceAll("\\n\\s+", " ");                       
                        newEntry.addContent(content); 
                    }
                    
                    //add a scripture
                    if (nChildNode.getNodeName().equals("scripture")){
                        Element eScriptureElement = (Element) nChildNode;
                        String startVerse = eScriptureElement.getAttribute("startverse");
                        String endVerse = eScriptureElement.getAttribute("endverse");

                        newEntry.addScripture(eScriptureElement.getAttribute("book"), eScriptureElement.getAttribute("chapter"), 
                           startVerse, endVerse);
                    }
               
                    //add a topic
                    if (nChildNode.getNodeName().equals("topic")) {
                        String topic = nChildNode.getTextContent();
                        topic = topic.trim();

                        newEntry.addTopic(topic);   
                    }
                } 
                entry.add(newEntry);
            }
        }
        catch (IOException ioex) {
            System.out.println("Error in Opening XML file! shutting down program");
        }
        catch (Exception ex){
            System.out.println("Error in parsing XML file! shutting down program");
        }
    }            

    /*
     * write to a XML file
     */
    public void writeXML(String file) {
        try{
            //prep for XML file building
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Journal");
            doc.appendChild(rootElement);
			
            //write each entry
            for (int i = 0; i < entry.size(); i++) {
                entry.get(i).writeXML(rootElement, doc);
            }
			
            //format XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
            //write to file
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(file));
            transformer.transform(source, result);
        }
        catch(Exception ex) {
            System.out.println("Error in writing to XML file");
        }
    }
	
    /*
     * write to a text file
     */
    public void writeDoc(String sfile) {
        try {
            File file = new File(sfile);
            PrintWriter writer = new PrintWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(writer);
				
            //write each entry
            for (int i = 0; i < entry.size(); i++) {
                entry.get(i).writeDoc(bw);
            }
				
            bw.close();
        } catch (IOException ex) {
            System.out.println("Error in writing to " + sfile + "text file");
        }
    }
   
    /*
     * old display from prev versions
     */
    public void oldDisplay(){
        System.out.println(journalTitle + "\n");
      
        for (int i = 0; i < entry.size(); i++)
            entry.get(i).display();
   }

    /*
     * Old display Entries from prev versions
     */
    public void display() {
        //print out scriptures
        displayByScriptures();
      
        //print out Topics
        displayByTopics();
   }

    /*
     * get the entry list by Scripture Sort
     */
    public List<String> getListByScriptures() {
        ArrayList<String> list = new ArrayList<>();
        
        //flag to ensure we only print the title/scripture out once per find
        boolean matchFlag;
      
        //itterate through scriptures
        for (int i = 0; i < standardWorks.getBookList().size(); i++) {
            matchFlag = false;
            for (int j = 0; j < entry.size(); j++) {
                if (entry.get(j).useScripture(standardWorks.getBookList().get(i))) {
                    if (matchFlag == false) {
                        list.add(standardWorks.getBookList().get(i));
                        matchFlag = true;
                    }
                    list.add(entry.get(j).getDate());
                }
            }
        }
        return list;
    }
    
    /*
     * get the entry list by Topic Sort
     */
    public List<String> getListByTopic() {
        
        ArrayList<String> list = new ArrayList<>();
        
        //flag to ensure we only print the title/scripture out once per find
        boolean matchFlag;
		
        //find Topics
        for (int i = 0; i < gospelTopics.getTopicList().size(); i++) {
            matchFlag = false;
            for (int j = 0; j < entry.size(); j++) {
                for (int k = 0; k < entry.get(j).getTopic().size(); k++){
                    if (gospelTopics.getTopicList().get(i).equalsIgnoreCase
                       (gospelTopics.getTopicAndTerms().getProperty(entry.get(j).getTopic().get(k).toLowerCase()))){
                        if (matchFlag == false) {
                            list.add(gospelTopics.getTopicList().get(i));
                            matchFlag = true;
                        }
                        list.add(entry.get(j).getDate());
                        break;
                    }
                }
            }
        }
        return list;
    }
    
    /*
     * Old "display entry dates by scripture reference" from prev versions
     */
    private void displayByScriptures() {
        //flag to ensure we only print the title/scripture out once per find
        boolean matchFlag;
      
        //print out scriptures
        for (int i = 0; i < standardWorks.getBookList().size(); i++) {
            matchFlag = false;
            for (int j = 0; j < entry.size(); j++) {
                if (entry.get(j).useScripture(standardWorks.getBookList().get(i))) {
                    if (matchFlag == false) {
                        System.out.println(standardWorks.getBookList().get(i));
                        matchFlag = true;
                    }
                    System.out.println(entry.get(j).getDate());
                }
            }
        }
    }
	
    /*
     * Old "display entry dates by topics" from prev versions
     */
    private void displayByTopics() {
        //flag to ensure we only print the title/scripture out once per find
        boolean matchFlag;
		
        //print out Topics
        for (int i = 0; i < gospelTopics.getTopicList().size(); i++) {
            matchFlag = false;
            for (int j = 0; j < entry.size(); j++) {
                for (int k = 0; k < entry.get(j).getTopic().size(); k++){
                    if (gospelTopics.getTopicList().get(i).equals
                       (gospelTopics.getTopicAndTerms().getProperty(entry.get(j).getTopic().get(k)))){
                        if (matchFlag == false) {
                            System.out.println(gospelTopics.getTopicList().get(i));
                            matchFlag = true;
                        }
                        System.out.println(entry.get(j).getDate());
                        break;
                    }
                }
            }
        }
    }
    
    /*
     * Delete an Entry from a Scripture sort given a specific index
     */
    public Entry deleteEntryByScriptureIndex(int index){
        Entry indexEntry = null;
        int listIndex = 0;

        boolean matchFlag;
      
        //search for entry.
        for (int i = 0; i < standardWorks.getBookList().size(); i++) {
            matchFlag = false;
            for (int j = 0; j < entry.size(); j++) {
                if (entry.get(j).useScripture(standardWorks.getBookList().get(i))) {
                    if (matchFlag == false) {
                        //wrong entry index
                        if(listIndex == index){
                            return null;
                        }
                            
                        listIndex++;
                        matchFlag = true;
                    }
                    //found entry
                    if (listIndex == index){
                        indexEntry = entry.get(j);
                        entry.remove(j);
                        return indexEntry;
                    }
                    listIndex++;
                }
            }
        }
        return indexEntry;
    }
    
    /*
     * Delete an Entry from a Topic sort given a specific index
     */
    public Entry deleteEntryByTopicIndex(int index){
        Entry indexEntry = null;
        
        //holds the current index through the list
        int listIndex = 0;
        
        //flag to ensure we only print the title/scripture out once per find
        boolean matchFlag;
		
        //go through Topics
        for (int i = 0; i < gospelTopics.getTopicList().size(); i++) {
            matchFlag = false;
            for (int j = 0; j < entry.size(); j++) {
                for (int k = 0; k < entry.get(j).getTopic().size(); k++){
                    if (gospelTopics.getTopicList().get(i).equalsIgnoreCase
                       (gospelTopics.getTopicAndTerms().getProperty(entry.get(j).getTopic().get(k).toLowerCase()))){
                        if (matchFlag == false) {
                            //user selected a title and not an entry
                            if(listIndex == index){
                                return null;
                            }
                            listIndex++;
                            matchFlag = true;
                        }
                        
                        //user selected this specific entry
                        if(listIndex == index){
                            indexEntry = entry.get(j);
                            entry.remove(j);
                            return indexEntry;
                        }
                        listIndex++;
                        break;
                    }
                }
            }
        }
        return null;
    }
    
    /*
     * Get dates of all entries
     */
    public List<String> getDates(){
        ArrayList<String> dates = new ArrayList<>();
        
        for(int i = 0; i < entry.size(); i++){
            dates.add(entry.get(i).getDate());
        }
        
       return dates; 
    }
    
    /*
     * Get entry from a specific index
     */
    public Entry getEntryByIndex(int index) {
        return entry.get(index);
    }
    
    /*
     * Delete an Entry from a specific index
     */
    public void deleteEntryByIndex(int index) {
        entry.remove(index);
    }
    
    /*
     * Add and parse new Entry
     */
    public void addEntry(String date, String text) {
        Entry newEntry = new Entry(date, text);
        parseAndAddTopics(newEntry);
        parseAndAddScripture(newEntry);
        entry.add(newEntry);
    }
}