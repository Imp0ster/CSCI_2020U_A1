package program;

import java.text.DecimalFormat;

public class TestFile {
	
	/** This field corresponds to the spam class. */
	public static final String SPAM = "Spam";
	
	/** This field corresponds to the non-spam (ham) class. */
	public static final String NOT_SPAM = "Ham";

	private String filename;
	
	private double spamProbability;
	
	private String actualClass;

	public TestFile(String filename,
			double spamProbability,
			String actualClass) {
		this.filename = filename;
		this.spamProbability = spamProbability;
		this.actualClass = actualClass;
	}

	public String getFilename() { return this.filename; }
	
	public double getSpamProbability() { return this.spamProbability; }
	
	public String getSpamProbRounded() {
		DecimalFormat df = new DecimalFormat("0.00000");
		return df.format(this.spamProbability);
	}
	
	public String getActualClass() { return this.actualClass; }
	
	
	public String getPredictedClass() {
		return (spamProbability > SpamFilter.SPAM_THRESHOLD)? SPAM : NOT_SPAM;
	}

	public void setFilename(String value) { this.filename = value; }
	
	public void setSpamProbability(double val) { this.spamProbability = val; }
	
	public void setActualClass(String value) { this.actualClass = value; }
}
