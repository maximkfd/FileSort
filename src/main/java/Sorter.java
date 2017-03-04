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
        final Runtime runtime = Runtime.getRuntime();
        final long begin = System.currentTimeMillis();

        System.out.println(Runtime.getRuntime().totalMemory() + ":  " + Runtime.getRuntime().freeMemory());
        final BufferedReader reader = new BufferedReader(new FileReader(filename));
        //прочитать несколько строк
        ArrayList<String> list;

        final File sortedFileIn = File.createTempFile("to read", null);
        final File sortedFile = File.createTempFile("to write", null);
        final File result = new File("res");
        // fix 1
        if (!result.exists()) {
            result.createNewFile();
        }
        // fix1 end
        int k = 0;
        while (reader.ready()) {
            System.gc();

            final long size = 0;
            //Читаем кусок из 1gb файла
            list = new ArrayList<>();
            while (reader.ready() &&  runtime.freeMemory() > runtime.totalMemory() - runtime.freeMemory()) {
                list.add(reader.readLine());
            }
            list.sort(null);

            if (sortedFileIn.length() == 0) {
                //Создаём временный файл из первой партии строк
                final BufferedWriter out = new BufferedWriter(new FileWriter(sortedFileIn));
                for (final String s :
                        list) {
                    out.write(s + "\r\n");
                }
//                out.writeObject("Holy fuck");
                out.close();
            } else {
                //Сливаем с отсортированным временным файлом в другой временный файл
                final BufferedReader in = new BufferedReader(new FileReader(sortedFileIn));
                final BufferedWriter out = new BufferedWriter(new FileWriter(sortedFile));
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
                final boolean delete = sortedFileIn.delete();
                final boolean rename = sortedFile.renameTo(sortedFileIn);

            }
            k += list.size();
        }
        //перемещаем временный файл в ответ
        try {
            Files.delete(result.toPath());
        } catch (final IOException e) {
            e.printStackTrace();
        }
        Files.move(sortedFileIn.toPath(), result.toPath());
        try {
            sortedFileIn.delete();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.println(System.currentTimeMillis() - begin);
    }
}
