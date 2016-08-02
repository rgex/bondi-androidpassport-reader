package bondi.nfcPassportReader.jan.mrtd2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nfcPassportReader.jan.mrtd2.R;

public class SodToListAdapter extends ArrayAdapter<String[]>
{

    public SodToListAdapter(Context context, ArrayList<String[]> fields)
    {
        super(context, 0, fields);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        String[] field = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.default_list_view_element, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.DG1TitleTextView);
        TextView tvContent = (TextView) convertView.findViewById(R.id.DG1ContentTextView);

        tvTitle.setText(field[0]);
        tvContent.setText(field[1]);

        return convertView;
    }
}
