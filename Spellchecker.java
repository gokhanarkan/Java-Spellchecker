import java.util.*;
import java.io.*;

public class Spellchecker {

    // These are the data structures that handles the data.
    public static ArrayList<String> dictionary = new ArrayList<>();
    public static ArrayList<String> sentence = new ArrayList<>();
    public static ArrayList<String> typoWords = new ArrayList<>();

    protected Spellchecker() throws FileNotFoundException {

        // Putting all the words in a single ArrayList. It's sorted already.
        Scanner s = new Scanner(new File("dictionary.txt"));

        while (s.hasNext()) {
            dictionary.add(s.next());
        }

        s.close();

    }

    // Basic boolean method to check if the relevant word is contained in our dictionary.
    protected void findString(String k){

        String s = k.replaceAll("[.,;:?!]", "");
        s = s.toLowerCase(); // These two changes will remove the symbols and make every word in lowercase to prevent issues.

        // Adding typo words to a typowords container
        if(dictionary.contains(s)) {
            sentence.add(k);
        } else if(!dictionary.contains(s)) {
            if(!typoWords.contains(s)) {
                typoWords.add(k);
            }
            sentence.add(k);
        }

    }

    protected void correction(ArrayList<String> typoWords){

        Scanner scn = new Scanner(System.in);

        for(int i = 0; i < typoWords.size(); i++){
            // Clearing the string
            String k = typoWords.get(i);
            String s = k.replaceAll("[.,;:?!]", "");
            s = s.toLowerCase();

            // Console initiated
            System.out.println("The word " + s + " is not in the dictionary. Would you like to add?");
            String response = scn.next();
            response = response.toLowerCase();
            label:
            while (true) {
                switch (response) {
                    case ("yes"):
                    case ("add"):
                        dictionary.add(typoWords.get(i));
                        break label;
                    case "no":
                        System.out.println("Please enter the replacement. If you want to remove, please type !remove."); // !remove command is just to delete the word if user wants it
                        String replacement = scn.next();
                        if (replacement.equals("!remove")) {
                            sentence.remove(i);
                            break label;
                        } else { // If !remove is not typed, then we put the replacement here
                            String regex = replacement; // This is the original version of the word
                            replacement = replacement.replaceAll("[.,;:?!]", "");
                            replacement = replacement.toLowerCase();

                            int j = 0;
                            while(j != sentence.size()){

                                if(sentence.get(j).equals(typoWords.get(i))){
                                    sentence.set(j, regex);
                                }

                                j++;

                            }
                            break label;
                        }
                    default:
                        System.out.println("Please key in either yes or no.");
                        response = scn.next();

                }
            }
        }
        scn.close();
    }

    // SoundEx Algorithm
    public String soundEx(String k){

        char[] cr = k.toUpperCase().toCharArray(); // Creating a charArray from the String and making every letter capital
        char first = cr[0]; // Saving the first letter of our string for future use

        for (int i = 0; i < cr.length; i++) { // Coding the string with numbers
            switch (cr[i]) {
                case 'B':
                case 'F':
                case 'P':
                case 'V': {
                    cr[i] = '1';
                    break;
                }

                case 'C':
                case 'G':
                case 'J':
                case 'K':
                case 'Q':
                case 'S':
                case 'X':
                case 'Z': {
                    cr[i] = '2';
                    break;
                }

                case 'D':
                case 'T': {
                    cr[i] = '3';
                    break;
                }

                case 'L': {
                    cr[i] = '4';
                    break;
                }

                case 'M':
                case 'N': {
                    cr[i] = '5';
                    break;
                }

                case 'R': {
                    cr[i] = '6';
                    break;
                }

                default: { // If the character is a, e, i, o, u, y, h, w; it will put 0
                    cr[i] = '0';
                    break;
                }
            }
        }

        String out = "" + first; // Converting char into String

        for(int j = 1; j < cr.length; j++){ // The loop that, if the char code is consecutive, takes only the first one also discarding the zeros.

            if(cr[j] != cr[j-1] && cr[j] != '0'){
                out += cr[j];
            }
        }

        out += "0000"; // Debug math. Just to make it 4 chars in case it's blank.
        out = out.substring(0,4); // Taking only the first 4 character.

        return out;
    }

    // Levenshtein Algorithm
    public int distanceCalc(String typo, String maybe){

        int[] d1 = new int[typo.length() + 1]; // Array of distance
        int[] d2 = new int[typo.length() + 1];  // Array of distance

        // calculating the initial cost of typo word
        for (int i = 0; i < typo.length() + 1; i++){
            d1[i] = i;
        }

        // calculating the transformation cost of possible word
        for (int j = 1; j < maybe.length() + 1; j++) {
            // calculating the initial cost of typo word
            d2[0] = j;

            // calculating the transformation cost of possible word
            for(int i = 1; i < typo.length() + 1; i++) {
                // matching the strings
                int match = (typo.charAt(i - 1) == maybe.charAt(j - 1)) ? 0 : 1;

                // transformation calculation
                int cost_replace = d1[i - 1] + match;
                int cost_insert  = d1[i] + 1;
                int cost_delete  = d2[i - 1] + 1;

                // minimums
                d2[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swapping the values
            int[] replacement = d1;
            d1 = d2;
            d2 = replacement;
        }
        return d1[typo.length()];
    }
    // This method exports the corrected sentence as a txt file to the main directory.
    public void writeOut(ArrayList<String> sentence) throws IOException {

        FileWriter writerF = new FileWriter("correctedSentence.txt");
        BufferedWriter bwF = new BufferedWriter(writerF);

        for(int i = 0; i < sentence.size(); i++){
            if(i == sentence.size() - 1){   // Prevent the extra line by adding nothing on the last word
                bwF.write(sentence.get(i).toString());
            } else {
                bwF.write(sentence.get(i).toString() + " ");
            }
        }

        bwF.close();
        writerF.close();
        System.out.println("The file exported successfully.");

    }
    // This method sorts the words alphabetically and then updates the txt file.
    public void updateDictionary(ArrayList<String> dictionary) throws IOException {

        Collections.sort(dictionary);

        FileWriter writer = new FileWriter("dictionary.txt");
        BufferedWriter bw = new BufferedWriter(writer);

        for(int i = 0; i < dictionary.size(); i++){
                writer.write(dictionary.get(i).toString() + "\n");
        }

        bw.close();
        writer.close();

    }

    public static void main(String[] args) throws FileNotFoundException {

        Spellchecker checker = new Spellchecker();

        if(args.length > 0) {   // This is the code fragment that be used to run a file from the command line

            File commandFile = new File(args[0]);
            Scanner file = new Scanner(new File(String.valueOf(commandFile)));
            while (file.hasNext()) {
                String k = file.next();
                checker.findString(k);  // Putting the words into arraylists whether they're in the library or not
            }

            file.close();

        } else {    // If there is no file initiated, then program will ask you to type a sentence

            System.out.println("Enter your text below:");
            Scanner scn = new Scanner(System.in); // Copying a text file
            String k = scn.nextLine(); // Getting the whole sentence.
            String [] kr = k.split(" "); // Putting the array words into an Array so I can work.

            for(String things : kr){

                checker.findString(things); // Putting the words into arraylists whether they're in the library or not

            }

            scn.close();
        }

        // Correction method checks every word within the dictionary and asks if it's correct or need a replacement
        checker.correction(typoWords);

        try {
            checker.updateDictionary(dictionary);   // Export method to change the dictionary file
        } catch (IOException io){
            System.exit(2);
        }

        try{
            checker.writeOut(sentence); // Export method to add the sentence as a txt file to the local root
        } catch(IOException ioe){
            System.exit(1);
        }
    }

}