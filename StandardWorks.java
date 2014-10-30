/* 
 * Class:
 *    StandardWorks
 * Author:
 *    Justin Hurley
 * Summary: 
 *    Holds all possible scriptures
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
 * Class: StandardWorks
 */
public class StandardWorks {
    
    private Properties booksAndChapters;
    private ArrayList <String> books;
   
    /*
     * Defualt Constructor
     */
    StandardWorks(String path) {
        booksAndChapters = new Properties();
        books = new ArrayList <String>();
        parsefile(path);
   }
   
    /*
     * Book List getter
     */
    public List<String> getBookList(){
        return books;
   }
   
    /*
     * parse the properties file
     */
    private void parsefile(String path) {
        BufferedReader in;
        String book;
        String chapter;

        try {
            in = new BufferedReader(new FileReader(path));
         
            Pattern patternBook = Pattern.compile("^.*(?=(\\:))");
            Pattern patternChapter = Pattern.compile("(?<=(\\:)).*");
         
            //find scriptures
            for (String line = in.readLine(); line != null; line = in.readLine()){
                Matcher matcherChapter = patternBook.matcher(line);
                Matcher matcherVerse = patternChapter.matcher(line);
            
                matcherChapter.find();
                book = matcherChapter.group();
                books.add(book);
            
                matcherVerse.find();
                chapter = matcherVerse.group();
            
                booksAndChapters.put(book, chapter);
            }
        } catch (IOException ex){
            System.out.println("Error opening file: " + "booksAndChapters.txt");
        }
    }
}