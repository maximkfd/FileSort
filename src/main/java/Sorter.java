import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Denis.Repp
 */
public class Sorter {

    public void sort(final String filename) throws IOException {
        final long begin = System.currentTimeMillis();

        final BufferedReader inFileReader = new BufferedReader(new FileReader(filename));
        final List<String> rowsList = new ArrayList<>();

        final File tempSortedFile = File.createTempFile("to_read", null);

        final File result = new File("res.txt");
        while (inFileReader.ready()) {
            long readedBufferSize = 0;
            //Читаем кусок из 1gb файла
            final int buffer32mb = 32 * 1024 * 1024;
            while (inFileReader.ready() && readedBufferSize < buffer32mb) {
                final String tmp = inFileReader.readLine();
                readedBufferSize += tmp.getBytes().length;
                rowsList.add(tmp);
            }
            rowsList.sort(null);
            System.out.println("read " + readedBufferSize + " bytes");
            if (tempSortedFile.length() == 0) {
                BufferedWriter out = new BufferedWriter(new FileWriter(tempSortedFile));
                for (String s : rowsList) {
                    out.write(s);
                    out.newLine();
                }
                out.close();
            } else {
                mergeListToFile(rowsList, tempSortedFile);
            }
            rowsList.clear();
        }
        //перемещаем временный файл в ответ
        if (result.exists()) {
            Files.delete(result.toPath());
        }

        Files.move(tempSortedFile.toPath(), result.toPath());
        tempSortedFile.delete();

        System.out.printf("Algorithm took ms: %d%n", System.currentTimeMillis() - begin);
    }

    /**
     * Сливаем с отсортированным временным файлом в другой временный файл
     */
    private void mergeListToFile(final List<String> rowsList, final File sortedFileIn) throws IOException {
        final File sortedFile = File.createTempFile("to_write", null);

        final BufferedReader in = new BufferedReader(new FileReader(sortedFileIn));
        final BufferedWriter out = new BufferedWriter(new FileWriter(sortedFile));

        final Iterator<String> iterator = rowsList.iterator();

        String tmp = in.readLine();
        while (iterator.hasNext() && in.ready()) {
            final String currentRow = iterator.next();
            if (currentRow.compareTo(tmp) < 0) {
                out.write(currentRow);
                out.newLine();
            } else {
                out.write(tmp);
                out.newLine();
                tmp = in.readLine();
            }
        }

        while (iterator.hasNext()) {
            final String currentRow = iterator.next();
            out.write(currentRow);
            out.newLine();
        }

        while (in.ready()) {
            out.write(in.readLine());
            out.newLine();
        }

//        while (i < rowsList.size() && in.ready()) {
//            if (rowsList.get(i).compareTo(tmp) < 0) {
//                out.write(rowsList.get(i++));
//                out.newLine();
//            } else {
//                out.write(tmp);
//                out.newLine();
//                tmp = in.readLine();
//            }
//        }

//        if (i >= rowsList.size()) {
//            while (in.ready()) {
//                out.write(in.readLine());
//                out.newLine();
//            }
//        } else {
//            for (int j = i; j < rowsList.size(); j++) {
//                out.write(rowsList.get(j));
//                out.newLine();
//            }
//        }
        out.close();
        in.close();
        //меняем файлы местами
        sortedFileIn.delete();
        sortedFile.renameTo(sortedFileIn);


    }
}
