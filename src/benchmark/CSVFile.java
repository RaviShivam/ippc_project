package benchmark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVFile {

    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR  = "\n";
    private static final String FILE_PLACE = "./results/";

    private static final String FILE_HEADER = "";

    public static void writeCSVFile(String fN, ArrayList<String> List) {

        String fileName = fN;

        try{
            int i = 0;
            if(new File(FILE_PLACE + fileName + ".csv").exists()) {
                i++;
                while(new File(FILE_PLACE + fileName + "_" + i + ".csv").exists()) {
                    i++;
                }
            }

            if(i != 0){
                fileName = fileName + "_" + i;
            }

            FileWriter fileWriter = new FileWriter(FILE_PLACE + fileName + ".csv");

            for (int j = 0; j < List.size() ; j++) {
                fileWriter.write(List.get(j));
                fileWriter.write(NEW_LINE_SEPARATOR);
            }

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
