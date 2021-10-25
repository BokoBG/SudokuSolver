package sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * This is the visual represnetation of the Sudoku board basaed on the internal
 * representation in the Sudoku class
 */
public class SudokuGUI extends Application 
{
    public static SudokuBox [][] gameBoard, startingBoard;
    public static GridPane sudokuBoard;
    public static Button nextStep, recursive, reset;
    File selectedFile, lastFile;
    
    @Override
    public void start(Stage primaryStage) 
    {
        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: white;");
        
        //Menu gives the user an option to switch between sample files
        MenuBar menu = new MenuBar();
        Menu menuSample = new Menu("File");
        MenuItem m1 = new MenuItem("Choose Sample File");
        MenuItem m2 = new MenuItem("Exit");
        menuSample.getItems().addAll(m1, m2);
        menu.getMenus().add(menuSample);
        
	//Control Buttons for the UI
        nextStep = new Button("Next Step");
        nextStep.setDisable(true);
        nextStep.setPrefSize(100, 50);
        nextStep.setPadding(new Insets (10));
        nextStep.setFont(new Font("Georgia", 15));
        nextStep.setStyle("-fx-border-style: solid; -fx-background-color: white");
        
        recursive = new Button("Recursive Solve");
        recursive.setDisable(true);
        recursive.setPrefSize(200, 50);
        recursive.setPadding(new Insets (10));
        recursive.setFont(new Font("Georgia", 15));
        recursive.setStyle("-fx-border-style: solid; -fx-background-color: white");
        
        reset = new Button("Reset");
        reset.setDisable(true);
        reset.setPrefSize(100, 50);
        reset.setPadding(new Insets (10));
        reset.setFont(new Font("Georgia", 15));
        reset.setStyle("-fx-border-style: solid; -fx-background-color: white");
        
        sudokuBoard = new GridPane();
        sudokuBoard.setPrefSize(630, 630);
        sudokuBoard.setAlignment(Pos.CENTER);
        
        //This creates an empty Sudoku grid to start with
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                Button boxText = new Button();
                boxText.setDisable(true);
                boxText.setPrefSize(70, 70);
                boxText.setAlignment(Pos.CENTER);
                sudokuBoard.add(boxText, c, r);
            }
        }
        main.setCenter(sudokuBoard);
        
        VBox top = new VBox(4);
        top.setAlignment(Pos.CENTER);
        
        HBox buttons = new HBox(4);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(nextStep, recursive, reset);
        top.getChildren().addAll(menu, buttons);
        VBox ui = new VBox(4);
        ui.getChildren().addAll(top, main);
        StackPane root = new StackPane();
        root.getChildren().add(ui);
        
        Scene scene = new Scene(root, 630, 630);
        
        primaryStage.setTitle("Sudoku Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        /*
            User able to choose any file in the system
        */
        m1.setOnAction((ActionEvent event) -> 
        {
            FileChooser sample = new FileChooser();
            sample.setInitialDirectory(new File(System.getProperty("user.dir")));
            selectedFile = sample.showOpenDialog(primaryStage);
            
	//Error Dialog in the case where user picks a different file type
            while (selectedFile.getName().endsWith(".csv") == false)
            {
                Alert wrongFileType = new Alert(AlertType.ERROR);
                wrongFileType.setTitle("File Type Error");
                wrongFileType.setHeaderText("Incorrect File Type!");
                wrongFileType.setContentText("You have not selected a .csv file!");

                wrongFileType.showAndWait();
                selectedFile = sample.showOpenDialog(primaryStage);
            }
  
            //If no error with choosing file, use sample numbers to set up board
            if (selectedFile != null)
            {
                lastFile = selectedFile;	//store original sudoku puzzle
                Sudoku.boxesLeft = 81;
                try 
                {
                    gameBoard = Sudoku.readFile(selectedFile);
                } 
                catch (FileNotFoundException ex) 
                {
                    Logger.getLogger(SudokuGUI.class.getName()).log(Level.SEVERE, null, ex);
                }

                Sudoku.checkForErrors(gameBoard);
                Sudoku.findAllPossibleNumbers(gameBoard);
                if (updateBoard())
                {
                    nextStep.setDisable(false);
                    recursive.setDisable(false);
                    reset.setDisable(false);
                }
                else
                {
                    nextStep.setDisable(true);
                    recursive.setDisable(true);
                    reset.setDisable(true);
                }
            }   
        });
        
        m2.setOnAction((ActionEvent event) -> 
        {   System.exit(0); });
        
        
        /*
            When button pressed, board is updated by one step
            Visual representation is then updated
        */
        nextStep.setOnAction ((ActionEvent event) -> 
        {
            Sudoku.solveStep(gameBoard);
            if (Sudoku.boxesLeft == 0)  //Means that board is solved
            {
                nextStep.setDisable(true);
                recursive.setDisable(true);
            }
            Sudoku.findAllPossibleNumbers(gameBoard);
            updateBoard(); 
            
            //No progress made, means that recursion is needed to solve the rest
            if (Sudoku.boxesLeft == Sudoku.currBoxesLeft)
                nextStep.setDisable(true);
        });
        
        /*
            When pressed, the whole puzzle is solved in one single step
        */
        recursive.setOnAction ((ActionEvent event) -> 
        {
            Sudoku.solveRecursive(gameBoard);
            updateBoard(); 
            nextStep.setDisable(true);
            recursive.setDisable(true);
        });
        /*
            Reset board to original state
        */
        reset.setOnAction ((ActionEvent event) -> 
        {
            Sudoku.boxesLeft = 81;
            try 
            {
                gameBoard = Sudoku.readFile(lastFile);
            } 
            catch (FileNotFoundException ex) 
            {
                Logger.getLogger(SudokuGUI.class.getName()).log(Level.SEVERE, null, ex);
            }

            Sudoku.checkForErrors(gameBoard);
            Sudoku.findAllPossibleNumbers(gameBoard);
            if (updateBoard())
            {
                nextStep.setDisable(false);
                recursive.setDisable(false);
                reset.setDisable(false);
            }
        });
    }
    
    /*
        Function to change the numbers in the Sudoku board when user changes
        the sample file option
            Process (after internal representation is reset):
                1. Clear current board
                2. Enter new values in the GridPane
    */
    public static boolean updateBoard ()
    {
        boolean validBoard = true;
        sudokuBoard.getChildren().clear();
        int index = 0;
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                Button boxText;
                
                //10 signifies error with input; Want to color code accordingly
                if (gameBoard[r][c].getValue().equals(10))
                {
                    boxText = new Button("Invalud\n  Input");
                    boxText.setFont(new Font("Georgia", 13));
                    boxText.setStyle("-fx-background-color: red");
                    boxText.setTextFill(Color.WHITE);
                    boxText.setPrefSize(70, 70);
                    boxText.setAlignment(Pos.CENTER);
                    sudokuBoard.add(boxText, c, r);
                    validBoard = false;
                }
                else
                {                    
                    //if not 0 (meaning blank cell), we copy the value from our 
                    //game board 2D array
                    if (!gameBoard[r][c].getValue().equals(0))
                    {
                        boxText = new Button(gameBoard[r][c].getValue().toString());
                        boxText.setFont(new Font("Georgia", 30));
                        boxText.setPrefSize(70, 70);
                        boxText.setAlignment(Pos.CENTER);
                        sudokuBoard.add(boxText, c, r);
                        
                        //For any invalid cells, color is changed to yellow
                        if (!gameBoard[r][c].isValid())
                        {    
                            boxText.setStyle("-fx-background-color: yellow");
                            validBoard = false;
                        }
                    }
                    //if 0, we refer to the list of Strings and enter the next index
                    //in the blank box
                    else
                    {
                        boxText = new Button();
                        //Sudoku.possibilities.get(index)
                        boxText.setFont(new Font("Georgia", 15));
                        boxText.setPrefSize(70, 70);
                        boxText.setAlignment(Pos.CENTER);
                        sudokuBoard.add(boxText, c, r);
                        index++;
                    }
                }
            } 
        }
        return validBoard;
    }

    public static void main(String[] args) 
    {   launch(args);   } 
}