package bondi.nfcPassportReader.jan.mrtd2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;

import bondi.nfcPassportReader.jan.mrtd2.BitiAndroid.AbstractNfcActivity;
import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Constants.MrtdConstants;
import bondi.nfcPassportReader.jan.mrtd2.BitiAndroid.TagProvider;
import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Parser.DG1Parser;
import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Parser.EFSODParser;
import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Reader.AbstractReader;
import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Reader.BacInfo;
import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Reader.DESedeReader;
import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Reader.ProgressListenerInterface;
import bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Tools.Tools;
import nfcPassportReader.jan.mrtd2.R;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class ReadingPassportActivity extends AbstractNfcActivity implements Serializable {

    private String passportNumber;
    private String dateOfBirth;
    private String dateOfExpiration;

    private byte[] dg1;
    private byte[] dg2;
    private byte[] sod;

    private AsyncReader asyncReader;
    private boolean isActivityRunning;

    private ProgressBar mrtdProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_passport);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.isActivityRunning = true;

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {

        }

        this.passportNumber = (String) getIntent().getSerializableExtra("passportNumber");
        this.dateOfBirth = (String) getIntent().getSerializableExtra("dateOfBirth");
        this.dateOfExpiration = (String) getIntent().getSerializableExtra("dateOfExpiration");

        this.mrtdProgressBar = (ProgressBar) this.findViewById(R.id.mrtdProgressBar);

        this.readNfc();
    }

    protected void readNfc() {
        this.asyncReader = new AsyncReader(
                this,
                this.passportNumber,
                this.dateOfBirth,
                this.dateOfExpiration
        );
        asyncReader.execute();
    }

    public void showResult() {
        if (this.dg1 != null && this.dg2 != null) {
            Intent intent = new Intent("bondi.nfcPassportReader.jan.mrtd2.ResultDisplayActivity");
            intent.putExtra("dg1", this.dg1);
            intent.putExtra("dg2", this.dg2);
            intent.putExtra("sod", this.sod);
            this.setMrtdProgressBarPercentage(96);
            startActivity(intent);
            this.setMrtdProgressBarPercentage(100);
        } else {
            System.out.println("dg1 or/and dg2 is/are null");
        }
    }

    public void showError(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(ReadingPassportActivity.this.isActivityRunning) {
                    new AlertDialog.Builder(ReadingPassportActivity.this)
                            .setTitle(getResources().getString(R.string.error_error))
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ReadingPassportActivity.this.finish();
                                }
                            }).create().show();
                }
            }
        });

    }

    public void setMrtdProgressBarPercentage(int progress) {
        this.mrtdProgressBar.setProgress(progress);
    }

    public void setDg1(byte[] dg1) {
        this.dg1 = dg1;
    }

    public void setDg2(byte[] dg2) {
        this.dg2 = dg2;
    }

    public void setSOD(byte[] sod) {
        this.sod = sod;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.isActivityRunning = false;
                this.asyncReader.cancel();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.isFinishing()) {
            this.isActivityRunning = false;
            this.asyncReader.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.isActivityRunning = false;
        this.asyncReader.cancel();
    }

    private class AsyncReader extends AsyncTask<Void, Integer, Boolean> implements ProgressListenerInterface {

        private boolean success = false;
        private WeakReference<ReadingPassportActivity> readingPassportActivity;
        private String passportNumber;
        private String dateOfBirth;
        private String dateOfExpiration;
        private boolean isCanceled = false;
        private int currentStep = 0;

        public AsyncReader(
                ReadingPassportActivity readingPassportActivity,
                String passportNumber,
                String dateOfBirth,
                String dateOfExpiration
        ) {

            this.passportNumber = passportNumber;
            this.dateOfBirth = dateOfBirth;
            this.dateOfExpiration = dateOfExpiration;
            this.isCanceled = false;

            this.link(readingPassportActivity);
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {


            try {
                if (TagProvider.getTag() != null) {

                    System.out.println("GOT TAG");

                    BacInfo bacInfo = new BacInfo();

                    bacInfo.setPassportNbr(this.passportNumber);
                    bacInfo.setDateOfBirth(this.dateOfBirth);
                    bacInfo.setDateOfExpiry(this.dateOfExpiration);

                    AbstractReader mrtd = new DESedeReader();
                    mrtd.setBacInfo(bacInfo);


                    if (mrtd.initSession()) {

                        mrtd.setProgressListener(new WeakReference<Object>(this));

                        this.readingPassportActivity.get().setMrtdProgressBarPercentage(5);

                        this.currentStep = 1;
                        byte[] dg1 = mrtd.readFile(MrtdConstants.FID_DG1);
                        if (dg1 == null) {
                            this.readingPassportActivity.get().showError(getResources().getString(R.string.error_dg1_is_null));
                        }

                        this.readingPassportActivity.get().setMrtdProgressBarPercentage(10);

                        this.readingPassportActivity.get().setDg1(dg1);
                        DG1Parser dg1Parser = new DG1Parser(dg1);
                        Tools tools = new Tools();

                        /*
                        System.out.println("Document Code : ".concat(dg1Parser.getDocumentCode()));
                        System.out.println("Issuing state : ".concat(dg1Parser.getIssuingStateCode()));
                        System.out.println("Document Number : ".concat(dg1Parser.getDocumentNumber()));
                        System.out.println("Gender : ".concat(dg1Parser.getGender()));
                        System.out.println("Given names : ".concat(dg1Parser.getGivenNames()));
                        System.out.println("Surname : ".concat(dg1Parser.getSurname()));
                        System.out.println("Nationality : ".concat(dg1Parser.getNationalityCode()));
                        System.out.println("Date of birth : ".concat(dg1Parser.getDateOfBirth()));
                        System.out.println("Date of Expiry : ".concat(dg1Parser.getDateOfExpiry()));
                        System.out.println("File content : ".concat(tools.bytesToString(dg1)));*/

                        this.currentStep = 2;
                        byte[] dg2 = mrtd.readFile(MrtdConstants.FID_DG2);
                        if (dg2 == null) {
                            this.readingPassportActivity.get().showError(getResources().getString(R.string.error_dg2_is_null));
                        }
                        this.readingPassportActivity.get().setDg2(dg2);

                        this.currentStep = 3;
                        byte[] efsod = mrtd.readFile(MrtdConstants.FID_EF_SOD);
                        this.readingPassportActivity.get().setSOD(efsod);

                        if (dg1 != null && dg2 != null) {
                            this.success = true;
                        }

                    } else {
                        System.out.println("Failed to init session");
                        this.readingPassportActivity.get().showError(getResources().getString(R.string.error_mutual_authentication_failed));
                        TagProvider.closeTag();
                        return false;
                    }
                } else {
                    System.out.println("Couldn't get Tag from intent");
                    this.readingPassportActivity.get().showError(getResources().getString(R.string.error_lost_connexion));
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Exception");
                this.readingPassportActivity.get().showError(getResources().getString(R.string.error_nfc_exception));
                return false;
            }

            if(!this.success) {
                this.readingPassportActivity.get().showError(getResources().getString(R.string.error_nfc_exception));
            }

            this.readingPassportActivity.get().setMrtdProgressBarPercentage(95);

            return true;
        }

        public void link(ReadingPassportActivity readingPassportActivity) {
            this.readingPassportActivity = new WeakReference<ReadingPassportActivity>(readingPassportActivity);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (this.success) {
                this.readingPassportActivity.get().showResult();
                this.readingPassportActivity.get().finish();
            }
        }

        public void updateProgress(int progress) {
            switch (currentStep) {
                case 1:
                    this.readingPassportActivity.get().setMrtdProgressBarPercentage(Math.round(progress * 10 / 100));
                    break;
                case 2:
                    this.readingPassportActivity.get().setMrtdProgressBarPercentage(Math.round(progress * 75 / 100) + 10);
                    break;
                case 3:
                    this.readingPassportActivity.get().setMrtdProgressBarPercentage(Math.round(progress * 10 / 100) + 85);
                    break;
            }
        }

        public void cancel() {
            this.isCanceled = true;
        }

        @Override
        protected void onCancelled() {
            this.isCanceled = true;
            super.onCancelled();
        }

        @Override
        public boolean isCanceled() {
            return this.isCanceled;
        }
    }

}
