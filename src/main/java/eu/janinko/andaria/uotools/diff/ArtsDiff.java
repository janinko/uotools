package eu.janinko.andaria.uotools.diff;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import eu.janinko.andaria.ultimasdk.files.Arts;

/**
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class ArtsDiff extends AbstractFileDiff<Arts> {
    public ArtsDiff() {
    }

    public ArtsDiff(Path outputPath) {
        super(outputPath);
    }

    public ArtsDiff(PrintStream textOutput) {
        super(textOutput);
    }

    public ArtsDiff(Path outputPath, PrintStream textOutput) {
        super(outputPath, textOutput);
    }

    @Override
    public void diff(Arts left, Arts right) throws IOException {
        int min = left.count();
        if(right.count() < min){
            min = right.count();
        }

        for (int i = 0; i < min; i++) {
            diffImage(left.get(i), right.get(i), i);
        }
        if(left.count() != right.count()){
            if(left.count() < right.count()){
                for(int i=min; i < right.count(); i++){
                    textOutput.printf("%d (%x): added %s\n", i, i, right);
                }
            }else {
                for(int i=min; i < right.count(); i++){
                    textOutput.printf("%d (%x): removed %s\n", i, i, left);
                }
            }
        }
    }
}
