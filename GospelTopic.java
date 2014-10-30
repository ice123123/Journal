/*
 * Class:
 *    GospelTopic
 * Author:
 *    Justin Hurley
 * Summary: 
 *    Holds all the possible Gospel topics
 */
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/*
 *Class: GospelTopic
 */
public class GospelTopic {
       
    private Properties topicAndTerms;
    private ArrayList<String> topicList;
    private ArrayList<String> terms;

    /*
     * Constructor
     */
    GospelTopic(String path){
        topicList = new ArrayList<String>();
        topicAndTerms = new Properties();
        terms = new ArrayList<String>();
        parseFile(path);
    }

    /*
     *Topic list getter
     */
    public List<String> getTopicList() {
        return topicList;
    }
   
    /*
     * term list getter
     */
    public List<String> getTermsList() {
        return terms;
    }
	
    /*
     * Topic and terms getter
     */
    public Properties getTopicAndTerms() {
        return topicAndTerms;
    }   
   
    /*
     * parse the properties file
     */
    private void parseFile(String path) {
        BufferedReader in;
        String topic;
        String term;

        try {
            in = new BufferedReader(new FileReader(path));
         
            //patterns for topics and terms
            Pattern patternTopic = Pattern.compile("^.*(?=(\\:))");
            Pattern patternTerm = Pattern.compile("(?<=(\\:))(\\w+(\\s?))+|(?<=(\\,))(\\w+(\\s?))+");
         
            //find scriptures
            for (String line = in.readLine(); line != null; line = in.readLine()){
                Matcher matcherTopic = patternTopic.matcher(line);
                Matcher matcherTerm = patternTerm.matcher(line);
            
                //topic finder
                matcherTopic.find();
                topic = matcherTopic.group();
                topicList.add(topic);
            
                //insert topic & terms
                while (matcherTerm.find()) {
                term = matcherTerm.group();
                topicAndTerms.put(term ,topic);
		terms.add(term);
                }
            } 
        } catch (IOException ex) {
        System.out.println("Error opening file: " + "terms.txt");
        }
    }
}