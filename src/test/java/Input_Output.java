import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Input_Output {
    private static Input_Output instance;

    private Input_Output() {

    }

    public static Input_Output getInstance() {
        if (instance == null) instance = new Input_Output();
        return instance;
    }

    public String leer_fichero(String nombre_fichero) throws IOException {
        String result = "";
        String line;
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(nombre_fichero);

            bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            bufferedReader.close();

            return result;
        } finally {
            if (bufferedReader != null) bufferedReader.close();
            if (fileReader != null) fileReader.close();
        }
    }

    public List<List<String>> read_tree_matrix(String nombre_fichero) throws IOException {
        String line;
        List<String> row;
        List<List<String>> tree = new ArrayList<>();
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(nombre_fichero);

            bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals("")) {
                    row = new ArrayList<>();
                    //System.out.println(line);
                    String[] words = line.split("\\s+");
                    for (int i = 0; i < words.length; ++i) {
                        //System.out.println(words[i]);
                        row.add(words[i]);
                    }
                    //System.out.println("tree row length" + words.length);
                    tree.add(row);
                }
            }

            //System.out.println("tree size " + tree.size());

            bufferedReader.close();

        /*for (int i = 0; i < tree.size(); ++i) {
            String res = "";
            List<String> aux = tree.get(i);
            for (int j = 0; j < aux.size(); ++j) {
                res += aux.get(j) + " ";
            }
            System.out.println(res);
        }*/

            return tree;
        } finally {
            if (bufferedReader != null) bufferedReader.close();
            if (fileReader != null) fileReader.close();
        }
    }

    public JSONObject read_json_file(String nombre_fichero) throws IOException, JSONException {

        String result = leer_fichero(nombre_fichero);

        return new JSONObject(result);

    }

    public void borrar_archivo(String nombre_fichero) throws IOException {
        File file = new File(nombre_fichero);

        if (file.delete()) {
            System.out.println(file.getName() + " is deleted!");
        } else {
            System.out.println("Delete operation is failed.");
        }
    }

    public void crear_archivo(String nombre_fichero) throws IOException {
        File file = new File(nombre_fichero);
        if (file.createNewFile())
        {
            System.out.println("File is created!");
        } else {
            System.out.println("File already exists.");
        }
    }

    public void escribir_fichero(String nombre_fichero, String content) throws IOException {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(nombre_fichero);
            bw = new BufferedWriter(fw);
            bw.write(content);
            System.out.println("Done");
        } finally {
            if (bw != null) bw.close();
            if (fw != null) fw.close();
        }
    }

}
