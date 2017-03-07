/* Name: SpamFilter
 * Author: Devon McGrath
 * Description: This class acts as the spam filter that decides whether or not
 * a file should be considered spam and what percentage of the file is
 * considered spam.
 * 
 * Version History:
 * 1.0 - 02/01/2017 - Initial version - Devon McGrath
 */

package program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SpamFilter {
	
	/** The directory in the {@link #root} directory that contains testing
	 * files. */
	public static final String TEST_DIR = "test";

	/** The directory in the {@link #root} directory that contains training
	 * files. */
	public static final String TRAIN_DIR = "train";
	
	/** The directory in the training and testing folders (see 
	 * {@link #TRAIN_DIR} and {@link #TEST_DIR}) which contain spam files. */
	public static final String SPAM_DIR = "spam";
	
	/** The first directory in the training and testing folders (see 
	 * {@link #TRAIN_DIR} and {@link #TEST_DIR}) which contain ham files. */
	public static final String NON_SPAM_DIR = "ham";
	
	/** The second directory in the training folder which contains more ham
	 * files. */
	public static final String NON_SPAM_DIR_2 = "ham2";
	
	/** The threshold that the spam probability must be greater than for a
	 * particular file to be considered spam. */
	public static final double SPAM_THRESHOLD = 0.6;
	
	/** The root directory from which the spam filter will operate. */
	private File root;
	
	/** The map of words that correspond to a {@link Frequency}. */
	private Map<String, Frequency> wordMap;
	
	/** The number of spam files checked in training. */
	private int spamFiles;
	
	/** The number of ham files checked in training. */
	private int hamFiles;
	
	/** The number of guesses that classified e-mails as spam. */
	private int spamGuesses;
	
	/** The number of guesses that correctly classified spam e-mails. */
	private int correctSpamGuesses;
	
	/** The number of correctly classified e-mails during testing. */
	private int correctGuess;
	
	/** Constructs a spam filter from the current working directory. */
	public SpamFilter() {
		this(new File("."));
	}
	
	/**
	 * Constructs a spam filter with a root directory.
	 * 
	 * @param root - the root directory.
	 */
	public SpamFilter(File root) {
		setRoot(root);
		this.wordMap = new TreeMap<>();
	}
	
	/**
	 * <b><em>train</em></b>
	 * 
	 * <p>This method trains the spam filter by creating a map of the words
	 * with a frequency associated with them. The training directory (
	 * root/{@value #TRAIN_DIR}) has three directories in it that have spam and
	 * non-spam e-mails.</p>
	 * 
	 * @see {@link #test()}, {@link #SPAM_DIR}, {@link #NON_SPAM_DIR},
	 * {@link #NON_SPAM_DIR_2}
	 */
	public void train() {
		
		// Special case
		if (root == null || !root.isDirectory()) {
			return;
		}
		
		// Clear the map
		this.wordMap.clear();
		
		// Check the ham files
		File dir = new File(root.getAbsolutePath()+File.separator
				+TRAIN_DIR+File.separator+NON_SPAM_DIR);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			this.hamFiles = files.length;
			for (File f : files) {
				updateMap(f, false);
			}
		}
		dir = new File(root.getAbsolutePath()+File.separator
				+TRAIN_DIR+File.separator+NON_SPAM_DIR_2);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			this.hamFiles += files.length;
			for (File f : files) {
				updateMap(f, false);
			}
		}
		
		// Check spam files
		dir = new File(root.getAbsolutePath()+File.separator
				+TRAIN_DIR+File.separator+SPAM_DIR);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			this.spamFiles = files.length;
			for (File f : files) {
				updateMap(f, true);
			}
		}
	}
	
	/**
	 * <b><em>test</em></b>
	 * 
	 * <p>Tests the spam filter after training has been completed. The spam
	 * files are located in spam/ham directories within 
	 * root/{@value #TEST_DIR}.</p>
	 * 
	 * <p>The way the filter classifies a file as spam or not is by checking
	 * the words contained in it. If many of the words have been associated
	 * with spam files, the test file is more likely to be associated with
	 * spam. If the spam probability is greater than {@value #SPAM_THRESHOLD},
	 * the file is considered spam.</p>
	 * 
	 * <p>Note that files are tested individually using
	 * {@link #isSpam(TestFile, String)}.</p>
	 * 
	 * @return the list of files tested in the testing directory.
	 * 
	 * @see {@link #train()}, {@link #SPAM_DIR}, {@link #NON_SPAM_DIR}
	 */
	public ObservableList<TestFile> test() {
		
		ObservableList<TestFile> files = FXCollections.observableArrayList();
		
		// Get all the files
		File[] nonSpam = (new File(root.getAbsolutePath()+File.separatorChar
				+TEST_DIR+File.separatorChar+NON_SPAM_DIR)).listFiles();
		File[] spam = (new File(root.getAbsolutePath()+File.separatorChar
				+TEST_DIR+File.separatorChar+SPAM_DIR)).listFiles();
		
		// Get the non-spam files
		if (nonSpam != null) {
			for (File f : nonSpam) {
				
				// Ignore if directory
				if (f.isDirectory()) {
					continue;
				}

				// Check the status of the file
				TestFile file = new TestFile(
						f.getName(), 0.0, TestFile.NOT_SPAM);
				boolean isSpam = isSpam(file, f.getAbsolutePath());
				if (isSpam) {
					this.spamGuesses ++;
				} else {
					this.correctGuess ++;
				}
				files.add(file);
			}
		}

		// Get the spam files
		if (spam != null) {
			for (File f : spam) {

				// Ignore if directory
				if (f.isDirectory()) {
					continue;
				}

				// Check the status of the file
				TestFile file = new TestFile(f.getName(), 0.0, TestFile.SPAM);
				boolean isSpam = isSpam(file, f.getAbsolutePath());
				if (isSpam) {
					this.spamGuesses ++;
					this.correctGuess ++;
					this.correctSpamGuesses ++;
				}
				files.add(file);
			}
		}
		
		return files;
	}
	
	/**
	 * <b><em>isSpam</em></b>
	 * 
	 * <p>Attempts to classify a file as spam or non-spam (ham). This is done
	 * after the filter has been trained (using {@link #train()}). It uses the
	 * words from training and which files they were associated with to try and
	 * classify the file as spam or not.</p>
	 * 
	 * @param file - the file to classify.
	 * @param path - the complete path to the file.
	 * 
	 * @return true if and only if the probability of the file being spam is
	 * greater than {@value #SPAM_THRESHOLD}.
	 * 
	 * @see {@link #test()}, {@link #train()}
	 */
	public boolean isSpam(TestFile file, String path) {
		
		// Special case
		if (file == null || path == null) {
			return false;
		}
		
		// Read the file
		try {

			// Read each word
			List<String> checked = new ArrayList<>();
			double eta = 0;
			Scanner s = new Scanner(new File(path));
			while (s.hasNext()) {
				String text = s.next().toLowerCase();

				// Only check words that haven't been checked for this file
				// If the word wasn't encountered in training, discard it
				if (isWord(text) && !checked.contains(text)
						&& wordMap.containsKey(text)) {
					checked.add(text);

					// Calculate the probability
					// NOTE: Adding '1' to the file count to avoid pSW = 0 or 1
					Frequency f = wordMap.get(text);
					final double pWS = (1.0*f.spamFileCount+1)/spamFiles;
					final double pWH = (1.0*f.hamFileCount+1)/hamFiles;
					final double pSW = pWS/(pWS + pWH);
					eta = eta
							+ (Math.log(1 - pSW) - Math.log(pSW));
				}
			}
			file.setSpamProbability(1.0/(1.0+Math.pow(Math.E, eta)));
			s.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return file.getSpamProbability() > SPAM_THRESHOLD;
	}
	
	public File getRoot() {
		return root;
	}

	public void setRoot(File root) {
		this.root = (root == null)? new File(".") : root;
	}
	
	public int getSpamFiles() {
		return spamFiles;
	}

	public int getHamFiles() {
		return hamFiles;
	}

	public int getSpamGuesses() {
		return spamGuesses;
	}

	public int getCorrectSpamGuesses() {
		return correctSpamGuesses;
	}

	public int getCorrectGuessCount() {
		return correctGuess;
	}

	/**
	 * <b><em>updateMap</em></b>
	 * 
	 * <p>Updates the word map to include the words from this file. If a
	 * particular word is already in the map, the frequency is updated. This
	 * is used during the training phase of the filter.</p>
	 * 
	 * @param file - the file to scrape the words from.
	 * @param isSpam - the actual class of the file.
	 * 
	 * @see {@link #train()}
	 */
	private void updateMap(File file, boolean isSpam) {
		
		// Special case
		if (file == null || !file.isFile()) {
			return;
		}
		
		// Read the file
		try {
			List<String> counted = new ArrayList<>();

			Scanner s = new Scanner(file);
			while (s.hasNext()) {
				String text = s.next().toLowerCase();	
				// Only check words
				if (isWord(text)) {

					boolean contains = counted.contains(text);
					if (!contains) {
						counted.add(text);
					}

					// Add the word to the map
					Frequency f = null;
					if (wordMap.containsKey(text)) {
						f = wordMap.get(text);
					} else {
						f = new Frequency();
					}
					if (isSpam) {
						f.spamTotal ++;
						if (!contains) {
							f.spamFileCount ++;
						}
					} else {
						f.hamTotal ++;
						if (!contains) {
							f.hamFileCount ++;
						}
					}
					this.wordMap.put(text, f);
				}
			}
			s.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <b><em>isWord</em></b>
	 * 
	 * <p>Checks if a string is considered a word. Note: the string is
	 * assumed to be all lower-case.</p>
	 * 
	 * @param text - the string.
	 * @return true if and only if the string is considered a word.
	 */
	private static boolean isWord(String text) {
		return text.matches("^[a-z]*$");
	}
	
	/** The {@code Frequency} class is used to keep track of the number of
	 * times a word shows up. It has counts for the total times it shows up in
	 * spam/non-spam files and the number of spam/non-spam files it shows. */
	private static class Frequency {
		
		/** The number of times a word appears in a ham file. */
		public int hamFileCount;
		
		/** The number of times the word shows up in all ham files. */
		public int hamTotal;
		
		/** The number of times a word appears in a spam file. */
		public int spamFileCount;
		
		/** The number of times the word shows up in all spam files. */
		public int spamTotal;
		
		public String toString() {
			return hamFileCount + "\t" + hamTotal + "\t"
					+ spamFileCount + "\t" + spamTotal;
		}
	}
}
