package eu.janinko.andaria.uotools.diff;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import eu.janinko.andaria.ultimasdk.files.Gumps;

/**
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class GumpsDiff extends AbstractFileDiff<Gumps> {
    public GumpsDiff() {
    }

    public GumpsDiff(Path outputPath) {
        super(outputPath);
    }

    public GumpsDiff(PrintStream textOutput) {
        super(textOutput);
    }

    public GumpsDiff(Path outputPath, PrintStream textOutput) {
        super(outputPath, textOutput);
    }

    @Override
    public void diff(Gumps left, Gumps right) throws IOException {
        for (int i = 0; i < Gumps.GUMPS_COUNT; i++) {
            diffImage(left.get(i), right.get(i), i);
        }
    }
}
