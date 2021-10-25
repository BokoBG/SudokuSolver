package sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
    Bozhidar Kolev
    This class represents a Sudoku Board and all of its function implementations
    Process:
        1. Read File, create and populate board
        2. Check board for any broken Sudoku rules based on input file
        3. Find possible numbers for each blank cell based on Sudoku rules
*/
public class Sudoku 
{
    public static String parentDir = System.getProperty("user.dir");
    public static ArrayList <String> possibilities = new ArrayList <>();
    public static int boxesLeft = 81, currBoxesLeft;
    
    /*
        This function reads input file and returns a 2D array to represent 
        the sudoku board
    */
    public static SudokuBox [][] readFile (File fileName) throws FileNotFoundException
    {
        SudokuBox [][] board = new SudokuBox [9][9];
        Scanner reader = new Scanner(fileName);
        
        int r = 0;
        while (reader.hasNextLine())
        {
            String line = reader.nextLine();
            String [] numbers = line.split(",");
            
            //currentRow creates a full row of 0s to account for any blank spots
            //in the readLine; A 0 will represent an empty box
            Integer [] currentRow = new Integer [] {0,0,0,0,0,0,0,0,0};
            
            //Any spots that are not blank will replace the zeros in the array
            for (int i=0; i<numbers.length; i++)
            {
                if (numbers[i].length() != 0)
                {
                    //Validate Input such as only a single number between 1-9
                    //is entered in original file
                    //10 signifies Invalid input
                    if (numbers[i].length() == 1 && Character.isDigit(numbers[i].charAt(0)))
                    {
                        currentRow[i] = Integer.parseInt(numbers[i]);
                        boxesLeft--;
                    }
                    else
                        currentRow[i] = 10;
                }                
            }
            
            for (int c=0; c<9; c++)
            {
                board[r][c] = new SudokuBox(currentRow[c]);
            }
            r++;    
        }
        return board;
    }
    
    /*
        This function validates the whole game board for errors before proceeding
        to finding out possibilities
    */
    public static void checkForErrors(SudokuBox [][] board)
    {
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                //0 = blank spot so no need to check
                if (!(board[r][c].getValue().equals(0)))
                    checkSingleBox (board, r, c);
            }
        }
    }   
    
    /*
        Function to check a single box on the game board
        If values match, flag the two boxes as Invalid
    */
    public static void checkSingleBox (SudokuBox [][] board ,int row, int col)
    {
        for (int c=0; c<9; c++)
        {
            //if column is the same, that means that we are checking the same box
            if (col != c)
            {
                if ((board[row][col].getValue()).equals((board[row][c].getValue())))
                {
                    board[row][col].changeValid();
                    board[row][c].changeValid();
                }    
            }
        }
         
        for (int r=0; r<9; r++)
        {
            //if row is the same, that means that we are checking the same box
            if (row != r)
            {
                if (board[row][col].getValue().equals(board[r][col].getValue()))
                {
                    board[row][col].changeValid();
                    board[r][col].changeValid();
                }  
            }
        }
        
	//Check 3by3 box for duplicate
        for (int a=row/3*3; a<row/3*3+3; a++)
        {            
            for (int b=col/3*3; b<col/3*3+3; b++)
            {
                //Avoid checking the box with itself
                if (row != a || col != b)
                {
                    if (board[row][col].getValue().equals(board[a][b].getValue()))
                    {
                        board[row][col].changeValid();
                        board[a][b].changeValid();
                    }
                }
            }
        }
    }
  
    /*
        This function finds all possible numbers for each cell on the game board
        according to original Sudoku rules
	//Commented out in GUI Class
    */
    public static void findAllPossibleNumbers (SudokuBox [][] board)
    {
        possibilities.clear();
        for (int row=0; row<9; row++)
        {
            for (int col=0; col<9; col++)
            {
                //Only if value is 0 (meaning blank cell), we check
                if (board[row][col].getValue().equals(0))
                    possibilities.add(possibleCellNumbers(board, row, col));                
            }
        }
    }
    
    /*
        Function checks a single cell for possible cell numbers
        We start with all numbers 1-9. As we check row, column, and box,
        Invalid values are removed from the list
        returns a String of ONLY possible numbers
    */
    public static String possibleCellNumbers (SudokuBox [][] board, int row,int column)
    {
        ArrayList <Integer> nums = new ArrayList <>();
        nums.add(1);nums.add(2);nums.add(3);nums.add(4);nums.add(5);nums.add(6);
        nums.add(7);nums.add(8);nums.add(9);
        for (int c=0; c<9; c++)
        {
            if (!board[row][c].getValue().equals(0))
                nums.remove(board[row][c].getValue());    
        }
         
        for (int r=0; r<9; r++)
        {
            if (!board[r][column].getValue().equals(0))
                nums.remove(board[r][column].getValue());    
        }
        
        for (int a=row/3*3; a<row/3*3+3; a++)
        {
            for (int b=column/3*3; b<column/3*3+3; b++)
            {
                if (!board[a][b].getValue().equals(0))
                    nums.remove(board[a][b].getValue()); 
            }
        }
        
        String result = "";
        for (int k=0; k<nums.size();k++)
            result+=nums.get(k).toString();
        
        return result;
    }
    
    /*
        This method finds all boxes which have only 1 suggested number
        This number is turned into the value for the box
        The box is then marked as solved
    */
    public static void solveStep (SudokuBox [][] board)
    {
        int index = 0;
        currBoxesLeft = boxesLeft;
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                if (board[r][c].getValue().equals(0))
                {
                    //If length of possibilities is one, then there is only one
                    //suggestion
                    //Index then removed from our list
                    String nums = possibilities.get(index);
                    if (nums.length() == 1)
                    {
                        board[r][c].setValue(Integer.parseInt(nums));
                        possibilities.remove(index);
                        boxesLeft--;
                    }
                    else
                        index++;
                }  
            }
        }
    }
    
    /*
        Recursive Method to solve the whole puzzle in one single step
    */
    public static boolean solveRecursive(SudokuBox [][] board)
    {
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                if (board[r][c].getValue().equals(0))
                {
                    for (int num=1; num<10; num++)
                    {
                        if (isGood(board, r, c, num))
                        {
                            board[r][c].setValue(num);
                            if (solveRecursive(board))
                                return true;
                            else
                                board[r][c].setValue(0);
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
    
    /*
        Method to check a single box is valid in the recursive version of solve
    */
    public static boolean isGood (SudokuBox [][] board, int row, int col, int num)
    {
        for (int c=0; c<9; c++)
        {
            if (board[row][c].getValue().equals(num))
                return false;
        }
        for (int r=0; r<9; r++)
        {
            if (board[r][col].getValue().equals(num))
                return false;
        }
        for (int a=row/3*3; a<row/3*3+3; a++)
        {
            for (int b=col/3*3; b<col/3*3+3; b++)
            {
                if (board[a][b].getValue().equals(num))
                    return false; 
            }
        }
        return true;
    }
}