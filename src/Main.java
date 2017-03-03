import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {

        Runtime runtime = Runtime.getRuntime();
        long begin = System.currentTimeMillis();

        System.out.println(Runtime.getRuntime().totalMemory() + ":  " + Runtime.getRuntime().freeMemory());
        BufferedReader reader = new BufferedReader(new FileReader("file2"));
        //прочитать несколько строк
        ArrayList<String> list;

        File sortedFileIn = File.createTempFile("to read", null);
        File sortedFile = File.createTempFile("to write", null);
        File result = new File("res");
        int k = 0;
        while (reader.ready()) {
            System.gc();

            long size = 0;
            //Читаем кусок из 1gb файла
            list = new ArrayList<>();
            while (reader.ready() &&  runtime.freeMemory() > runtime.totalMemory() - runtime.freeMemory()) {
                list.add(reader.readLine());
            }
            list.sort(null);

            if (sortedFileIn.length() == 0) {
                //Создаём временный файл из первой партии строк
                BufferedWriter out = new BufferedWriter(new FileWriter(sortedFileIn));
                for (String s :
                        list) {
                    out.write(s + "\r\n");
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
