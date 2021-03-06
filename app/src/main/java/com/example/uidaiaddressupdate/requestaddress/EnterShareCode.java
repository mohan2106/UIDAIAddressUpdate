package com.example.uidaiaddressupdate.requestaddress;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uidaiaddressupdate.R;
import com.example.uidaiaddressupdate.SharedPrefHelper;
import com.example.uidaiaddressupdate.service.server.ServerApiService;
import com.example.uidaiaddressupdate.service.server.model.getpublickey.PublicKeyRequest;
import com.example.uidaiaddressupdate.service.server.model.getpublickey.PublicKeyResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterShareCode extends Fragment {

    private Button share_code_submit;
    private EditText sharecode_edit_text;
    public EnterShareCode() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_share_code, container, false);
        share_code_submit = (Button) view.findViewById(R.id.share_code_submit);
        sharecode_edit_text = (EditText)view.findViewById(R.id.et_share_code);
        share_code_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Wait while we send the request to lender...");
                String recieverShareCode = sharecode_edit_text.getText().toString();
                Log.d("KYC",recieverShareCode);
                ServerApiService.getApiInstance().getPublicKey(new PublicKeyRequest(SharedPrefHelper.getUidToken(getContext()),SharedPrefHelper.getAuthToken(getContext()),recieverShareCode)).enqueue(new Callback<PublicKeyResponse>() {
                    @Override
                    public void onResponse(Call<PublicKeyResponse> call, Response<PublicKeyResponse> response) {
                        progressDialog.dismiss();
                        switch (response.code()){
                            case 200:
                                Log.d("KYC",response.message());
                                String publicKey = response.body().getPublicKey();
                                Bundle bundle = new Bundle();
                                bundle.putString("publicKey",publicKey);
                                bundle.putString("recieverShareCode",recieverShareCode);
                                Navigation.findNavController(view).navigate(R.id.action_enterShareCode_to_requesterOTP,bundle);
                                break;

                            case 400:
                                Toast.makeText(getActivity(),"Invalid request parameters",Toast.LENGTH_SHORT).show();
                                break;

                            case 403:
                                Toast.makeText(getActivity(),"Invalid share code",Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                Toast.makeText(getActivity(),"Error code: "+String.valueOf(response.code()),Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }

                    @Override
                    public void onFailure(Call<PublicKeyResponse> call, Throwable t) {
                        t.printStackTrace();
                        //Error
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        return view;
    }
}