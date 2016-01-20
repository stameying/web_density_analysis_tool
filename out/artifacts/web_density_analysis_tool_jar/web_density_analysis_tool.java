import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Name: Xiaoran Hu
 * Date: 1/19/2016
 * Email: huxiaora@usc.edu
 * Phone: 614-886-2780
 */
public class web_density_analysis_tool {
    private final String[] stripChars = { ":", ";", ",", ".", "-", "_", "^", "~", "(", ")", "[", "]", "'", "?", "|", ">", "<", "!", "\"" , "{","}" , "/", "*","&","+","$","@","%","`","#","=","\n"};
    private String webUrl;
    private Set<String> stopwords;
    private Set<String> jsstopwords;
    private Map<String,Integer> freqMap;
    private Map<String,Integer> jsfreqMap;
    private StringBuffer jsContent;

    /**
     * Default constructor
     * initiate all the following:
     * webUrl is url of the webpage
     * stopwords is a set contains all natural common stop words
     * jsstopwords is a set contains some common stop words for javascript
     * freqMap is a map stores pairs of word and its frequency
     * jsfreqMap is an additional map stores pairs of word and frequency that appear in javascript
     * jsContent is a buffer that stores the content of javascript of a page
     * */
    public web_density_analysis_tool(){
        this.webUrl = "";
        this.stopwords = new HashSet<>();
        this.jsstopwords = new HashSet<>();
        this.freqMap = new HashMap<>();
        this.jsfreqMap = new HashMap<>();
        this.jsContent = new StringBuffer();
    }


    /**
     * @param words a list of words
     * count the frequency for each word,
     * store the pair info into map,
     * git rid of normal common stop words
     * */
    private void countFrequency(List<String> words){
        for(int i = 0; i < words.size(); i++){
            String word = words.get(i);
            if (!stopwords.contains(word.toLowerCase())){
                if (freqMap.containsKey(word)){
                    freqMap.put(word,freqMap.get(word)+1);
                }else{
                    freqMap.put(word,1);
                }
            }
        }

    }

    /**
     * @param words a list of words
     * count the frequency for each word,
     * store the pair info into map,
     * git rid of javascript stop words
     * */
    private void countJSFrequency(List<String> words){
        for(int i = 0; i < words.size(); i++){
            String word = words.get(i);
            if (!jsstopwords.contains(word.toLowerCase()) && !stopwords.contains(word.toLowerCase())){
                if (jsfreqMap.containsKey(word)){
                    jsfreqMap.put(word,jsfreqMap.get(word)+1);
                }else{
                    jsfreqMap.put(word,1);
                }
            }
        }
    }


    /**
     * @param webContent html5 content as a string format
     * */
    private void displayWebContent(String webContent){
        System.out.println(webContent);
    }

    /**
     * @param k only words with frequency greater or equal to k times will be printed
     * */
    private void getKTopwords(int k){
        PriorityQueue<weightedWord> queue = new PriorityQueue<weightedWord>(new Comparator<weightedWord>(){
            public int compare(weightedWord w1, weightedWord w2){
                return Integer.compare(w2.frequency,w1.frequency);
            }
        });
        for (Map.Entry<String,Integer> entry: freqMap.entrySet()){
            weightedWord wordNode = new weightedWord(entry.getKey(),entry.getValue());
            queue.offer(wordNode);
        }

        PriorityQueue<weightedWord> queue2 = new PriorityQueue<weightedWord>(new Comparator<weightedWord>(){
            public int compare(weightedWord w1, weightedWord w2){
                return Integer.compare(w2.frequency,w1.frequency);
            }
        });
        for (Map.Entry<String,Integer> entry: jsfreqMap.entrySet()){
            weightedWord wordNode = new weightedWord(entry.getKey(),entry.getValue());
            queue2.offer(wordNode);
        }

        System.out.println("-------------- Major relevant (From HTML content)-----------------");
        prettyDisplay("word",-1);
        int majorCount = 0;
        for (int i = 0; i < queue.size(); i++){
            weightedWord node = queue.poll();
            if (node.frequency >= k || majorCount < 10){
                if (majorCount <= 10) prettyDisplay(node.word,node.frequency);
                majorCount++;
            }else break;

        }

        System.out.println("-------------- Minor relevant (From Javascript content)-----------------");
        prettyDisplay("word",-1);
        int minorCount = 0;
        for (int i = 0; i < queue2.size(); i++){
            weightedWord node = queue2.poll();
            if (node.frequency >= k || minorCount < 5){
                if (minorCount <= 10) prettyDisplay(node.word,node.frequency);
                minorCount++;
            }else break;
        }

        System.out.println("");
        System.out.println("Above words may describe the content of the page, but can be inaccurate");
    }

    /**
     * @param webUrl a url in string format
     * @return the content of the corresponding web page
     * */
    private String grabWeb(String webUrl){
        try {
            Document document = Jsoup.connect(webUrl).get();
            Elements scriptElements = document.getElementsByTag("script");
            for (Element element :scriptElements ){
                for (DataNode node : element.dataNodes()) {
                    jsContent.append(node.getWholeData());
                }
            }
            return document.text();
        }catch(Exception e){
            e.getMessage();
        }
        return null;
    }

    /**
     * @param content html content in string format
     * @return a list of strings as words
     * */
    private List<String> getWords(String content){
        List<String> resultList = new ArrayList<String>();
        String[] words = content.split(" ");
        for (String word: words){
            String cleanWord = wordHandler(word.trim());
            if (cleanWord.length() > 1 && cleanWord.length() < 12){
                resultList.add(cleanWord.toLowerCase());
            }
        }
        return resultList;
    }

    /**
     * Load external stop words txt file
     * */
    private void loadStopwords(){
        /*
        * Load stop words from first txt file
        * */
        try {
            Scanner sc = new Scanner(getClass().getResourceAsStream("stopwords.txt"));
            while (sc.hasNext()){
                String word = sc.next();
                stopwords.add(word);
            }
            sc.close();
        }catch(Exception err){
            System.out.println(err.getMessage());
        }

        /*
        * Load stop words from second txt file
        * */
        try {
            Scanner sc = new Scanner(getClass().getResourceAsStream("jsStopwords"));
            while (sc.hasNext()){
                String word = sc.next();
                jsstopwords.add(word);
            }
            sc.close();
        }catch(Exception err){
            System.out.println(err.getMessage());
        }
    }

    /**
     * @param word to be displayed
     * @param frequency of the word
     * */
    private void prettyDisplay(String word, int frequency){
        StringBuilder builder = new StringBuilder();
        if (frequency == -1){
            builder.append("|        WORD        | Frequency|");
            System.out.println(builder.toString());
            return;
        }
        builder.append("|");
        int oriSpace = 20-word.length();
        int space = oriSpace/2;
        while (space-- > 0) builder.append(" ");
        builder.append(word);
        space = oriSpace-oriSpace/2;
        while (space-- > 0) builder.append(" ");
        builder.append("|");
        oriSpace = 10-String.valueOf(frequency).length();
        space = oriSpace/2;
        while (space-- > 0) builder.append(" ");
        builder.append(frequency);
        space = oriSpace-oriSpace/2;
        while (space-- > 0) builder.append(" ");
        builder.append("|");
        System.out.println(builder.toString());
    }

    /**
     * Print header of output
     * */
    private void printHeader(){
        System.out.println("\n------------------------------------------------------------------");
        System.out.println("                            Web Url                               ");
        System.out.println(webUrl);
        System.out.println("------------------------------------------------------------------");
    }
    /**
     * get url address from terminal line
     * */
    private String readWebUrl(String[] args){
        String webUrl = args[0];
        return webUrl;
    }

    /**
     * @param word to be modified
     * @return a word that does not contain special character
     * */
    private String wordHandler(String word){
        String wordRes = word.trim();
        for(String specialStr: stripChars){
            wordRes = wordRes.replace(specialStr,"");
        }
        return wordRes;
    }

    /**
     * @param args arguments from terminal line
     * @return the web page content in string format
     * */
    private String preProcessing(String[] args){
        loadStopwords();
        webUrl = readWebUrl(args);
        // grab web info
        String webInfo = grabWeb(webUrl);
        int trial = 0;
        while (webInfo == null && trial < 10){
            webInfo = grabWeb(webUrl);
            trial++;
        }
        return webInfo;
    }

    /**
     * @param webInfo web content in string format
     * parse the large string into words
     * */
    private void parseWeb(String webInfo){
        List<String> wordList = getWords(webInfo);
        countFrequency(wordList);

        List<String> jswordList = getWords(this.jsContent.toString());
        countJSFrequency(jswordList);
    }


    /**
     * @param freqValue a value limit the words selected by its frequency
     * */
    private void analysis(int freqValue){
        getKTopwords(freqValue);
    }


    /**
     * Main function
     * */
    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("wrong number of arguments, the proper command should be java -jar assignment.jar weburl");
            return;
        }
        web_density_analysis_tool tool = new web_density_analysis_tool();

        // pre-processing
        String webInfo = tool.preProcessing(args);

        if (webInfo == null || webInfo.length() == 0){
            System.out.println("invalid web page, please try another");
            return;
        }
        // parse page html into words
        tool.parseWeb(webInfo);

        tool.printHeader();
        // select only words with frequency larger than 10
        tool.analysis(10);
    }

}
