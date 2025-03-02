package eu.janinko.andaria.uotools.diff;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;

import eu.janinko.andaria.ultimasdk.files.Anims;
import eu.janinko.andaria.ultimasdk.files.anims.Anim;
import eu.janinko.andaria.ultimasdk.files.anims.Body;
import eu.janinko.andaria.ultimasdk.files.anims.Body.Action;
import eu.janinko.andaria.ultimasdk.files.anims.Frame;
import eu.janinko.andaria.uotools.dump.Dumper;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class AnimDiff extends AbstractFileDiff<Anims> {
    public AnimDiff() {
    }

    public AnimDiff(Path outputPath) {
        super(outputPath);
    }

    public AnimDiff(PrintStream textOutput) {
        super(textOutput);
    }

    public AnimDiff(Path outputPath, PrintStream textOutput) {
        super(outputPath, textOutput);
    }

    @Override
    public void diff(Anims left, Anims right) throws IOException {
        int bodies = left.numberOfBodies();
        if(bodies != right.numberOfBodies()){
            textOutput.println("Different number of bodies " + bodies + "=>" + right.numberOfBodies());
            if(right.numberOfBodies() < bodies){
                bodies = right.numberOfBodies();
            }
        }
        
        for (int i = 0; i < bodies; i++) {
            Body l = left.getBody(i);
            Body r = right.getBody(i);
            if (l == null && r == null) {
                // both empty
            } else if (l == null) {
                textOutput.printf("%d (%x): added %s\n", i, i, r);
            } else if (r == null) {
                textOutput.printf("%d (%x): removed %s\n", i, i, l);
            } else if (!l.equals(r)) {
                textOutput.printf("%d (%x): changed %s => %s\n", i, i, l, r);
                diff(i, l, r);
            }
        }
    }

    private void diff(int body, Body l, Body r) throws IOException {
        for (Action a : l.getActions()) {
            for(Body.Direction d : Body.Direction.values()){
                Anim lAnim = l.getAnim(a, d);
                Anim rAnim = r.getAnim(a, d);

                if(lAnim == null && rAnim == null){
                    // OK
                }else if (lAnim == null){
                    textOutput.println("  left is missing action " + a + " " + d);
                }else if (rAnim == null){
                    textOutput.println("  right is missing action " + a + " " + d);
                }else if (!lAnim.equals(rAnim)) {
                    textOutput.println("  changed action " + a + " " + d);
                    if (outputPath != null) {
                        Path leftPath = outputPath.resolve("left/" + body);
                        Path rihtPath = outputPath.resolve("right/" + body);
                        textOutput.println("   saving " + lAnim.frameCount() + " frames");
                        for (int i = 0; i < lAnim.frameCount(); i++) {
                            Frame f = lAnim.getFrame(i);
                            if (f == null) {
                                textOutput.println("   left frame " + i + " missing");
                            } else {
                                Dumper.save(leftPath, body + "-" + a + "_" + d + "-" + i, f.getImage());
                            }
                        }

                        textOutput.println("   saving " + rAnim.frameCount() + " frames");
                        for (int i = 0; i < rAnim.frameCount(); i++) {
                            Frame f = rAnim.getFrame(i);
                            if (f == null) {
                                textOutput.println("   right frame " + i + " missing");
                            } else {
                                Dumper.save(rihtPath, body + "-" + a + "_" + d + "-" + i, f.getImage());
                            }
                        }
                    }
                }
            }
        }
    }
}
