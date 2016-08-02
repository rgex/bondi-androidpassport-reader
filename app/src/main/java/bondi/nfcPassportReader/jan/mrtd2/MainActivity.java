package bondi.nfcPassportReader.jan.mrtd2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import bondi.nfcPassportReader.jan.mrtd2.BitiAndroid.AbstractNfcActivity;
import nfcPassportReader.jan.mrtd2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AbstractNfcActivity implements DatePickerDialog.OnDateSetListener
{

    public int selectedDateField;
    public String dateOfBirth = "000000";
    public int[] dateOfBirthIntArray = {15,6,1980};
    public String dateOfExpiration = "000000";
    public int[] dateOfExpirationIntArray = {15,6,2020};

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((EditText)findViewById(R.id.DateOfBirth)).setText("");
        ((EditText)findViewById(R.id.DateOfExpiration)).setText("");

        ((EditText)findViewById(R.id.PassportNbr)).setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        findViewById(R.id.MainLayout).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        findViewById(R.id.PassportNbr).clearFocus();
                        findViewById(R.id.DateOfBirth).clearFocus();
                        findViewById(R.id.DateOfExpiration).clearFocus();
                    }
                }
        );

        ((EditText)findViewById(R.id.DateOfBirth)).setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            MainActivity.this.selectedDateField = 1;
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            MainActivity.this.displayDatePickerDialog();
                        }
                    }
                }
        );

        ((EditText)findViewById(R.id.DateOfExpiration)).setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            MainActivity.this.selectedDateField = 2;
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            MainActivity.this.displayDatePickerDialog();
                        }
                    }
                }
        );

        findViewById(R.id.ReadNfcBtn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {

                            Intent intent = new Intent("bondi.nfcPassportReader.jan.mrtd2.WaitingForNfcActivity");

                            String passportNumber = ((EditText) findViewById(R.id.PassportNbr)).getText().toString();

                            intent.putExtra("passportNumber", passportNumber);
                            intent.putExtra("dateOfBirth", MainActivity.this.dateOfBirth);
                            intent.putExtra("dateOfExpiration", MainActivity.this.dateOfExpiration);

                            startActivity(intent);
                        }
                        catch(Exception e) {
                            //@TODO
                        }

                    }
                }
        );

    }

    @Override
    public void onResume() {
        super.onResume();

        // if values are not the default values (HACK)
        if(!(this.dateOfBirthIntArray[0] == 15 && this.dateOfBirthIntArray[1] ==  6 && this.dateOfBirthIntArray[2] == 1980)) {
            this.setDateToTextView("dob", this.dateOfBirthIntArray[2], this.dateOfBirthIntArray[1], this.dateOfBirthIntArray[0]);
        }
        if(!(this.dateOfExpirationIntArray[0] == 15 && this.dateOfExpirationIntArray[1] == 6 && this.dateOfExpirationIntArray[2] == 2020)) {
            this.setDateToTextView("doe", this.dateOfExpirationIntArray[2], this.dateOfExpirationIntArray[1], this.dateOfExpirationIntArray[0]);
        }
    }

    public void displayDatePickerDialog()
    {

        findViewById(R.id.DateOfBirth).clearFocus();
        findViewById(R.id.DateOfExpiration).clearFocus();
        findViewById(R.id.PassportNbr).clearFocus();

        Calendar calendar = Calendar.getInstance();

        //date of expiration
        int calendarYear = this.dateOfExpirationIntArray[2];
        int calendarMonth = this.dateOfExpirationIntArray[1];
        int calendarDay = this.dateOfExpirationIntArray[0];

        if(this.selectedDateField == 1) { // date of birth
            calendarYear = this.dateOfBirthIntArray[2];
            calendarMonth = this.dateOfBirthIntArray[1];
            calendarDay = this.dateOfBirthIntArray[0];
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                MainActivity.this,
                calendarYear,
                calendarMonth,
                calendarDay
        );

        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setSpinnersShown(true);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        if (this.selectedDateField == 1) {
            this.setDateToTextView("dob", year, monthOfYear, dayOfMonth);
        }
        if (this.selectedDateField == 2) {
            this.setDateToTextView("doe", year, monthOfYear, dayOfMonth);
        }
    }

    public void setDateToTextView(String fieldName, int year, int monthOfYear, int dayOfMonth)
    {
        try {
            monthOfYear += 1;

            String displayDate = String.valueOf(dayOfMonth)
                    .concat("/")
                    .concat(String.valueOf(monthOfYear))
                    .concat("/")
                    .concat(String.valueOf(year));

            Date selectedDate = (new SimpleDateFormat("dd/MM/yyyy")).parse(displayDate);
            String passportDate = (new SimpleDateFormat("yyMMdd")).format(selectedDate);

            displayDate = SimpleDateFormat.getDateInstance().format(selectedDate);

            if (fieldName.equals("dob")) {
                ((EditText) findViewById(R.id.DateOfBirth)).setText(displayDate);
                this.dateOfBirth = passportDate;

                this.dateOfBirthIntArray[2] = year;
                this.dateOfBirthIntArray[1] = monthOfYear - 1;
                this.dateOfBirthIntArray[0] = dayOfMonth;
            }
            if (fieldName.equals("doe")) {
                ((EditText) findViewById(R.id.DateOfExpiration)).setText(displayDate);
                this.dateOfExpiration = passportDate;

                this.dateOfExpirationIntArray[2] = year;
                this.dateOfExpirationIntArray[1] = monthOfYear - 1;
                this.dateOfExpirationIntArray[0] = dayOfMonth;
            }
        }
        catch (Exception e) {

        }
    }
}
