package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    /*
    * A projekt felépítése:
    * (IntelliJ projekt hierarchia,
    * a beadott fájlhoz csak a Main.java, input_sample.txt,
    * output_sample.txt van csatolva)
    * src
    *   '-> com.company
    *       '-> Main
    *   '-> txt
    *       '-> input_sample.txt
    *       '-> output_sample.txt (ide fogja generálni)
    *
    * A fájl felépítése:
    *
    *   - Egyszerű regEx példák
    *   - Összetett regEx példa + I/O műveletek
    *       (A main() függvény alatt található segédfüggvényekből épül fel)
    *   - 2db RegEx illesztés összehasonlítás (idő alapú)
    * */

    public static void main(String[] args) {

        //Egyszerű regEx példák
        System.out.println("Capturing group");
        boolean isMatch =
                Pattern.compile("(\\s*)(.*)")
                .matcher("  valami")
                .matches();

        if (isMatch) System.out.println("A minta illeszkedik a textre!");
        else System.out.println("A minta NEM illeszkedik a textre!");

        //-----------------------------------------------------------------

        System.out.println("\nNon-capturing group");
        Pattern pattern = Pattern.compile("(?:\\s*)(?:.*)");
        Matcher matcher = pattern.matcher("  valami");
        System.out.println(matcher.matches() ? "Illeszkedik!" : "Nem Illeszkedik!");
        for (int i = 0; i <= matcher.groupCount(); i++) {
            System.out.println(i + ". csoport: " + matcher.group(i));
        }

        //-----------------------------------------------------------------

        System.out.println("\nBackreference");
        pattern = Pattern.compile("(\\w+)\\s\\1");
        matcher = pattern.matcher("szavdazvvalami valamisandaifi");
        System.out.println(matcher.find() ? "Illeszkedik!" : "Nem Illeszkedik!");
        for (int i = 0; i <= matcher.groupCount(); i++) {
            System.out.println(i + ". csoport: " + matcher.group(i));
        }

        //-----------------------------------------------------------------

        System.out.println("\nLookahead/Lookaround/Non-consuming group");
        pattern = Pattern.compile("valami(?=2)");
        matcher = pattern.matcher("valami1valami2valami3");
        System.out.println(matcher.find() ? "Illeszkedik!" : "Nem Illeszkedik!");
        for (int i = 0; i <= matcher.groupCount(); i++) {
            System.out.println(i + ". csoport: " + matcher.group(i));
        }

        //-----------------------------------------------------------------

        System.out.println("\nAtomi csoport");
        boolean finded = Pattern.compile("(?>vala|val)mi")
                .matcher("valami")
                .find();

        boolean findNext = Pattern.compile("(?>vala|val)mi")
                .matcher("valmi")
                .find();

        if (finded) {
            System.out.println("A minta illeszkedik az " +
                    "első megadott értékre mind a két esteben!");
            System.out.println("'valami' : " +
                    (finded ? "Illeszkedik!" : "Nem illszkedik"));

            System.out.println("'valmi' : " +
                    (finded ? "Illeszkedik!" : "Nem illszkedik"));

        } else { System.out.println("A minta NEM talált értéket!"); }

        //-----------------------------------------------------------------

        System.out.println("\nLazy iterálás");
        pattern = Pattern.compile("\\w+\\s??");
        matcher = pattern.matcher("valami valami valami valami valami");
        System.out.println(matcher.find() ? "Illeszkedik!" : "Nem Illeszkedik!");
        for (int i = 0; i <= matcher.groupCount(); i++) {
            System.out.println(i + ". csoport: " + matcher.group(i));
        }


        //-----------------------------------------------------------------

        //Összetett regEx példa
        String filePath = readFilePathFromConsole();
        List<String> inputTxtAsList = readFile(filePath);
        List<String> resultList = new ArrayList<>();

        /*
        * Add vissza az input_sample.txt-ből azokat a számokat amelyek duplázott értékek és
        * az utána lévő szóközzel elválasztott tagjukat
        * */
        boolean localMatch;
        for (String line : inputTxtAsList) {
            pattern = Pattern.compile("(\\w+)\\s\\1(?:\\d)??");
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    resultList.add("" + matcher.group(i));
                }
            }
        }
        writeToTxt(resultList, "sample");

        //-----------------------------------------------------------------

        //2db RegEx illesztés összehasonlítás (idő alapú)
        complexMethod();
        simpleMethod();
    }

    public static void complexMethod() {

        long startTime = System.nanoTime();

        Pattern pattern = Pattern.compile("valami(?=\\d)");
        Matcher matcher = pattern.matcher("valami1valami2valami3");

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        System.out.println(matcher.find() ? "\nIlleszkedik!" : "\nNem Illeszkedik!");
        System.out.println("A complexMethod() függvény " +
                duration * Math.pow(10, -9) + " mp-ig futott.");
    }

    public static void simpleMethod() {

        long startTime = System.nanoTime();

        Pattern pattern = Pattern.compile("[a-z]+");
        Matcher matcher = pattern.matcher("valami1valami2valami3");

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        System.out.println(matcher.find() ? "\nIlleszkedik!" : "\nNem Illeszkedik!");
        System.out.println("A simpleMethod() függvény " +
                duration * Math.pow(10, -9) + " mp-ig futott.");
    }

    public static List<String> readFile(String filePath) {

        String data[] = filePath.split("/");
        String fileName = "";
        for (String d : data) {
            if (d.contains(".txt")) {
                fileName = d;
            }
        }

        List<String> lines = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader("src/txt/" + fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();
            while (line != null) {
                lines.add(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileReader.close();
            System.out.println("\nSikeres beolvasás!");

        } catch (Exception e) { System.out.println("Hiba beolvasás közben:\n" + e); }

        return lines;
    }

    public static void writeToTxt(List<String> resultList, String fileName) {

        try {
            FileWriter myWriter = new FileWriter("src/txt/output_" + fileName + ".txt");
            for (String line : resultList) {
                myWriter.write(line + "\n");
            }
            myWriter.write("");
            myWriter.close();
            System.out.println("\nSikeres fájlbaírás!");

        } catch (Exception e) { System.out.println("Hiba írás közben:\n" + e); }
    }

    public static String readFilePathFromConsole() {

        System.out.println("\nKérlek adj meg egy elérési útvonalat, majd nyomj egy Enter-t.\n" +
                "Az útvonal lehet relatív és abszolút is, viszont a mappaszerkezet csak " +
                "'/' slash karaktert tartalmazhat '\\' backsplash-t NEM!\n" +
                "(Példa txt neve: input_sample.txt)\n");

        System.out.println("A fájlokat az 'src/txt/' mappából fogja beolvasni:");
        System.out.print(">> ");
        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine();
    }


}
