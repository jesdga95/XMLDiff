/*
 * Copyright (C) 2012 Jesús David Gulfo Agudelo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xmldiff;

import java.io.*;

/**
 * @author D4rKn3sSyS @ XDA-developers
 */
public class XMLDiff {

    private static final String[] HEADERS = {"<string name=\""};
    private static final int STRING = 1;
    
    public static void main(String[] args) {
        if(args.length < 1)
            System.out.println("Not enough arguments. To get help, use java -jar XMLDiff.jar help");
        else if(args[0].equals("help"))
            System.out.println("\nUsage: java -jar XMLDiff.jar oldfile.xml newfile.xml. Compares oldfile to newfile and writes differences into a file"
                    + "\n\nUsage: java -jar XMLDiff oldfile.xml newfile.xml exclude.xml. Excludes selected values"
                    + "\n\nCompatible tags:"
                    + "\n\n -> <string name=\"*\">*</string>");
        else if(args.length == 2){
            String[] oldText = fileToArray(args[0]);
            String[] newText = fileToArray(args[1]);
            String diff = getDiff(oldText, newText);
            writeFile(diff);
        } else if(args.length == 3){
            String[] oldText = fileToArray(args[0]);
            String[] newText = fileToArray(args[1]);
            String exclude = fileToString(args[2]);
            String diff = getDiff(oldText, newText, exclude);
            writeFile(diff);
        } else
            throw new IllegalArgumentException("Too many arguments");
    }
    
    private static String getDiff(String[] o, String[] n){
        return getDiff(o, n, null);
    }
    
    private static String getDiff(String[] o, String[] n, String e){
        String diff = "";
        for(int j=0; j<n.length-1; j++){
            String l = n[j];
            if(findMatches(l, HEADERS)){
                String id;
                int type = 0;
                for(int i=0; i<HEADERS.length; i++){
                    if(l.contains(HEADERS[i])){
                        type = i+1;
                    }
                }
                switch(type){
                    case STRING:
                        id = l.substring(l.indexOf(HEADERS[0])+HEADERS[0].length(), nthOccurrence(l, (char) '"', 1));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid type. See java -jar XMLDiff.jar help");
                        
                }
                for(int i=0; i<o.length-1; i++){
                    String l2 = o[i];
                    if(l2.contains(id) && !diff.contains(id)){
                        if(e == null)
                            diff += l2 + "\n";
                        else if(!e.contains(id))
                            diff += l2 + "\n";
                    }
                }
            }
        }
        return diff;
    }
    
    private static boolean findMatches(String in, String[] array){
        try{
            for(int i=0; i<array.length; i++){
                if(in.contains(array[i]))
                    return true;
            }
            return false;
        }catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public static int nthOccurrence(String str, char c, int n) {
        int pos = str.indexOf(c, 0);
        while (n-- > 0 && pos != -1)
            pos = str.indexOf(c, pos+1);
        return pos;
    }
    
    private static String[] fileToArray(String input){
        return fileToString(input).split("\n");
    }
    
    private static String fileToString(String input){
        String text="";
        int read, N = 1024*1024;
        char[] buffer = new char[N];
        try {
            FileReader fr = new FileReader(new File(input));
            BufferedReader br = new BufferedReader(fr);
            while(true) {
                read = br.read(buffer, 0, N);
                text+=new String(buffer, 0, read);
                if(read < N) 
                    break;
            }
        } catch(Exception ex) {
            System.err.println("Error while loading selected file: " + ex.getMessage());
        }
        return text;
    }

    private static void writeFile(String diff) {
        try{
            String output = System.getProperty("user.dir")+ File.separator + "diff.txt";
            System.out.println("Sucessfully written "+output);
            FileWriter fstream = new FileWriter(output);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(diff);
            out.close();
        }catch (IOException e){
            System.err.println("Error writing diff file: " + e.getMessage());
        }
    }
}
