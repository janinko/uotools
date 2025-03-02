package eu.janinko.andaria.uotools.dump;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.OptionalInt;

import eu.janinko.andaria.ultimasdk.files.Arts;
import eu.janinko.andaria.ultimasdk.files.Hues;
import eu.janinko.andaria.ultimasdk.files.arts.Art;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.ultimasdk.graphics.Bitmap;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class ImageDumper {


    public static void main(String[] args) throws IOException {

        Path filepath = Paths.get("/home/janinko/Ultima/cuo/files");
        Path outpath = Paths.get("/tmp/Arty/Andar");
        Path barvy = Paths.get("/tmp/barvy");
        Dumper d = new Dumper(filepath, outpath);

        d.doAnimsStay();
        //d.generateArtHues(5359);
        //int hues[] = {2971, 1163, 2251, 2775, 2062, 1364, 1363, 2934, 1952, 2094, 1331, 1324, 1284, 1282, 1195, 2066, 1160, 1173, 1425, 1424, 2613, 1285, 2020, 1445, 1196, 1944, 2917, 2919, 1281, 2718, 1864, 1940, 1502, 1518, 1504, 2061, 2722, 1217, 2261, 1945, 2634, 2753, 2097, 2065, 2636, 2246, 2250, 2809, 1246, 2064};
/*
        for(int i=0; i<hues.length; i++){
            d.doAnimWalk(2, 203, i, Optional.of(hues[i]));
        }*/

        //for(int i=1; i<= 3000; i++){
            //d.generateArt(3921, i, ""+i);
//            d.doAnimStay(2, 203, OptionalInt.of(i));
          // d.doAnimStay(1, 51, OptionalInt.of(i));
        //}
        //d.generateBodyAnim(400);
        //d.generateUnifont(1);
        //d.generateArts();
        //d.generateArtHues(5167);
        //generateColoredArt(files.getArts(), files.getHues(), 0xef2);
    }

    public static void generateColoredArt(Arts arts, Hues hues, int artId) throws IOException {
        Path outpath = Paths.get("/tmp/uorender/huedArt/" + artId);
        Art art = arts.getStatic(artId);
        for (int i = 1; i <= 3000; i++) {
            Hue hue = hues.get(i);
            Bitmap b = art.getBitmap();
            b.hue(hue, false);
            BufferedImage image = b.getImage();
            if (image != null) {
                float[] hsb = hue.averageHsb();
                int h = (int) (hsb[0] * 1000);
                int s = (int) (hsb[1] * 1000);
                int v = (int) (hsb[2] * 1000);
                Dumper.save(outpath, h + "x" + s + "x" + v + "_" + i, image);
            }
        }
    }

    /*
	public static void doAnim(Anims anims, String listOfAnims) throws IOException{
        Path dir = dir("anims/walk");
        try(BufferedReader r = new BufferedReader(new FileReader(listOfAnims))){
            for(String line = r.readLine(); line != null; line = r.readLine()){
                String[] split = line.split("\t");
                int i = Integer.parseInt(split[0]);
                if(i>1000) continue;
                Path outdir = dir.resolve(Integer.toString(i));
                        Files.createDirectory(outdir);
                Anim anim = anims.getAnim1(i, Anims.WALK, Anims.SOUTH);
                if(anim == null) continue;
                for(int j=0; j<anim.frameCount(); j++){
                    Frame f = anim.getFrame(j);
                    if(f == null) continue;
                    BufferedImage image = f.getCenteredImage();
                    if (image != null) {
                        Dumper.save(outdir, j, image);
                    }
                }
            }
		}
	}
*/


}
