/* Name: ProgramManager
 * Author: Devon McGrath
 * Description: This class starts the program by instantiating the JavaFX
 * display.
 * 
 * Version History:
 * 1.0 - 01/29/2017 - Initial version - Devon McGrath
 */

package program;

import javafx.application.Application;
import javafx.stage.Stage;

public class ProgramManager extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Setup the display
		primaryStage.setTitle("Spam Master 3000");
		primaryStage.setScene(new InitialView(primaryStage));
		primaryStage.show();
	}

}
