/* Name: InitialView
 * Author: Devon McGrath
 * Description: This class tells the user what kind of directory to select.
 * 
 * Version History:
 * 1.0 - 01/29/2017 - Initial version - Devon McGrath
 */

package program;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class InitialView extends Scene {
	
	/** The main display window. */
	private Stage primaryStage;
	
	/** The button which allows selecting a directory. */
	private Button select;

	/**
	 * Constructs an initial view for the primary stage.
	 * 
	 * @param primaryStage - the primary stage (window).
	 */
	public InitialView(Stage primaryStage) {
		super(new BorderPane(), 700, 500);
		
		this.primaryStage = primaryStage;
		
		// Setup the top of the layout
		BorderPane layout = (BorderPane) getRoot();
		layout.setPadding(new Insets(10));
		this.select = new Button("Select Directory");
		this.select.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setInitialDirectory(new File("."));
			File root = directoryChooser.showDialog(this.primaryStage);
			if (root != null) {
				this.primaryStage.setScene(
						new DirectoryView(root));
			}
		});
		FlowPane top = new FlowPane();
		top.getChildren().add(select);
		top.setPadding(new Insets(5));
		layout.setTop(top);
		
		// Setup the center
		TextArea dirStruct = new TextArea("Select a directory with the "
				+ "following structure:\n<root>\n\ttest\n\t\tham\n\t\tspam"
				+ "\n\ttrain\n\t\tham\n\t\tham2\n\t\tspam");
		dirStruct.setEditable(false);
		layout.setCenter(dirStruct);
	}
}
