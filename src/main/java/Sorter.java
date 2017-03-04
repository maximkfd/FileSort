import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * @author Denis.Repp
 */
public class Sorter {

    public void sort(final String filename) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        long begin = System.currentTimeMillis();

        System.out.println(Runtime.getRuntime().totalMemory() + ":  " + Runtime.getRuntime().freeMemory());
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        //прочитать несколько строк
        ArrayList<String> list;

        File sortedFileIn = File.createTempFile("to read", null);
        File sortedFile = File.createTempFile("to write", null);
        File result = new File("res");
        int k = 0;
        while (reader.ready()) {
//            System.gc();
            list = new ArrayList<>();

            long size = 0;
            //Читаем кусок из 1gb файла
//            while (reader.ready() &&  runtime.freeMemory() > runtime.totalMemory() - runtime.freeMemory()) {
            while (reader.ready() &&  size < 32000000) {
                final String tmp = reader.readLine();
                size += tmp.getBytes().length;
                list.add(tmp);
//                list.add(reader.readLine());
            }
            list.sort(null);
            System.out.println("read " + size + " bytes");
            if (sortedFileIn.length() == 0) {
                //Создаём временный файл из первой партии строк
                BufferedWriter out = new BufferedWriter(new FileWriter(sortedFileIn));
                for (String s :
                        list) {
                    out.write(s);
                    out.newLine();
                }
//                out.writeObject("Holy fuck");
                out.close();
            } else {
                //Сливаем с отсортированным временным файлом в другой временный файл
                BufferedReader in = new BufferedReader(new FileReader(sortedFileIn));
                BufferedWriter out = new BufferedWriter(new FileWriter(sortedFile));
                String tmp = null;
                int i = 0;
                tmp = in.readLine();

                while (i < list.size() && in.ready()) {
                    if (list.get(i).compareTo(tmp) < 0) {
                        out.write(list.get(i++) + "\r\n");
                    } else {
                        out.write(tmp + "\r\n");
                        tmp = in.readLine();
                    }
                }

                if (i >= list.size()) {
                    while (in.ready()) {
                        out.write(in.readLine() + "\r\n");
                    }
                } else {
                    for (int j = i; j < list.size(); j++) {
                        out.write(list.get(j) + "\r\n");
                    }
                }
                out.close();
                in.close();
                //меняем файлы местами
                boolean delete = sortedFileIn.delete();
                boolean rename = sortedFile.renameTo(sortedFileIn);

            }
            k += list.size();
        }
        //перемещаем временный файл в ответ
        try {
            Files.delete(result.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Files.move(sortedFileIn.toPath(), result.toPath());
        try {
            sortedFileIn.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(System.currentTimeMillis() - begin);
    }
}
