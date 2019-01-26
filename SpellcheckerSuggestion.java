import java.util.*;
import java.io.*;
// Extending base file to reach the methodsS
public class SpellcheckerSuggestion extends Spellchecker {

    private static LinkedHashMap<String, String> dictMap = new LinkedHashMap<>(); // The Linked Hash Map stores the soundEx value of the dictionary.
    private ArrayList<String> matchedWords = new ArrayList<>(); // This is the main ArrayList that I will put matching words. I need to reset it for every word though.

    private SpellcheckerSuggestion() throws FileNotFoundException {
        super(); // Get everything
    }

    private void tableMaker(ArrayList<String> dictionary){ // The method that creates a Linked Hash Map from the strings' SoundEx values.

        for(String words : dictionary){

            String k = soundEx(words);
            dictMap.put(words, k);

        }
    }

    // ----------------------- Debug method to check if everything's working or not --------------------------- //
    private void dictMaker(LinkedHashMap<String, String> dictMap) throws IOException {

        FileWriter writer = new FileWriter("hashmaptest.txt");
        Set<Map.Entry<String, String>> keys = dictMap.entrySet();

        Iterator iterator = keys.iterator();

        while(iterator.hasNext()){
            Map.Entry words = (Map.Entry)iterator.next();
            writer.write(words.getKey() + "=" + words.getValue() + "\n");
        }
    }
    // ----------------------- Debug method to check if everything's working or not --------------------------- //

    private void wordMatching(String k){ // This method matches the typo word with the same soundEx value in the dictionary.

        Set<Map.Entry<String, String>> keys = dictMap.entrySet(); // Creating a set of entries

        Iterator iterator = keys.iterator();

        while(iterator.hasNext()){

            Map.Entry matching = (Map.Entry) iterator.next();

            if(matching.getValue().equals(soundEx(k))){ // If the value is equal, we add into a new ArrayList called matchedWords.
                int l = distanceCalc(k, matching.getKey().toString()); // l variable represents the matched word edit distance
                if(l <= 2) { // The if statement that checks the distance with the possible number using Levenshtein algorithm
                    matchedWords.add(matching.getKey().toString());
                }
            }
        }
    }

    private void correctionSuggestion(ArrayList<String> typoWords){

        Scanner sc = new Scanner(System.in);

        for(int j = 0; j < typoWords.size(); j++){

            String k = typoWords.get(j);
            String s = k.replaceAll("[.,;:?!]", "");
            s = s.toLowerCase();

            System.out.println("The word " + s + " is not in the dictionary. Would you like to add?");
            String response = sc.next();
            response = response.toLowerCase();

            advise:
            while(true){
                switch(response){

                    case("yes"): // If the word is not in the dictionary but user is sure it's correct, it adds into the dictionary and goes on.

                        dictionary.add(typoWords.get(j));
                        break advise;

                    case("no"): // If the word is not correct and user doesn't want to add the dictionary, there ara two options

                        wordMatching(typoWords.get(j));
                        // If there is a suggestion we list here
                        if(dictMap.containsValue(soundEx(typoWords.get(j))) && matchedWords.size() > 0){
                            System.out.println("Suggestion:");
                            System.out.println(matchedWords); // Printing out the suggestions matched
                            System.out.println("Please enter the replacement.");
                            matchedWords.clear(); // Clearing the arraylist after the using to re-use for other words
                            String replacement = sc.next();
                            String regex = replacement; // This is the original version of the word
                            replacement = replacement.replaceAll("[.,;:?!]", "");
                            replacement = replacement.toLowerCase();

                            int i = 0;
                            while(i != sentence.size()){

                                if(sentence.get(i).equals(typoWords.get(j))){
                                    sentence.set(i, regex);
                                }

                                i++;

                            }
                            break advise;
                        }
                        else{ // If there is no suggestion we list here
                            System.out.println("There are no suggestions. Please enter the replacement.");
                            String replacement = sc.next();
                            String regex = replacement;
                            replacement = replacement.replaceAll("[.,;:?!]", "");
                            replacement = replacement.toLowerCase();

                            int i = 0;
                            while(i != sentence.size()){

                                if(sentence.get(i).equals(typoWords.get(j))){
                                    sentence.set(i, regex);
                                }

                                i++;

                            }
                            break advise;
                        }
                    default: // The default statement forces either write yes or no to prevent errors
                        System.out.println("Please enter either yes or no");
                        response = sc.next();
                }
            }

        }
        sc.close();
    }

    public static void main(String[] args) throws FileNotFoundException {

        SpellcheckerSuggestion suggestion = new SpellcheckerSuggestion();

        suggestion.tableMaker(dictionary);  // Making a imagination table from linked hash map to store all the sound ex values with the words

        // --- Debugger --- // This is to debug if something goes wrong.

        /*
        try {
            suggestion.dictMaker(dictMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        // --- Debugger --- //

        if(args.length > 0) { // This is the code fragment that be used to run a file from the command line
            File suggestionFile = new File(args[0]);
            Scanner file = new Scanner(new File(String.valueOf(suggestionFile)));
            while(file.hasNext()){
                String things = file.next();
                suggestion.findString(things);
            }
        } else {    // If there is no file initiated, then program will ask you to type a sentence

            System.out.println("Enter your text below:");
            Scanner scn = new Scanner(System.in); // Copying a text file
            String k = scn.nextLine(); // Getting the whole sentence.
            String[] kr = k.split(" "); // Putting the array words into an Array List so I can work.

            for (String things : kr) {

                suggestion.findString(things);

            }
        }
        // Correction method checks every word within the dictionary and asks if it's correct or need a replacement with suggestions
        suggestion.correctionSuggestion(typoWords);

        try {
            suggestion.updateDictionary(dictionary);    // Export method to change the dictionary file
        } catch (IOException io){
            System.exit(2);
        }

        try{
            suggestion.writeOut(sentence);  // Export method to add the sentence as a txt file to the local root
        } catch(IOException ioe){
            System.exit(1);
        }

    }

}