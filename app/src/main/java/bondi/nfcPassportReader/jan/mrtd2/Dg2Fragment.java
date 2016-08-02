package bondi.nfcPassportReader.jan.mrtd2;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import nfcPassportReader.jan.mrtd2.R;

public class Dg2Fragment extends Fragment
{

    private ImageView faceImageView;
    private Bitmap faceImageBitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dg2_fragment, container, false);
        this.faceImageView = (ImageView)view.findViewById(R.id.faceImageView);
        this.displayImage();
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.displayImage();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.displayImage();
    }

    public void displayImage()
    {
        System.out.println("Try to display DG2");
        if(this.faceImageView != null && this.faceImageBitmap != null) {
            System.out.println("Will display DG2");
            this.faceImageView.setImageBitmap(this.faceImageBitmap);
        }
    }

    public void setBitmap(Bitmap image)
    {
        this.faceImageBitmap = image;
        this.displayImage();
    }
}
