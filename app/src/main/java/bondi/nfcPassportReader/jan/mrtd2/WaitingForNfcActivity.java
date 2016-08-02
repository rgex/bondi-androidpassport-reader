package bondi.nfcPassportReader.jan.mrtd2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TextView;

import bondi.nfcPassportReader.jan.mrtd2.BitiAndroid.AbstractNfcActivity;
import bondi.nfcPassportReader.jan.mrtd2.BitiAndroid.TagProvider;
import nfcPassportReader.jan.mrtd2.R;

import java.io.Serializable;

public class WaitingForNfcActivity extends AbstractNfcActivity implements Serializable
{

    private String passportNumber;
    private String dateOfBirth;
    private String dateOfExpiration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_nfc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.passportNumber = (String)getIntent().getSerializableExtra("passportNumber");
        this.dateOfBirth = (String)getIntent().getSerializableExtra("dateOfBirth");
        this.dateOfExpiration = (String)getIntent().getSerializableExtra("dateOfExpiration");

        if(TagProvider.isTagReady()) {
            this.readPassport();
        }

    }

    public void onNewIntent(Intent intent)
    {
        ((TextView)findViewById(R.id.placeYourDeviceInstructions)).setText(getResources().getString(R.string.found_nfc_text));
        ((TextView)findViewById(R.id.placeYourDeviceInstructions)).setGravity(Gravity.CENTER_HORIZONTAL);
        super.onNewIntent(intent);

        this.readPassport();
    }

    private void readPassport()
    {
        Intent activityIntent = new Intent("bondi.nfcPassportReader.jan.mrtd2.ReadingPassportActivity");
        activityIntent.putExtra("passportNumber", this.passportNumber);
        activityIntent.putExtra("dateOfBirth", this.dateOfBirth);
        activityIntent.putExtra("dateOfExpiration", this.dateOfExpiration);
        startActivity(activityIntent);

        this.finish();
    }

}
