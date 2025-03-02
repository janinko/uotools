package eu.janinko.andaria.uotools.diff;

import eu.janinko.andaria.ultimasdk.files.Gumps;
import eu.janinko.andaria.ultimasdk.files.Sounds;
import eu.janinko.andaria.ultimasdk.files.sounds.SoundSample;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

public class SoundsDiff extends AbstractFileDiff<Sounds>{
    public SoundsDiff() {
    }

    public SoundsDiff(Path outputPath) {
        super(outputPath);
    }

    public SoundsDiff(PrintStream textOutput) {
        super(textOutput);
    }

    public SoundsDiff(Path outputPath, PrintStream textOutput) {
        super(outputPath, textOutput);
    }

    @Override
    public void diff(Sounds left, Sounds right) throws IOException {
        for (int i = 0; i < Gumps.GUMPS_COUNT; i++) {
            SoundSample leftSound = left.get(i);
            SoundSample rightSound = right.get(i);

            diffObjectsText(leftSound, rightSound, i);
        }
    }
}