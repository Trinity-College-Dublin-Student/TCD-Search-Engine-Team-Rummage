/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucene;

/**
 * @author Nicky
 */
public class Parser {

    public static void main(String[] args) {

        System.out.println("Running parser, please be patient!");

        LATimesParser.Parse();
        System.out.println("LATimes complete..");
        FTParser.Parse();
        System.out.println("FT complete..");
        FbisParser.Parse();
        System.out.println("FBis complete..");
        FRParser.Parse();
        System.out.println("FRis complete..");
        System.out.println("Parsing Complete!");

    }
}
