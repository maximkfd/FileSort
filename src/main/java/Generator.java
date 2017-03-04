import org.apache.commons.lang.RandomStringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Denis.Repp
 */
public class Generator {

    public void generate(final String filename, final long rows) throws IOException {
        final BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename));

        for (int i = 0; i < rows; i++) {
            final String row = RandomStringUtils.randomAlphanumeric((int) (1024*1024*1024/rows));
            writer.write(row);
            writer.newLine();
        }

        writer.close();
    }

    public static void main(final String[] args) throws IOException {
        final Generator g = new Generator();
        g.generate("input.txt",500);
    }
}
