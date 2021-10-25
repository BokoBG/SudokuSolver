package sudoku;
/*
    Bozhidar Kolev
    Class for a single Sudoku Box (grid cell)
    Constructor includes an Integer value to represent the number in the cell
    and a Boolean to flag the cell if the input file comes with mistakes
 */
public class SudokuBox 
{
    public Integer value;
    public boolean valid;
    
    public SudokuBox (Integer num)
    {
        this.value = num;
        this.valid = true;
    }
    
    public Integer getValue()
    {   return this.value;   }
    
    public void setValue (Integer num)
    {   this.value = num; }
    
    public boolean isValid ()
    {   return this.valid; }
    
    public void changeValid ()
    {   
        //All cells start as valid (true), so we only want to change them to false
        if (this.valid == true)  
            this.valid = false;
    }
}