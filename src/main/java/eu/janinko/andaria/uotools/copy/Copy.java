package eu.janinko.andaria.uotools.copy;

import eu.janinko.andaria.ultimasdk.UOFiles;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

import eu.janinko.andaria.ultimasdk.files.Arts;
import eu.janinko.andaria.ultimasdk.files.Hues;
import eu.janinko.andaria.ultimasdk.files.arts.Art;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Copy {

    public static void main(String[] args) {
        if (args.length < 4) {
            printHelp();
            System.exit(1);
        }
        Path from = Paths.get(args[1]);
        Path to = Paths.get(args[2]);
        Path out = Paths.get(args[3]);
        try {
            switch (args[0]) {
                case "art": copyArt(from, to, out);
                    break;
                case "hue": copyHue(from, to, out);
                    break;
                default: printHelp();
                    System.exit(1);
                    break;
            }
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getLocalizedMessage());
            System.exit(2);
        }

    }

    private static void printHelp() {
        System.out.println("Usage: Copy TYPE FROM TO OUTPUT");
    }

    private static void copyArt(Path from, Path to, Path out) throws IOException {
        Arts fromArt = UOFiles.loadArts(from.getParent().resolve("artidx.mul"), from);
        Arts toArt = UOFiles.loadArts(to.getParent().resolve("artidx.mul"), to);
        Files.createDirectories(out);
        Path outArtMul = out.resolve("art.mul");
        Path outArtIdx = out.resolve("artidx.mul");

        readCommands(fromArt, toArt);
        System.out.println("Saving");
        toArt.save(Files.newOutputStream(outArtIdx), Files.newOutputStream(outArtMul));
    }

    private static void copyHue(Path from, Path to, Path out) throws IOException {
        Hues fromHue = UOFiles.loadHues(from);
        Hues toHue = UOFiles.loadHues(to);
        Files.createDirectories(out);
        Path outHueMul = out.resolve("hues.mul");

        readCommands((f, t) -> toHue.setHue(t, fromHue.get(f)));
        System.out.println("Saving");
        toHue.save(Files.newOutputStream(outHueMul));
    }
    
    private static <T> void readCommands(BiConsumer<Integer, Integer> copy) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        while (line != null) {
            try {
                String s[] = line.split(" ");
                int fid = Integer.parseInt(s[0]);
                int tid;
                if (s.length == 1) {
                    tid = fid;
                } else {
                    tid = Integer.parseInt(s[1]);
                }

                copy.accept(fid, tid);
            } catch (NumberFormatException ex) {
                System.err.println("'" + line + "' is not one or two numbers");
            }
            line = br.readLine();
        }
    }

    private static <T> void readCommands(Arts from, Arts to) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        while (line != null) {
            try {
                String s[] = line.split(" ");
                int fid = Integer.parseInt(s[0]);
                int tid;
                if (s.length == 1) {
                    tid = fid;
                } else {
                    tid = Integer.parseInt(s[1]);
                }

                Art art = from.getStatic(fid);
                to.setStatic(tid, art);
            } catch (NumberFormatException ex) {
                System.err.println("'" + line + "' is not one or two numbers");
            }
            line = br.readLine();
        }
    }
}
