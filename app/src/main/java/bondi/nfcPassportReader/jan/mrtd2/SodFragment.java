package bondi.nfcPassportReader.jan.mrtd2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import nfcPassportReader.jan.mrtd2.R;

public class SodFragment extends Fragment
{
    private ListView sodListView;
    private ArrayList<String[]> sodDataList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        System.out.println("DG1 onCreateView called");
        View view = inflater.inflate(R.layout.sod_fragment, container, false);
        this.sodListView = (ListView)view.findViewById(R.id.sodListView);
        this.populateListView();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.populateListView();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.populateListView();
    }

    public void populateListView()
    {
        System.out.println("Try to populate SOD");
        if(this.sodListView != null) {
            System.out.println("Will populate SOD");
            SodToListAdapter sodToListAdapter =
                    new SodToListAdapter(
                            getActivity(),
                            this.sodDataList
                    );

            System.out.println("sodArrayAdapter has : ".concat(String.valueOf(sodToListAdapter.getCount()).concat(" elements")));
            this.sodListView.setAdapter(sodToListAdapter);
        }
    }

    public void setSodList(ArrayList<String[]> sodDataList)
    {
        this.sodDataList = sodDataList;
        this.populateListView();
    }
}
