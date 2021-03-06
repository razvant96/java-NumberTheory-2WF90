/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package numbertheory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;


public class NumberTheory {

    /**
     * @param args the command line arguments
     */
    
    // Map for all the possible digits
    public static Map<Character, Integer> digits = new HashMap<Character, Integer>();
    // Map used for converting back to letters
    public static Map<Integer, Character> letters = new HashMap<Integer, Character>();
    // Arrays for storing the different types of digits
    public static char[] digitsReal = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    // Arrays for storing the two numbers
    public static ArrayList<Integer> X = new ArrayList<>();
    public static ArrayList<Integer> Y = new ArrayList<>();
    // Array list for storing the final result
    public static ArrayList<Integer> R = new ArrayList<>();
    // Variables for comparing the two numbers
    // XlY = X less than Y;    Xgy = X greater than Y
    public static boolean XlY, XgY;
    // Variable for storing the current operation
    public static String OPERATION;
    // Variable for storing the final sign of the computation
    public static char SIGN = ' ';
    // Variables for storing the signs of X and Y
    public static char SIGN_X;
    public static char SIGN_Y;
    // Variable for storing the number of elementary operations (for multiplication)
    public static int NUMBER_OPERATIONS;
    public static int NUM_OPERATIONS;
    // Variable for checking if we are provided with an answer
    public static boolean hasAnswer = false;
    
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
        
        // Name of the input file i.e: "example.txt"
        String inputFile = "example.txt";
        
        // Used for reading one line at a time from the buffer
        String line = "";
        
        // Variable for storing each line of text from the input file
        // Will be using a hash map (line number, string)
        Map<Integer, String> map = new HashMap<Integer, String>();
        
        // Array for all the possible operations
        String[] operations = {"add", "subtract", "multiply", "karatsuba"};
        // Array for all the possible bases
        String[] base = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
        
        // Variable for storing the number of lines
        int i = 0;
        
        try {
            
            // Declare a new FileReader for the inputFile
            FileReader fileReader = new FileReader(inputFile);
            
            // We wrap the FileReader in a BufferedReader for later reading line by line
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            // Cycle through all the lines in the inputFile
            while((line = bufferedReader.readLine()) != null) {
                
                // Used for debugging to print the input file to the console
                //System.out.println(line);
                
                // Trim extra whitespaces from the beginning/end of the string
                    line = line.trim();
                
                // Check if the line has an answer on it, in which case we store it as well
                // We remove the hash symbol from the beginning
                if(line.length() >= 10 && line.charAt(0) == '#')
                    if(line.substring(3,9).equals("answer")) line = line.substring(2);
                // Store each line in the hash map
                // IMPORTANT: Stores all the lines that are not comments (do not start with # symbol)
                if(line.length() > 0 && line.charAt(0) != '#') {    
                    
                    // Add the new line to the map
                    map.put(i, line);
                    
                    // Increse the value of i (to keep track of the number of lines)
                    i++;
                }
            }
            
            // Close the inputFile
            bufferedReader.close();
            
        } catch(FileNotFoundException e) {
            
            // Catch the FileNotFoundException and inform user
            System.out.println("File " + inputFile + " was not found.");
            
            // Give the user a small hint as to what might have gone wrong
            System.out.println("Check if the input file is placed in the same directory and please name it example.txt");
            System.exit(0);
            
        } catch(IOException e) {
            
            // Catch the IOException and inform user
            System.out.println("There was an error while reading the " + inputFile + " file.");
        }
        
        // Prompt user with choice: save output in file or print in console
        System.out.println("Do you wish to save the output in a file (output.txt) ? (Y/N)");
        Scanner scanner = new Scanner(System.in);
        String temp = scanner.nextLine();
        temp = temp.toLowerCase();
        if(temp.equals("y") || temp.equals("yes") || temp.charAt(0) == 'y') {
            // Change where the program outputs the results (now in output.txt)
            PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);
        }
        
        // Run all the setup for the program
        buildDigitsMap();
        
        // Solve the given operations one-by-one
        for(int j = 0; j < i; j++) {
            String radix = map.get(j).substring(8);
            String operation = map.get(j+1).substring(1,map.get(j+1).length()-1);
            String xNumber = map.get(j+2).substring(4);
            String yNumber = map.get(j+3).substring(4);
            String answer = map.get(j+4).substring(0);
            
            // Check if we are provided with an answer
            answerGiven(answer);
            
            // Clear all the ArrayLists; New computation begins
            R.clear();
            X.clear();
            Y.clear();
            NUMBER_OPERATIONS = 0;
            NUM_OPERATIONS = 0;
            
            // Convert the base from string to int
            int b = Integer.parseInt(radix);
            
            // Update the global operation
            OPERATION = operation;
            
            // Read the sign for X
            SIGN_X = (xNumber.charAt(0) == '-') ? '-' : '+';
            
            // Read the sign for Y
            SIGN_Y = (yNumber.charAt(0) == '-') ? '-' : '+';
            
            // Remove the minus sign from the numbers
            xNumber = (xNumber.charAt(0) == '-') ? xNumber.substring(1) : xNumber;
            yNumber = (yNumber.charAt(0) == '-') ? yNumber.substring(1) : yNumber;
            // Convert the two numbers to ArrayList<Integer>
            for(int k = 0; k < xNumber.length(); k++)
                X.add(digits.get(xNumber.charAt(k)));
            for(int k = 0; k < yNumber.length(); k++)
                Y.add(digits.get(yNumber.charAt(k)));
            
            // Compare X and Y and change them if neccessary so that A > B
            compareNumbers();
            // Compute the sign of the final operation
            computeSign();
            
            switch(OPERATION) {
                case "add":
                    System.out.println("add");
                    R = add(X, Y, b);
                    break;
                case "subtract":
                    System.out.println("subtract");
                    R = subtract(X, Y, b);
                    break;
                case "multiply":
                    System.out.println("multiply");
                    R = multiply(X, Y, b);
                    break;    
                case "karatsuba":
                    System.out.println("karatsuba");
                    R = karatsuba(X, Y, b);
                    break;
                default:
                    System.out.println("This is an invalid operation.");
            }
            
            // Convert the number back to a char array and print to console
            // IMPORTANT: Now we also convert bigger digits to letters
            if(!R.isEmpty()) computeResult(R, R.size());
            
            // Print the given answer to the output file as well
            // Skip to the next operation that needs to be computed
            if(hasAnswer) {
                System.out.println(answer);
                j += 4;
            } else {
                j += 3;
            }
            
            // Print number of elementary operations (for multiplication)
            if(OPERATION.equals("multiply")) System.out.println("[operations] " + NUMBER_OPERATIONS);
            if(OPERATION.equals("karatsuba")) System.out.println("[operations] " + NUM_OPERATIONS);
        }
    }
    
    // Function for building the digits map; it maps each digit to its value
    // Future reference example: digits.get("b") will return 11
    public static void buildDigitsMap() {
        
        for(int i = 0; i < digitsReal.length; i++) {
            
            // Map each digit to a specific value
            digits.put(digitsReal[i], i);
            // Map each value to a specific letter
            letters.put(i, digitsReal[i]);
        }
    }
    
    public static void compareNumbers() {
        ArrayList<Integer> Z = new ArrayList<>();
        int i = 0;
        // Compare X and Y and interchange them if necessary
        if(X.size() == Y.size()) {
            while(X.get(i) == Y.get(i)) i++;
            if(X.get(i) < Y.get(i)) {
                Z = X;
                X = Y;
                Y = Z;
                XlY = true;
                XgY = false;
            }
        } else if(X.size() < Y.size()) {
            Z = X;
            X = Y;
            Y = Z;
            XlY = true;
            XgY = false;
        } else {
            XlY = false;
            XgY = true;
        }
    }
    
    public static void computeSign() {
        // IMPORTANT: See table below code to check for sign conversion
        // i.e: SIGN = '-'
        switch(OPERATION) {
            case "add":
                if(SIGN_X == '+' && SIGN_Y == '+') {
                    SIGN = '+';
                }
                if(SIGN_X == '+' && SIGN_Y == '-' && !XlY) {
                    SIGN = '+';
                    OPERATION = "subtract";
                }
                if(SIGN_X == '+' && SIGN_Y == '-' &&  XlY) {
                    SIGN = '-';
                    OPERATION = "subtract";
                }
                if(SIGN_X == '-' && SIGN_Y == '+' &&  XgY) {
                    SIGN = '-';
                    OPERATION = "subtract";
                }
                if(SIGN_X == '-' && SIGN_Y == '+' && !XgY) {
                    SIGN = '+';
                    OPERATION = "subtract";
                }
                if(SIGN_X == '-' && SIGN_Y == '-') {
                    SIGN = '-';
                }
                break;
            case "subtract":
                if(SIGN_X == '+' && SIGN_Y == '+' && !XlY) {
                    SIGN = '+';
                }
                if(SIGN_X == '+' && SIGN_Y == '+' &&  XlY) {
                    SIGN = '-';
                }
                if(SIGN_X == '+' && SIGN_Y == '-') {
                    SIGN = '+';
                    OPERATION = "add";
                }
                if(SIGN_X == '-' && SIGN_Y == '+') {
                    SIGN = '-';
                    OPERATION = "add";
                }
                if(SIGN_X == '-' && SIGN_Y == '-' && !XgY) {
                    SIGN = '+';
                }
                if(SIGN_X == '-' && SIGN_Y == '-' && XgY) {
                    SIGN = '-';
                }
                break;
            case "multiply":
                if(SIGN_X == '-' || SIGN_Y == '-') {
                    SIGN = '-';
                }
                if(SIGN_X == '-' && SIGN_Y == '-') {
                    SIGN = '+';
                }
                break;
            case "karatsuba":
                if(SIGN_X == '-' || SIGN_Y == '-') {
                    SIGN = '-';
                }
                if(SIGN_X == '-' && SIGN_Y == '-') {
                    SIGN = '+';
                }
                break;
            default:
                SIGN = '+';
        }
    }
    
    // Function for adding two numbers
    public static ArrayList<Integer> add(ArrayList<Integer> A, ArrayList<Integer> B, int b) {
        
        int i, t = 0;
        ArrayList<Integer> X = new ArrayList<>();
        ArrayList<Character> Y = new ArrayList<>();
        int x, y;
        
        // Reverse the two arrays A and B
        Collections.reverse(A);
        Collections.reverse(B);
        
        for(i = 0; (i < A.size()) || (i < B.size()) || (t != 0); i++, t/=b) {
            x = (i < A.size()) ? A.get(i) : 0;
            y = (i < B.size()) ? B.get(i) : 0;
            t += x + y;
            X.add(i, t % b);
        }
        
        // Update the new number of digits
        i -= 1;
        
        // Reverse the number and return from function call
        Collections.reverse(A);
        Collections.reverse(B);
        Collections.reverse(X);
        return X;
    }
    
    // Function for subtracting two numbers
    public static ArrayList<Integer> subtract(ArrayList<Integer> A, ArrayList<Integer> B, int b) {
        
        int i, t = 0;
        ArrayList<Integer> X = new ArrayList<>();
        ArrayList<Character> Y = new ArrayList<>();
        int x, y;
        
        // Reverse the two arrays A and B
        Collections.reverse(A);
        Collections.reverse(B);
        
        // IMPORTANT: A.length > B.length (arrange the two numbers before the function call)
        for(i = 0; i < A.size(); i++) {
            x = A.get(i);
            y = (i < B.size()) ? B.get(i) : 0;
            
            x -= y + t;
            t = (x < 0) ? 1 : 0;
            x += t * b;
            
            // Add the value to the new array list
            X.add(i, x);
        }
        
        while(X.size() > 1 && X.get(X.size()-1) == 0) {
            X.remove(X.size()-1);
        }
        
        // Reverse the number and return from function call
        Collections.reverse(A);
        Collections.reverse(B);
        Collections.reverse(X);
        return X;
    }
    
    // Function for multiplying two numbers
    public static ArrayList<Integer> multiply(ArrayList<Integer> A, ArrayList<Integer> B, int b) {
        
        ArrayList<Integer> X = new ArrayList<>();
        int i = 0,j = 0,t = 0;
        int x, y;
        
        // Reverse the two arrays A and B
        Collections.reverse(A);
        Collections.reverse(B);
        
        for(i = 0; i < A.size(); i++) {
            x = (i < A.size()) ? A.get(i) : 0;
            for(j = 0; (j < B.size()) || (t != 0); j++, t /= b) {
                y = (j < B.size()) ? B.get(j) : 0;
                
                if(X.size() - 1 >= i+j) {
                    t += X.get(i+j) + x * y;
                    NUMBER_OPERATIONS += 2;
                    if(i+j <= X.size() - 1) {
                        X.set(i+j, t % b);
                    }
                    else {
                        X.add(i+j, t % b);
                    }
                } else {
                    t += x * y;
                    X.add(X.size(), t % b);
                    NUMBER_OPERATIONS += 2;
                }
            }
        }
        
        // Reverse the number and return from function call
        Collections.reverse(A);
        Collections.reverse(B);
        Collections.reverse(X);
        return X;
    }
    
    // Function for multiplying two numbers using Karatsuba
    public static ArrayList<Integer> karatsuba(ArrayList<Integer> A, ArrayList<Integer> B, int b) {
        // Declare two arraylists for each of the two numbers
        ArrayList<Integer> X_hi = new ArrayList<>();
        ArrayList<Integer> X_lo = new ArrayList<>();
        ArrayList<Integer> Y_hi = new ArrayList<>();
        ArrayList<Integer> Y_lo = new ArrayList<>();
        // Declare array for storing result
        ArrayList<Integer> X = new ArrayList<>();
        ArrayList<Integer> Y = new ArrayList<>();
        int i = 0,j = 0, temp, tempD = 1;
        
        // Make A and B of equal size by adding leading zeros
        if(A.size() > B.size()) {
            // We first revert the ArrayList so that adding is made easier
            Collections.reverse(B);
            int dim = A.size();
            while(B.size() != dim) {
                B.add(0);
            }
            // Reverse the array again so we have it in the correct orientation
            Collections.reverse(B);
        } else if(A.size() < B.size()) {
            Collections.reverse(A);
            int dim = B.size();
            while(A.size() != dim) {
                A.add(0);
            }
            Collections.reverse(A);
        }
        
        // If the size of both the arrays is one we just multiply the two digits and return the value
        // IMPORTANT: The size will be the same for both arrays
        if(A.size() == 1) {
            int digit = A.get(0) * B.get(0);
            // Also count the number of elementary operations (2 x add; 2 x subtract; 2 x multiply)
            NUM_OPERATIONS += 1;
            while(digit != 0) {
                X.add(digit % b);
                digit /= b;
            }
            Collections.reverse(X);
            return X;
        }
        
        // We calculate the length of the two numbers
        // IMPORTANT: The two numbers have to be of equal length
        int length = A.size();
        // We calculate the first half of the number
        int hi = length / 2;
        // We calculate the second half of the number
        int lo = length - hi;
        
        j = 0;
        // Split the first number in two halves
        for(i = 0; i < hi; i++)
            X_hi.add(i, A.get(i));
        for(i = hi; i < A.size(); i++)
            X_lo.add(j++, A.get(i));
        
        j = 0;
        // Split the second number in two halves
        for(i = 0; i < hi; i++)
            Y_hi.add(i, B.get(i));
        for(i = hi; i < B.size(); i++)
            Y_lo.add(j++, B.get(i));
        
        // Apply the Karatsuba algorithm; we get P1, P2, P3
        ArrayList<Integer> P1 = karatsuba(X_hi, Y_hi, b);
        ArrayList<Integer> P2 = karatsuba(X_lo, Y_lo, b);
        ArrayList<Integer> P3 = karatsuba(add(X_hi, X_lo, b), add(Y_hi, Y_lo, b), b);
        
        // Calculate b^length and b^(length/2)
        ArrayList<Integer> pow1 = new ArrayList<Integer>();
        ArrayList<Integer> pow2 = new ArrayList<Integer>();
        ArrayList<Integer> base = new ArrayList<Integer>();
        pow1.add(b);
        base.add(b);
        if(length % 2 == 1) temp = length / 2 + 1;
        else temp = length / 2;
        for(i = 1; i < 2 * temp; i++) {
            if(i == temp) pow2 = pow1;
            pow1 = multiply(pow1, base, b);
        }
        
        // Account for the case in which either of the three P's is empty
        if (P1.size() == 0) P1.add(0);
        if (P2.size() == 0) P2.add(0);
        if (P3.size() == 0) P3.add(0);
        
        // Combine the three products to get the final result.
        X = multiply(pow1, P1, b);
        Y = subtract(P3, P1, b);
        Y = subtract(Y, P2, b);
        Y = multiply(pow2, Y, b);
        X = add(X, Y, b);
        X = add(X, P2, b);
        // Also count the number of elementary operations (2 x addition; 2 x subtraction)
        NUM_OPERATIONS += 4;
        
        // This is also equivalent to the following:
        // X = add(add(multiply(P1, pow1, b), multiply((subtract(subtract(P3, P1, b),P2, b)), pow2, b), b), P2, b);
        
        // Remove all the 0's from the start of the array list
        i = 0;
        if(X.size() > 0) {
            tempD = X.get(0);
        }
        while(tempD == 0 && X.size() > 0) {
            X.remove(0);
            if(X.size() > 0) tempD = X.get(0);
        }
        
        return X;
    }
    
    // Function for converting the computed array list back to chars
    // This function also prints the result to the console
    public static void computeResult(ArrayList<Integer> X, int i) {
        
        ArrayList<Character> Y = new ArrayList<>();
        
        for(int j = 0; j < i; j++) {
            // Get the value of the new digit
            int temp = X.get(j);
            // Convert it back using the map
            char digit = letters.get(temp);
            
            // NOTE: When adding the chars to the new array we DO NOT revert the order
            // We will have the correct orientation of digits after this loop
            Y.add(digit);
        }
        
        // Print the correct format: [result]
        // This is to let the user know that this is the result of the computation
        System.out.print("[result] ");
        // Print the correct sign for the final computation result
        if(SIGN != ' ' && SIGN != '+') {
            System.out.print(SIGN);
            // Revert the sign back to its original state
            SIGN = ' ';
        }
        
        // Return the result i.e: print the array Y to the console
        // IMPORTANT: The array Y has the digits in the correct order
        for(int j = 0; j < Y.size(); j++) {
            System.out.print(Y.get(j));
        }
        System.out.println();
    }
    
    public static void answerGiven(String answer) {
        // Check to see if the user has provided an answer
        if(answer.length() >= 8) {
            String word = answer.substring(1, 7);
            if(word.equals("answer")) hasAnswer = true;
            else hasAnswer = false;
        }
    }
}

/*
*/

/* 
// Table that shows which is the final sign of the computation \\
// | op  | x | y | op' | sign |
// | add | + | + | add |   +  |
// | add | + | - | sub |  +/- |     x > y --> (sign = -) OR x <= y --> (sign = +)
// | add | - | + | sub |  +/- |     x >= y --> (sign = +) OR x < y --> (sign = -)
// | add | - | - | add |   -  |
// | sub | + | + | sub |  +/- |     x >= y --> (sign = +) OR x < y --> (sign = -)
// | sub | + | - | add |   +  |
// | sub | - | + | add |   -  |
// | sub | - | - | sub |  +/- |     x > y --> (sign = -) OR x <= y --> (sign = +)
*/
