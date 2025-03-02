package eu.janinko.andaria.uotools.diff;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import eu.janinko.andaria.ultimasdk.graphics.Image;
import eu.janinko.andaria.uotools.dump.Dumper;

/**
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public abstract class AbstractFileDiff<T> implements FileDiff<T> {

    protected final Path outputPath;
    protected final PrintStream textOutput;

    public AbstractFileDiff() {
        this(null, System.out);
    }

    public AbstractFileDiff(Path outputPath) {
        this(outputPath, System.out);
    }

    public AbstractFileDiff(PrintStream textOutput) {
        this(null, textOutput);
    }

    public AbstractFileDiff(Path outputPath, PrintStream textOutput) {
        this.outputPath = outputPath;
        this.textOutput = textOutput;
    }

    protected void diffImage(Image leftMap, Image rightMap, int i) throws IOException {
        if (outputPath == null) {
            diffObjectsText(leftMap, rightMap, i);
        } else {
            diffImageFile(leftMap, rightMap, i);
        }
    }

    protected <T> void diffObjectsText(T left, T right, int i) {
        if (left == null && right == null) {
            // both empty
        } else if (left == null) {
            textOutput.printf("%d (%x): added %s\n", i, i, right);
        } else if (right == null) {
            textOutput.printf("%d (%x): removed %s\n", i, i, left);
        } else if (!left.equals(right)) {
            textOutput.printf("%d (%x): changed %s => %s\n", i, i, left, right);
        }
    }

    private void diffImageFile(Image leftMap, Image rightMap, int i) throws IOException {
        final Path imgdir = outputPath.resolve(Integer.toString(i));
        if (leftMap == null && rightMap == null) {
            // both empty
        } else if (leftMap == null) {
            Dumper.save(imgdir, "added", rightMap.getBitmap().getImage());
        } else if (rightMap == null) {
            Dumper.save(imgdir, "removed", leftMap.getBitmap().getImage());
        } else if (!leftMap.equals(rightMap)) {
            Dumper.save(imgdir, "old", leftMap.getBitmap().getImage());
            Dumper.save(imgdir, "new", rightMap.getBitmap().getImage());
        }
    }

    public <T> void diffNum(StringBuilder diff, T left, T right, ToIntFunction<T> get, String name) {
        if (get.applyAsInt(left) != get.applyAsInt(right)) {
            diff.append(' ').append(name).append(": ").append(get.applyAsInt(left)).append(" => ").append(get.applyAsInt(right));
        }
    }

    public <T> void diffObject(StringBuilder diff, T left, T right, Function<T, ?> get, String name) {
        if (!get.apply(left).equals(get.apply(right))) {
            diff.append(' ').append(name).append(": ").append(get.apply(left)).append(" => ").append(get.apply(right));
        }
    }
}
