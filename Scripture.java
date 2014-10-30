/* 
 * Class:
 *    Scripture
 * Author:
 *    Justin Hurley
 * Summary: 
 *    Holds a single scripture 
 */

/*
 * Class: Scripture
 */
public class Scripture {
    
    private String book;
    private String chapter;
    private String startVerse;
    private String endVerse;

    /*
     * Constructor
     */
    Scripture(String book, String chapter, String startVerse, String endVerse){
        this.book = book;
        this.chapter = chapter;
        this.startVerse = startVerse;
        this.endVerse = endVerse;
    }
    
    /*
     * getter
     */   
    String getBook(){
        return book;
    }

    /*
     * getter
     */
    String getChapter(){
        return chapter;
    }

    /*
     * getter
     */    
    String getStartVerse(){
        return startVerse;
    }

    /*
     * getter
     */
    String getEndVerse(){
        return endVerse;
    }
	
    /*
     * getter
     */
    String getScripture() {
        if(startVerse.equals("")){
            return book + " " + chapter;
        } else if(endVerse.equals("")) {
            return book + " " + chapter + ":" + startVerse;
        } else {
            return book + " " + chapter + ":" + startVerse + "-" + endVerse;
        }
    }
}