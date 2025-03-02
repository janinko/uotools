package eu.janinko.andaria.uotools.diff;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;

import eu.janinko.andaria.ultimasdk.files.Hues;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.ultimasdk.graphics.Color;

/**
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class HuesDiff extends AbstractFileDiff<Hues> {
    public HuesDiff() {
    }

    public HuesDiff(Path outputPath) {
        super(outputPath);
    }

    public HuesDiff(PrintStream textOutput) {
        super(textOutput);
    }

    public HuesDiff(Path outputPath, PrintStream textOutput) {
        super(outputPath, textOutput);
    }

    @Override
    public void diff(Hues hues1, Hues hues2) {
        for (int i = 1; i <= 3000; i++) {
            Hue h1 = hues1.get(i);
            Hue h2 = hues2.get(i);
            if (!h1.getName().equals(h2.getName()) || !Arrays.equals(h1.getColors(), h2.getColors())) {
                textOutput.print(i + ": ");
                if (!h1.getName().equals(h2.getName())) {
                    textOutput.print("name: '" + h1.getName() + "' => '" + h2.getName() + "' ");
                }
                for (int j = 0; j < 32; j++) {
                    if (!h1.getColor(j).equals(h2.getColor(j))) {
                        textOutput.print("c" + j + ": " + colorToString(h1.getColor(j)) + " => " + colorToString(h2.getColor(j)) + " ");
                    }
                }
                textOutput.println();
            }
        }
    }

    private String colorToString(Color c) {
        return "(" + c.get5Red() + "," + c.get5Green() + "," + c.get5Blue() + "," + (c.isAlpha() ? "A" : " ") + ")";
    }

}
