

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
public class FileWriter {
    // запрет создания экземпляров
    private FileWriter() {}

    // --- Сохранение в текст ---
    public static void saveText(File file, LinkedList<RecIntegral> dataList)
            throws IOException {

        try (BufferedWriter writer =
                 new BufferedWriter(new java.io.FileWriter(file))) {

            for (RecIntegral rec : dataList) {

                String resultStr = (rec.getResult() == null)
                        ? "null"
                        : rec.getResult().toString();

                String line =
                        rec.getFrom() + ";" +
                        rec.getTo() + ";" +
                        rec.getStep() + ";" +
                        resultStr;

                writer.write(line);
                writer.newLine();
            }
        }
    }

    // --- Сохранение в бинарный файл ---
    public static void saveBinary(File file, LinkedList<RecIntegral> dataList)
            throws IOException {

        try (ObjectOutputStream out =
                 new ObjectOutputStream(
                     new BufferedOutputStream(
                         new FileOutputStream(file)))) {

            out.writeObject(dataList);
        }
    }
}
