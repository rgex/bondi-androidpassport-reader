package bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Parser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Tools.Tools;
import org.jmrtd.jj2000.JJ2000Decoder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class Iso19794Parser {

    private byte[] rawData;
    private Tools tools;
    private Bitmap bitmap = null;

    public Iso19794Parser(byte[] data)
    {
        this.rawData = data;
        this.tools = new Tools();
        this.parse();
    }

    private void parse()
    {
        int cursor = 0;

        /**
         * Facial record header
         */
        cursor += 4; //skip Format Identifier
        cursor += 4; //skip Version Number

        cursor += 4; //skip Length of Record
        cursor += 2; //skip number of facial images

        /**
         * Facial record data
         */
        cursor += 4; //skip Facial Record Data Length

        // @TODO remove
        System.out.println("Facial record header:");
        System.out.println(this.tools.bytesToString(Arrays.copyOfRange(this.rawData, 0, 1024)));
        // end todo remove

        int numberOfFeaturePoints = this.tools.getIntFrom16bits(Arrays.copyOfRange(this.rawData, cursor, cursor + 2));

        System.out.println("Number of feature points: ".concat(String.valueOf(numberOfFeaturePoints)));
        cursor += 2;

        cursor += 1; //skip Gender
        cursor += 1; //skip Eye Colour
        cursor += 1; //skip Hair Colour
        cursor += 3; //skip Property Mask
        cursor += 2; //skip Expression
        cursor += 3; //skip Pose Angle
        cursor += 3; //skip Pose Angle Uncertainty

        /**
         * Feature point(s)
         */
        for(int i = 0; i < numberOfFeaturePoints; i++) {
            cursor += 8; //skip Feature Point
        }

        /**
         * Image Information
         */
        cursor += 1; //skip Face Image Type
        cursor += 1; //skip Image Data Type
        cursor += 2; //skip Width
        cursor += 2; //skip Height
        cursor += 1; //skip Image Colour Space
        cursor += 1; //skip Source Type
        cursor += 2; //skip Device Type
        cursor += 2; //skip Quality

        /**
         * Image Data
         */
        try {
            //first try with jpg
            this.bitmap = BitmapFactory.decodeByteArray(this.rawData, cursor, this.rawData.length);
        }
        catch(Exception e) {
            // Second try with jp2000
            System.out.println(e.getLocalizedMessage());

            try {

                org.jmrtd.jj2000.Bitmap jj2000image = JJ2000Decoder.decode(
                        new ByteArrayInputStream(
                                Arrays.copyOfRange(this.rawData, cursor, this.rawData.length)
                        )
                );

                int[] pixels = jj2000image.getPixels();

                this.bitmap = Bitmap.createBitmap(
                        pixels,
                        0,
                        jj2000image.getWidth(),
                        jj2000image.getWidth(),
                        jj2000image.getHeight(),
                        Bitmap.Config.ARGB_8888
                );

            } catch (Exception e2) {
                System.out.println(e2.getLocalizedMessage());
            }
        }
    }

    public Bitmap getBitmap()
    {
        return this.bitmap;
    }
}
