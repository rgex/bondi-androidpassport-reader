package bondi.nfcPassportReader.jan.mrtd2;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import nfcPassportReader.jan.mrtd2.R;

public class Dg1Fragment extends Fragment
{
    private ListView dg1ListView;
    private ArrayList<String[]> dg1DataList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        System.out.println("DG1 onCreateView called");
        View view = inflater.inflate(R.layout.dg1_fragment, container, false);
        this.dg1ListView = (ListView)view.findViewById(R.id.dg1ListView);
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
        System.out.println("Try to populate DG1");
        if(this.dg1DataList != null && this.dg1ListView != null) {
            System.out.println("Will populate DG1");
            DG1ToListAdapter dg1ArrayAdapter =
                    new DG1ToListAdapter(
                            getActivity(),
                            this.dg1DataList
                    );

            System.out.println("dg1ArrayAdapter has : ".concat(String.valueOf(dg1ArrayAdapter.getCount()).concat(" elements")));
            this.dg1ListView.setAdapter(dg1ArrayAdapter);
        }
    }

    public void setDg1List(ArrayList<String[]> dg1DataList)
    {
        this.dg1DataList = dg1DataList;
        this.populateListView();
    }

}
