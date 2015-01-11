package application;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;


// Inspired by: http://www.reddit.com/r/dailyprogrammer/comments/2nynip/2014121_challenge_191_easy_word_counting/

public class HtmlParser {
	// HTML URL in which to parse text from
	private static final String URL = "http://en.wikipedia.org/wiki/South_African_labour_law";
	// Used in detection of words and counting
	private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z]+");
	// Tree Map used to count words
	private static TreeMap<String, Integer> wordMap = new TreeMap<String, Integer>();
	// Allows or omits propositions/determiners (the, or, in, by, as, of, and,
	// etc.)
	private static final boolean ALL_WORDS = true;
	// Determines how many of the most common words to print out
	private static final int NUM_WORDS_DISPLAYED = 200;

	/**
	 * Counts the number of times words occur in a given string. Depends on
	 * WORD_PATTERN
	 */
	public static void countWords(String str) {
		Matcher m = WORD_PATTERN.matcher(str);
		while (m.find()) {
			String word = m.group().toLowerCase(); // ignore case!
			// haven't encountered this word yet (set count to 1)
			if (!wordMap.containsKey(word)) {
				wordMap.put(word, 1);
			}
			// word exists in hashMap, increase count (value) by 1
			else {
				wordMap.put(word, wordMap.get(word) + 1);
			}
		}
	}

	/**
	 * Calls helper method which sorts, then prints all key-value pairs in map.
	 */
	public static void printDescOrder() {
		// sort
		Map<String, Integer> sorted = sortByValue(wordMap);
		// print
		StringBuilder result = new StringBuilder();

		int counter = 0;
		for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
			result.append(entry.getKey() + ": " + entry.getValue() + "\n");
			if (++counter >= NUM_WORDS_DISPLAYED)
				break;
		}

		System.out.println(result.toString());
	}

	/**
	 * Sorts the given Map<String, Integer> in descending order based on values.
	 * Returns a sorted map. Credit to
	 * http://stackoverflow.com/a/13913206/3901262
	 */
	private static Map<String, Integer> sortByValue(
			Map<String, Integer> unsorted) {
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(
				unsorted.entrySet());

		// sort the list based on VALUES
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				// change below line if you wish to change ordering (asc/desc)
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// maintain insertion order with help of LinkedList
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	/**
	 * Connects to an HTML page (intended for Wikipedia) and retrieves the text
	 * in the main body (determined by CSS query id="mw-context-text"). Then it
	 * cleans it up a bit by removing some extra spacing.
	 */
	public static String getTextFromHtml(String html) {
		String result = "";
		if (html == null)
			return html;
		// Connect to page
		Document doc = null;
		try {
			doc = Jsoup.connect(html).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		doc.outputSettings(new Document.OutputSettings().prettyPrint(false));

		Element mainBodyOfText = doc.select("#mw-content-text").first();
		result = new HtmlToPlainText().getPlainText(mainBodyOfText);
		result = doc.html().replaceAll("\\\\n", "\n");
		result = Jsoup.clean(result, "", Whitelist.none(),
				new Document.OutputSettings().prettyPrint(false));
		// Removes everything after the REFERENCES header (helps eliminate a lot
		// of junk)
		result = StringUtils.substringBefore(result, "References[edit]");

		return result;
	}

	/**
	 * Remove HTML tags in a given string. Employs the Apache Commons Lang
	 * Library (commons-lang-3.3.0.1.jar)
	 */
	public static String removeHtmlTags(String str) {
		return StringEscapeUtils.unescapeHtml4(str);
	}

	/**
	 * Removes URLs from provided string. Credit to:
	 * http://stackoverflow.com/a/5713697/3901262
	 */
	public static String removeUrl(String str) {
		String regex = "\\b(https?|ftp|file|telnet|http|Unsure)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		return str.replaceAll(regex, "");
	}

	/**
	 * Removes all non-letter characters (keeps spaces). Anything that isn't a-z
	 * or A-Z is removed. Credit: http://stackoverflow.com/a/5008459/3901262
	 */
	public static String removeNonLetters(String str) {
		StringBuilder cleaned = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			char current = str.charAt(i);
			if (current == 0x20 || // space " "
					current >= 0x41 && current <= 0x5a || // A-Z
					current >= 0x61 && current <= 0x7a) // a-z{
				cleaned.append(current);
			// prevents unwanted fusion of words
			else
				cleaned.append(" ");
		}
		return cleaned.toString();
	}

	/**
	 * Removes all unwanted words that aren't real words. These can originate
	 * from removing non-letters (e.g. R&B becomes 'R B', 1800s becomes 's', 4th
	 * becomes 'th'). Propositions and determiners are also allowed/omitted
	 * here.
	 */
	public static String removeUnwantedWords(String str) {
		String result = str;
		ArrayList<String> unwanted = new ArrayList<String>();

		String[] singles = { "b", "c", "d", "e", "f", "g", "h", "j", "k", "l",
				"m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
				"y", "z", "th", "edit" };
		String[] prepositionsDeterminersConjunctions = { "abord", "about",
				"above", "across", "after", "against", "along", "amid",
				"among", "around", "as", "at", "before", "behind", "below",
				"beneath", "beside", "besides", "between", "beyond", "but",
				"by", "down", "during", "except", "for", "from", "in",
				"inside", "into", "like", "of", "onto", "over", "past", "per",
				"since", "than", "through", "to", "toward", "towards", "under",
				"underneath", "unlike", "until", "up", "via", "with", "within",
				"without", "a", "an", "the", "my", "your", "yours", "his",
				"her", "it", "its", "our", "their", "whose", "each", "every",
				"either", "neither", "some", "any", "no", "much", "many",
				"more", "most", "little", "less", "least", "few", "fewer",
				"fewest", "what", "whatever", "which", "whichever", "both",
				"all", "several", "enough", "and", "but", "or", "nor", "for",
				"yet", "so", "although", "because", "since", "unless", "are",
				"that", "is", "have", "been", "may", "also", "on", "be", "not",
				"was", "has", "can", "this", "were", "however", "these",
				"when", "would", "could", "he", "she", "such", "only", "other",
				"they", "there", "their", "will", "if", "must", "who", "whom",
				"out", "where", "had" };

		for (String letter : singles)
			unwanted.add(letter);

		unwanted.add("th"); // ie 4th --> th
		unwanted.add("edit"); // from [edit] all over Wikipedia article
		unwanted.add("et");
		unwanted.add("al"); // ie Johnson et al

		if (ALL_WORDS) {
			for (String word : prepositionsDeterminersConjunctions)
				unwanted.add(word);
		}
		for (String unwantedChars : unwanted) {
			result = result.replaceAll(" " + unwantedChars + " ", " ");
		}
		return result;
	}

	/**
	 * Calls every helper method on the given string, resulting in a string
	 * without any unwanted characters.
	 */
	public static String HtmlToPlainText(String htmlToParse) {
		String result = "";

		result = getTextFromHtml(htmlToParse).toLowerCase(); // 800ms
		result = removeHtmlTags(result); // 500ms
		result = removeUrl(result);
		result = removeNonLetters(result);
		result = removeUnwantedWords(result); // 950ms

		return result;
	}

	// public static void main(String[] args) {
	// String text = HtmlToPlainText(URL);
	// countWords(text);
	// printDescOrder();
	//
	// }

	// ABOVE is main for program to run to console, below is code to make pretty
	// :)
	// TODO Consider splitting this stuff up...

//	public HtmlParser() {
//		initializeUI();
//	}
//
//	private void initializeUI() {
//		JPanel pane = (JPanel) getContentPane();
//		GroupLayout gl = new GroupLayout(pane); // Never use GroupLayout manually again....
//		pane.setLayout(gl);
//
//		// =========== MODULES =========== 
//		
//		JButton aboutBtn = new JButton("About");
//		aboutBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				AboutDialog ad = new AboutDialog();
//				ad.setVisible(true);
//			}
//		});
//		
//		JButton calcBtn = new JButton("Calculate");
//		calcBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				System.out.println("butts");
//			}
//		});
//
//		
//		JLabel wikiText = new JLabel("Link to Wikipedia Article");
//		JLabel wordText = new JLabel("Number of Words");
//		
//		JTextField url = new JTextField(30);
//		
//		// =========== HORIZONTAL =========== 
//		
//		GroupLayout.SequentialGroup hGroup = gl.createSequentialGroup();
//		
//		hGroup.addGroup(gl.createParallelGroup().
//				addComponent(wikiText).
//				addComponent(url).
//				addComponent(wordText).
//				addComponent(aboutBtn));
//		
//		hGroup.addGroup(gl.createParallelGroup().
//				addComponent(calcBtn));
//
//		hGroup.addGroup(gl.createParallelGroup());
//		
//		gl.setHorizontalGroup(hGroup);
//
//		// =========== VERTICAL =========== 
//		
//		GroupLayout.SequentialGroup vGroup = gl.createSequentialGroup();
//		
//		vGroup.addGroup(gl.createParallelGroup(Alignment.BASELINE). // text label (wikipedia)
//				addComponent(wikiText));
//		vGroup.addGroup(gl.createParallelGroup(Alignment.BASELINE). // text field for URL
//				addComponent(url));
//		vGroup.addGroup(gl.createParallelGroup(Alignment.BASELINE). // text label (num words)
//				addComponent(wordText));
//		vGroup.addGroup(gl.createParallelGroup(Alignment.BASELINE). // about button
//				addComponent(aboutBtn).
//				addComponent(calcBtn));
//		
//		gl.setVerticalGroup(vGroup);
//
//		// =========== SETTINGS =========== 
//		
//		gl.setAutoCreateContainerGaps(true);
//		gl.setAutoCreateGaps(true);
//		
//		pack();
//
//		setTitle("HTML Parser v0.1");
//		setLocationRelativeTo(null); // centers window to screen
//		setResizable(false);
//		setDefaultCloseOperation(EXIT_ON_CLOSE); // allows X to close window
//	}

//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				HtmlParser window = new HtmlParser();
//				window.setVisible(true);
//			}
//		});
//	}

}
// http://zetcode.com/tutorials/javaswingtutorial/firstprograms/