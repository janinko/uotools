package eu.janinko.andaria.uotools.diff;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import eu.janinko.andaria.ultimasdk.files.CliLocs;
import eu.janinko.andaria.ultimasdk.files.cliloc.CliLoc;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class CliLocsDiff extends AbstractFileDiff<CliLocs> {
    public CliLocsDiff() {
    }

    public CliLocsDiff(Path outputPath) {
        super(outputPath);
    }

    public CliLocsDiff(PrintStream textOutput) {
        super(textOutput);
    }

    public CliLocsDiff(Path outputPath, PrintStream textOutput) {
        super(outputPath, textOutput);
    }

    @Override
    public void diff(CliLocs left, CliLocs right) {

        Map<Integer, CliLoc> leftEntries = left.getEntries();
        Map<Integer, CliLoc> rightEntries = right.getEntries();

        Set<Integer> sortedLeft = new TreeSet<>(leftEntries.keySet());
        Set<Integer> unusedRight = new TreeSet<>(rightEntries.keySet());

        Set<Integer> missingRight = new TreeSet<>();
        for (Integer i : sortedLeft) {
            CliLoc l = leftEntries.get(i);
            CliLoc r = rightEntries.get(i);
            if (r == null) {
                missingRight.add(i);
            } else {
                unusedRight.remove(i);
                if(!l.equals(r)){
                    textOutput.println(i+": "+ diff(l, r));
                }
            }
        }

        if (!unusedRight.isEmpty()) {
            String missLeft = unusedRight.stream()
                    .map(x -> x.toString())
                    .collect(Collectors.joining(", "));
            textOutput.println("Missing in left: " + missLeft);
        }
        if (!missingRight.isEmpty()) {
            String missRight = missingRight.stream()
                    .map(x -> x.toString())
                    .collect(Collectors.joining(", "));
            textOutput.println("Missing in right: " + missRight);
        }

    }

    private String diff(CliLoc left, CliLoc right) {
        StringBuilder diff = new StringBuilder();

        diffNum(diff, left, right, CliLoc::getFlag, "Flag");
        diffObject(diff, left, right, CliLoc::getText, "Text");

        return diff.toString();
    }
}
