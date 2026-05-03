

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.*;
import java.util.LinkedList;
/**
 *
 * @author Oleg
 */

public class FileReader {

    private FileReader() {}

    // --- Загрузка из текста ---
    public static LinkedList<RecIntegral> loadText(File file)
            throws IOException, RecIntegral.InvalidRangeException {

        LinkedList<RecIntegral> list = new LinkedList<>();

        try (BufferedReader reader =
                 new BufferedReader(new java.io.FileReader(file))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split(";");

                if (parts.length != 4) {
                    throw new IOException(
                        "Ошибка формата в строке " + lineNumber
                    );
                }

                try {
                    // --- ВАЖНО: используем parseAndValidate ---
                    double a = RecIntegral.parseAndValidate(parts[0]);
                    double b = RecIntegral.parseAndValidate(parts[1]);
                    double h = RecIntegral.parseAndValidate(parts[2]);

                    RecIntegral rec = new RecIntegral(a, b, h);

                    // обработка result
                if (!parts[3].equals("null")) {

                    String value = parts[3].replace(',', '.');

                    try {
                        double result = Double.parseDouble(value);
                        rec.setResult(result);
                    } catch (NumberFormatException e) {
                        // при ошибке можно либо пропустить, либо вывести сообщение
                        rec.setResult(null);
                    }
                }

                    list.add(rec);

                } catch (RecIntegral.InvalidRangeException ex) {
                    throw new RecIntegral.InvalidRangeException(
                        "Ошибка в строке " + lineNumber + ": " + ex.getMessage()
                    );
                }
            }
        }

        return list;
    }

    // --- Загрузка из бинарного файла ---
    public static LinkedList<RecIntegral> loadBinary(File file)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream in =
                 new ObjectInputStream(
                     new BufferedInputStream(
                         new FileInputStream(file)))) {

            Object obj = in.readObject();

            if (!(obj instanceof LinkedList)) {
                throw new IOException("Некорректный формат файла");
            }

            return (LinkedList<RecIntegral>) obj;
        }
    }
}
