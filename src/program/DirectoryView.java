/* Name: DirectoryView
 * Author: Devon McGrath
 * Description: This class creates the directory view for the display. It
 * lists all the files in a specific directory, the accuracy, and precision.
 * 
 * Version History:
 * 1.0 - 01/29/2017 - Initial version - Devon McGrath
 */

package program;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * <p>The {@code DirectoryView} class will take a root directory and display
 * test email files that are spam or non-spam. The root must have the following
 * sub-directories for the {@code DirectoryView} to work:<br>
 * <em>root</em>/train/spam<br>
 * <em>root</em>/train/ham<br>
 * <em>root</em>/test/spam<br>
 * <em>root</em>/test/ham</p>
 * 
 * <p>From the root, the {@code DirectoryView} will train a {@link SpamFilter}
 * with the spam and non-spam (ham) files. Then, the test emails will be used
 * with the trained spam filter to try to accurately classify the files as
 * spam or ham.</p>
 */
public class DirectoryView extends Scene {
	
	/** The root directory. */
	private File directory;
	
	/** The spam filter that will be trained and used for testing. */
	private SpamFilter filter;

	/** The list of testing files. */
	private TableView<TestFile> fileList;
	
	/** The field that displays the accuracy of the system to the user. */
	private TextField accuracy;
	
	/** The field that displays the precision of the system to the user. */
	private TextField precision;
	
	/** Constructs a directory view with the root directory being the current
	 * working directory. */
	public DirectoryView() {
		this(new File("."));
	}
	
	/**
	 * Constructs a directory view with a specified root.
	 * @param directory - the root directory.
	 */
	public DirectoryView(File directory) {
		super(new BorderPane(), 700, 500);
		init();
		setDirectory(directory);
	}
	
	/** Initializes the layout for the directory view. */
	private void init() {
		
		BorderPane layout = (BorderPane) getRoot();
		
		// Create the table
		TableColumn<TestFile, String> fileCol = new TableColumn<>("File");
		fileCol.setCellValueFactory(new PropertyValueFactory<>("filename"));
		TableColumn<TestFile, String> pClassCol = new TableColumn<>(
				"Predicted Class");
		pClassCol.setCellValueFactory(
				new PropertyValueFactory<>("predictedClass"));
		TableColumn<TestFile, String> classCol = new TableColumn<>(
				"Actual Class");
		classCol.setCellValueFactory(
				new PropertyValueFactory<>("actualClass"));
		TableColumn<TestFile, Double> probCol = new TableColumn<>(
				"Spam Probability");
		probCol.setCellValueFactory(
				new PropertyValueFactory<>("spamProbRounded"));
		this.fileList = new TableView<>();
		this.fileList.getColumns().add(fileCol);
		this.fileList.getColumns().add(pClassCol);
		this.fileList.getColumns().add(classCol);
		this.fileList.getColumns().add(probCol);
		
		// Create the bottom panel
		FlowPane top = new FlowPane();
		top.setPadding(new Insets(5));
		FlowPane bottom = new FlowPane();
		bottom.setPadding(new Insets(5));
		GridPane grid = new GridPane();
		this.accuracy = new TextField("0.0");
		this.accuracy.setEditable(false);
		this.precision = new TextField("0.0");
		this.precision.setEditable(false);
		top.getChildren().add(new Label("Accuracy:   "));
		top.getChildren().add(accuracy);
		bottom.getChildren().add(new Label("Precision:   "));
		bottom.getChildren().add(precision);
		
		// Add the components
		layout.setCenter(fileList);
		grid.add(top, 0, 0);
		grid.add(bottom, 0, 1);
		layout.setBottom(grid);setPrecision(0);
	}

	/**
	 * <b><em>getDirectory</em></b>
	 * 
	 * <p>Gets the root directory, which has a specified structure (see
	 * {@link DirectoryView}). Note that the {@code File} object returned is
	 * never null.</p>
	 * 
	 * @return the root directory.
	 * @see {@link #setDirectory(File)}
	 */
	public File getDirectory() {
		return directory;
	}

	/**
	 * <b><em>setDirectory</em></b>
	 * 
	 * <p>Sets the root directory, which has a specified structure (see
	 * {@link DirectoryView}). Note that if this method is called with a null
	 * file, the root directory is set to the current working directory.</p>
	 * 
	 * <p>Once the new root directory is set, the spam filter will be retrained
	 * and the display will be updated.</p>
	 * 
	 * @param directory - the root directory.
	 * 
	 * @see {@link #getDirectory()}
	 */
	public void setDirectory(File directory) {
		
		// Re-train the filter
		this.directory = (directory == null)? new File(".") : directory;
		this.filter = new SpamFilter(this.directory);
		this.filter.train();
		
		// Test the filter
		this.fileList.getItems().clear();
		ObservableList<TestFile> files = filter.test();
		this.fileList.setItems(files);
		
		// Calculate the accuracy and precision
		setAccuracy((1.0*filter.getCorrectGuessCount())/
				(filter.getHamFiles()+filter.getSpamFiles()));
		setPrecision((1.0*filter.getCorrectSpamGuesses())
				/filter.getCorrectGuessCount());
	}
	
	/**
	 * <b><em>getAccuracy</em></b>
	 * 
	 * <p>Gets the accuracy of the spam filter. This value is displayed to the
	 * user.</p>
	 * 
	 * @return the accuracy.
	 * @see {@link #setAccuracy(double)}, {@link #getPrecision()},
	 * {@link #setPrecision(double)}
	 */
	public double getAccuracy() {

		// Parse the text from the text field
		try {
			return Double.parseDouble(accuracy.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * <b><em>setAccuracy</em></b>
	 * 
	 * <p>Sets the accuracy of the system, which is a value between 0 and 1.
	 * Where 0 is 0% accuracy and 1 is 100% accuracy. The field that displays
	 * this value will be updated to the new accuracy.</p>
	 * 
	 * @param accuracy - the accuracy of the spam filter.
	 * 
	 * @see {@link #getAccuracy()}, {@link #getPrecision()},
	 * {@link #setPrecision(double)}
	 */
	public void setAccuracy(double accuracy) {
		this.accuracy.setText(""+(((int)(100000*accuracy))/(100000.0)));
	}
	
	/**
	 * <b><em>getPrecision</em></b>
	 * 
	 * <p>Gets the precision of the spam filter. This value is displayed to the
	 * user.</p>
	 * 
	 * @return the precision.
	 * @see {@link #setPrecision(double)}, {@link #getAccuracy()},
	 * {@link #setAccuracy(double)}
	 */
	public double getPrecision() {
		
		// Parse the text from the text field
		try {
			return Double.parseDouble(precision.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * <b><em>setPrecision</em></b>
	 * 
	 * <p>Sets the precision of the system, which is a value between 0 and 1.
	 * Where 0 is 0% precision and 1 is 100% precision. The field that displays
	 * this value will be updated to the new precision.</p>
	 * 
	 * @param precision - the precision of the spam filter.
	 * 
	 * @see {@link #getPrecision()}, {@link #getAccuracy()},
	 * {@link #setAccuracy(double)}
	 */
	public void setPrecision(double precision) {
		this.precision.setText(""+(((int)(100000*precision))/(100000.0)));
	}
}
