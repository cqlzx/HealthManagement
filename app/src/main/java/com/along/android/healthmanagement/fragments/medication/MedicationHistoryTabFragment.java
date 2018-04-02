package com.along.android.healthmanagement.fragments.medication;


import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.along.android.healthmanagement.R;
import com.along.android.healthmanagement.activities.MedicationDetailsActivity;
import com.along.android.healthmanagement.adapters.MedicationHistoryAdapter;
import com.along.android.healthmanagement.apis.Apis;
import com.along.android.healthmanagement.common.JsonCallback;
import com.along.android.healthmanagement.entities.Prescription;
import com.along.android.healthmanagement.network.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedicationHistoryTabFragment extends Fragment {
    private Fragment mContext;

    private List<Prescription> mPrescriptionList = new ArrayList<>();


    public MedicationHistoryTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_medication_history, container, false);

        OkGo.<BaseResponse<List<Prescription>>>get(Apis.getMedicationHistory())
                .tag(this)
                .execute(new JsonCallback<BaseResponse<List<Prescription>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<List<Prescription>>> response) {
                        BaseResponse data = response.body();
                        if (data != null) {
                            mPrescriptionList = (List<Prescription>) data.data;
                            initCurrentList(mPrescriptionList, view);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<List<Prescription>>> response) {
                        super.onError(response);
                        // 错误的时候取本地
                        List<Prescription> prescriptionList;

                        try {
                            prescriptionList = Prescription.listAll(Prescription.class);
                        } catch (SQLiteException e) {
                            prescriptionList = new ArrayList<>();
                        }
                        initCurrentList(prescriptionList, view);
                    }
                });

        return view;
    }

    public void initCurrentList(List<Prescription> prescriptionList, View view) {
        List<Prescription> historyPrescription = new ArrayList<Prescription>();
        for (Prescription prescription : prescriptionList) {
            //If today > endDate, then add to current
            try {
                if (Calendar.getInstance().getTimeInMillis() > Long.parseLong(prescription.getEndDate())) {
                    historyPrescription.add(prescription);
                }
            } catch (NumberFormatException nfe) {
                historyPrescription.add(prescription);
            }
        }

        MedicationHistoryAdapter medicationHistoryAdapter =
                new MedicationHistoryAdapter(getActivity(), historyPrescription);

        TextView tvEmptyMsg = (TextView) view.findViewById(R.id.tvMHEmptyMsg);

        ListView listView = (ListView) view.findViewById(R.id.medication_history_list);

        listView.setAdapter(medicationHistoryAdapter);
        listView.setEmptyView(tvEmptyMsg);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MedicationDetailsActivity medicationDetailActivity = new MedicationDetailsActivity();

                TextView prescriptionId = (TextView) view.findViewById(R.id.tvMHPrescriptionId);

                Intent medicationDetailsIntent = new Intent(getActivity(), MedicationDetailsActivity.class);
                medicationDetailsIntent.putExtra("selectedPrescriptionItemId", prescriptionId.getText().toString());
                startActivity(medicationDetailsIntent);
            }
        });
    }

}
