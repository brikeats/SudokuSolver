package com.example.brian.sudokusolver;
import android.util.Log;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.lang.Exception;


// This is a port of Peter Norvig's solution http://norvig.com/sudoku.html

public class SudokuSolver
{
    static public final String TAG = "SudokuSolver";

    static public final String digits = "123456789";
    static public final String cols = "123456789";
    static public final String rows = "ABCDEFGHI";

    public static class UnsolvableException extends Exception{}
    public static class GridFormatException extends Exception{}

    // TODO: this should all be static
    private HashMap<String, HashSet<String>> peers;
    private ArrayList<String> squares;
    private HashMap<String, ArrayList<ArrayList<String>>> units;

    public SudokuSolver(){

        // squares
        squares = cross(rows, cols);

        // unitlist
        ArrayList<ArrayList<String>> unitList = new ArrayList();
        for(char c: cols.toCharArray())
            unitList.add(cross(rows, String.valueOf(c)));
        for(char r: rows.toCharArray())
            unitList.add(cross(String.valueOf(r), cols));
        ArrayList<String> rsArr = new ArrayList();
        rsArr.add("ABC"); rsArr.add("DEF"); rsArr.add("GHI");
        ArrayList<String> csArr = new ArrayList();
        csArr.add("123"); csArr.add("456"); csArr.add("789");
        for(String rs: rsArr){
            for(String cs: csArr){
                unitList.add(cross(rs, cs));
            }
        }

        // units
        units = new HashMap();
        for(String square: squares){
            ArrayList<ArrayList<String>> unitArr = new ArrayList();
            for(ArrayList<String> unit: unitList){
                if(unit.contains(square))
                    unitArr.add(unit);
                units.put(square, unitArr);
            }
        }

        // peers
        peers = new HashMap();
        for(String square: squares){
            ArrayList<ArrayList<String>> unit = units.get(square);
            HashSet<String> peerSet = new HashSet<String>();
            for(ArrayList<String> strArr: unit){
                for(String str: strArr){
                    peerSet.add(str);
                }
            }
            peerSet.remove(square);
            peers.put(square, peerSet);
        }
    }


    private static ArrayList<String> cross(String A, String B){
        ArrayList<String> strArr = new ArrayList();
        for(char a: A.toCharArray()){
            for(char b: B.toCharArray()){
                strArr.add(String.valueOf(a)+String.valueOf(b));
            }
        }
        return strArr;
    }

/*
    public static void main(String [] args)
    {

        String easyGrid = "003020600900305001001806400008102900700000008006708200002609500800203009005010300";
        String hardGrid = "4.....8.5.3..........7......2.....6.....8.4......1.......6.3.7.5..2.....1.4......";
        String photoGrid = "......... .57...... 6.3...2.. ....2.8.. ....6.... .95.78... .....718. ..194..3. .2..8645.";
        photoGrid = photoGrid.replace(" ", "");

        SudokuSolver solver = new SudokuSolver();

        HashMap<String, String> values;
        HashMap<String, String> solution;

        solver.printGrid(photoGrid);
        values = solver.parseGrid(photoGrid);
        solver.display(values);
        solution = solver.search(solver.parseGrid(photoGrid));
        if (solution == null)
            System.out.println("No solution found");
        //else
        //solver.display(solution);
    }
*/

    public String solvePuzzle(String puzzle){
        Log.d(TAG, "puzzle = "+puzzle);
        HashMap<String, String> solution = search(parseGrid(puzzle));
        if (solution == null)
            return null;
        Log.d(TAG, "solution map = "+solution.toString());
        StringBuilder stringBuilder = new StringBuilder();
        for (Character r : rows.toCharArray()){
            for(Character c: cols.toCharArray()) {
                String squareStr = r.toString() + c.toString();
                stringBuilder.append(solution.get(squareStr));
            }
        }
        return stringBuilder.toString();
    }


    private HashMap<String, String> search(HashMap<String, String> values) {

        // failed earlier
        if (values == null)
            return null;

        // check if it's already solved
        boolean solved = true;
        for(String s: squares){
            if (values.get(s).length() != 1){
                solved = false;
                break;
            }
        }
        if (solved)
            return values;

        // Choose the unfilled square minSquare with the fewest possibilities
        int minLength = 10;
        String minSquare = "A1";
        for (String square: squares){
            int length = values.get(square).length();
            if (length < minLength && length > 1){
                minLength = length;
                minSquare = square;
            }
        }

        for (char d: values.get(minSquare).toCharArray()){
            String dStr = String.valueOf(d);
            HashMap<String, String> vals = search(assign(new HashMap(values), minSquare, dStr));
            if(vals != null)
                return vals;
        }
        return null;
    }


    private HashMap<String, String> parseGrid(String grid){

        HashMap<String, String> values = new HashMap();

        // To start, every square can be any digit; then assign values from the grid.
        for(String square: squares)
            values.put(square, digits);

        try{
            HashMap<String,String> gridVals = gridValues(grid);
            for(String s: gridVals.keySet()){
                String d = gridVals.get(s);
                if (digits.contains(d) && assign(values, s, d)==null)
                    return null;
            }
            return values;
        }
        catch(GridFormatException e){
            return null;
        }
    }


    // Eliminate d from values[s]; propagate when values or places <= 2.
    // Return values, except return False if a contradiction is detected.
    private HashMap<String, String> eliminate(HashMap<String, String> values, String s, String d){

        //System.out.println("eliminating "+d+" from "+s);
        //System.out.println("before value = "+values.get(s));

        // Already eliminated
        if(!values.get(s).contains(d)){
            //System.out.println("already eliminated");
            return values;
        }

        values.put(s, values.get(s).replace(d, ""));

        // (1) If a square s is reduced to one value d2, then eliminate d2 from the peers.
        if (values.get(s).length() == 0){
            return null;  // Contradiction: removed last value
        }
        else if (values.get(s).length() == 1){
            String d2 = values.get(s);
            boolean all_ok = true;
            for(String s2: peers.get(s)){
                values = eliminate(values, s2, d2);
                if (values == null){
                    all_ok = false;
                    break;
                }
            }
            if (!all_ok)
                return null;
        }

        // (2) If a unit u is reduced to only one place for a value d, then put it there.
        for (ArrayList<String> u: units.get(s)){
            ArrayList<String> dplaces = new ArrayList();
            for(String s1: u)
                if (values.get(s1).contains(d))
                    dplaces.add(s1);
            if (dplaces.size() == 0)
                return null;
            else if (dplaces.size() == 1)
                if (assign(values, dplaces.get(0), d) == null )
                    return null;
        }

        return values;
    }


    private HashMap<String, String> assign(HashMap<String, String> values, String s, String d){
        String other_values = values.get(s).replace(d, "");
        boolean ok = true;
        for (Character d2: other_values.toCharArray()){
            values = eliminate(values, s, String.valueOf(d2));
            if (values == null){
                ok = false;
                break;
            }
        }
        return values;
    }


    private HashMap<String, String> gridValues(String grid) throws GridFormatException{

        ArrayList<String> chars = new ArrayList();
        for(Character c: grid.toCharArray()){
            String cStr = String.valueOf(c);
            if(digits.contains(cStr) || "0.".contains(cStr))
                chars.add(cStr);
        }

        if(chars.size() != 81)
            throw new GridFormatException();

        HashMap<String, String> gridVals = new HashMap();
        for(int i=0; i<81; i++)
            gridVals.put(squares.get(i), chars.get(i));
        return gridVals;
    }


    private void display(HashMap<String, String> values){

        int width = 0;
        for(String str: values.values()){
            if(str.length() > width)
                width = str.length();
        }

        String linePart = new String(new char[3*width]).replace("\0", "-");
        String line = linePart + "+" + linePart + "+" + linePart;

        for(char r: rows.toCharArray()){
            String rStr = String.valueOf(r);
            for(char c: cols.toCharArray()){
                String cStr = String.valueOf(c);
                String numStr = values.get(rStr+cStr);
                int preLength = (width - numStr.length())/2;
                int postLength = (width - numStr.length())/2;
                String preStr = new String(new char[preLength]).replace("\0", " ");
                String postStr = new String(new char[preLength]).replace("\0", " ");
                String str = preStr + numStr + postStr;
                if("36".contains(cStr))
                    str += "|";
                System.out.print(str);
            }
            System.out.print("\n");
            if("CF".contains(rStr))
                System.out.println(line);
        }
        System.out.println();
    }


    private void printGrid(String grid){
        for(int row=0; row<9; row++){
            for(int unit=0; unit<3; unit++){
                int sta_ind = 9*row + 3*unit;
                char[] triplet = grid.substring(sta_ind, sta_ind+3).toCharArray();
                System.out.print(triplet[0]+" "+triplet[1]+" "+triplet[2]+" ");
                if(unit != 2)
                    System.out.print("|");
            }
            System.out.print("\n");
            if(row==2 || row==5)
                System.out.println("------+------+------");
        }
        System.out.println();
    }

}
