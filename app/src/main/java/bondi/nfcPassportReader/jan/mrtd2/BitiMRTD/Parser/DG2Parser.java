package bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Parser;

import android.graphics.Bitmap;

import java.util.Arrays;

import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Tools.Tools;

public class DG2Parser {
    private Tools tools;
    private TagParser tagParser;
    private Iso19794Parser iso19794;

    /**
     * We expect to get the full DG2 file in the constructor
     * @param dg2
     */
    public DG2Parser(byte[] dg2)
    {

        this.tools = new Tools();
        System.out.println("Length of DG2 : ");
        System.out.println(String.valueOf(dg2.length));

        TagParser tagParser = new TagParser(dg2);
        TagParser tag7F60 = tagParser.geTag("75").geTag("7F61").geTag("7F60");
        byte[] iso19794Bytes = null;

        if(tag7F60.geTag("5F2E").getBytes() != null) {
            iso19794Bytes = tag7F60.geTag("5F2E").getBytes();
        }

        if(tag7F60.geTag("7F2E").getBytes() != null) {
            iso19794Bytes = tag7F60.geTag("7F2E").getBytes();
        }

        this.iso19794 = new Iso19794Parser(iso19794Bytes);

    }

    public Bitmap getBitmap()
    {
        return this.iso19794.getBitmap();
    }
}
