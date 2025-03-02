package eu.janinko.andaria.uotools.diff;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import eu.janinko.andaria.ultimasdk.files.TileData;
import eu.janinko.andaria.ultimasdk.files.tiledata.ItemData;
import eu.janinko.andaria.ultimasdk.files.tiledata.LandData;
import eu.janinko.andaria.ultimasdk.files.tiledata.TileFlag;
import eu.janinko.andaria.ultimasdk.files.tiledata.TileFlags;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class TiledataDiff extends AbstractFileDiff<TileData> {
    public TiledataDiff() {
    }

    public TiledataDiff(Path outputPath) {
        super(outputPath);
    }

    public TiledataDiff(PrintStream textOutput) {
        super(textOutput);
    }

    public TiledataDiff(Path outputPath, PrintStream textOutput) {
        super(outputPath, textOutput);
    }

    @Override
    public void diff(TileData left, TileData right){
        textOutput.println("Lands:");
        List<LandData> leftLands = left.getLands();
        List<LandData> rightLands = right.getLands();
        for (int i = 0; i < leftLands.size(); i++) {
            LandData ld = leftLands.get(i);
            LandData rd = rightLands.get(i);
            if(!ld.equals(rd)){
                String change;
                if(ld.isEmpty()) change = "added";
                else if (rd.isEmpty()) change = "removed";
                else change = "changed";
                textOutput.printf("%d (0x%x): %s%s\n", i, i, change, diff(ld, rd));
            }
        }
        textOutput.println("Items:");
        List<ItemData> leftItems = left.getItems();
        List<ItemData> rightItems = right.getItems();
        for (int i = 0; i < leftItems.size(); i++) {
            ItemData ld = leftItems.get(i);
            ItemData rd = rightItems.get(i);
            if(!ld.equals(rd)){
                String change;
                if(ld.isEmpty()) change = "added";
                else if (rd.isEmpty()) change = "removed";
                else change = "changed";
                textOutput.printf("%d (0x%x): %s%s\n", i, i, change, diff(ld, rd));
            }
        }
    }

    private String diff(LandData left, LandData right){
        StringBuilder diff = new StringBuilder();
        
        diffFlags(diff, left.getFlags(), right.getFlags());
        diffNum(diff, left, right, LandData::getTextureId, "TextureId");
        diffObject(diff, left, right, LandData::getName, "Name");
        
        return diff.toString();
    }
    
    private String diff(ItemData left, ItemData right){
        StringBuilder diff = new StringBuilder();
        
        diffFlags(diff, left.getFlags(), right.getFlags());
        diffNum(diff, left, right, ItemData::getAnimation, "Animation");
        diffNum(diff, left, right, ItemData::getHeight, "Height");
        diffNum(diff, left, right, ItemData::getHue, "Hue");
        diffNum(diff, left, right, ItemData::getQuality, "Quality");
        diffNum(diff, left, right, ItemData::getQuantity, "Quantity");
        diffNum(diff, left, right, ItemData::getUnknown1, "Unknown1");
        diffNum(diff, left, right, ItemData::getUnknown2, "Unknown2");
        diffNum(diff, left, right, ItemData::getUnknown3, "Unknown3");
        diffNum(diff, left, right, ItemData::getUnknown4, "Unknown4");
        diffNum(diff, left, right, ItemData::getValue, "Value");
        diffNum(diff, left, right, ItemData::getWeight, "Weight");
        diffObject(diff, left, right, ItemData::getName, "Name");
        
        return diff.toString();
    }
    
    private void diffFlags(StringBuilder diff, TileFlags lFlags, TileFlags rFlags) {
        StringBuilder flags = new StringBuilder();
        for (TileFlag flag : TileFlag.values()) {
            if(lFlags.contains(flag)){
                if(!rFlags.contains(flag)){
                    flags.append(" -").append(flag);
                }
            }else{
                if(rFlags.contains(flag)){
                    flags.append(" +").append(flag);
                }
            }
        }
        if(flags.length() > 0){
            diff.append(" Flags:").append(flags);
        }
    }
}
