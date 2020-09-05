package com.soradius.salesforce.details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gturedi.views.StatefulLayout;
import com.salesforce.androidsdk.mobilesync.app.MobileSyncSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;
import com.soradius.salesforce.MainActivity;
import com.soradius.salesforce.R;
import com.soradius.salesforce.models.AccountListModel;

import org.json.JSONArray;
import org.michaelbel.bottomsheet.BottomSheet;
import org.michaelbel.bottomsheet.BottomSheetCallback;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AccountDetailsActivity extends SalesforceActivity {

    String contact_id= "";
    private RestClient client;
    String request_soql = "";
    Toolbar toolbar;
    AccountListModel singleAccount = new AccountListModel();
    TextView title,balanceAmnt;
    StatefulLayout statefulLayout;

    RecyclerView detailsRecyclerview;

    AccountDetailsRecyclerviewAdapter adapter;
    ArrayList<AccountDetailsModel> details = new ArrayList<>();

    ImageView backBtn;
    Button makePaymentButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setTheme(R.style.SalesforceSDK);
        MobileSyncSDKManager.getInstance().setViewNavigationVisibility(this);

        // Setup view
        setContentView(R.layout.activity_account_details);
    }

    @Override
    public void onResume(RestClient client) {
        this.client = client;
    }

    @Override
    public void onResume() {
        super.onResume();
        statefulLayout = findViewById(R.id.statefulLayout);


        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        balanceAmnt = findViewById(R.id.balanceAmnt);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        makePaymentButton = findViewById(R.id.makePaymentButton);
        makePaymentButton.setOnClickListener(v-> showSelectorDialog());

        detailsRecyclerview = findViewById(R.id.detailsRecyclerview);
        adapter = new AccountDetailsRecyclerviewAdapter(details,this);
        detailsRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        detailsRecyclerview.setAdapter(adapter);


        Intent intent = getIntent();
        contact_id = intent.getStringExtra("account_id");
        toolbar.setTitle(contact_id);

        request_soql = "SELECT Name, Balance__c, AccountId,Most_Recent_Opportunity_Created__c,Most_Recent_Payment_Date_Hebrew__c,Most_Recent_Payment_Date__c FROM Contact WHERE AccountId='"+contact_id+"'";
        sendRequest();
    }


    private void sendRequest() {
        RestRequest restRequest = null;
        statefulLayout.showLoading();
        try {
            restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(this), request_soql);
            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, final RestResponse result) {
                    result.consumeQuietly(); // consume before going back to main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray records = result.asJSONObject().getJSONArray("records");

                                for (int i = 0; i < records.length(); i++) {
                                    singleAccount.setBalance__c(records.getJSONObject(i).getString("Balance__c"));
                                    singleAccount.setName(records.getJSONObject(i).getString("Name"));
                                    singleAccount.setMost_Recent_Opportunity_Created__c(records.getJSONObject(i).getString("Most_Recent_Opportunity_Created__c"));
                                    singleAccount.setMost_Recent_Payment_Date__c(records.getJSONObject(i).getString("Most_Recent_Payment_Date__c"));
                                    setUserData();
                                }
                                statefulLayout.showContent();

                            } catch (Exception e) {
                                onError(e);
                            }
                        }
                    });
                }

                @Override
                public void onError(final Exception exception) {
                    statefulLayout.showError(exception.toString(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendRequest();
                        }
                    });
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    void setUserData(){

        title.setText(singleAccount.getName() + " contact details");
        balanceAmnt.setText(singleAccount.getBalance__c());

        details.clear();
        details.add(new AccountDetailsModel("Opportunity name",singleAccount.getMost_Recent_Opportunity_Created__c()));
        details.add(new AccountDetailsModel("Recent payment date",singleAccount.getMost_Recent_Payment_Date__c()));
        details.add(new AccountDetailsModel("Recent payment date hebrew",singleAccount.getMost_Recent_Payment_Date_Hebrew__c()));

        adapter.notifyDataSetChanged();
    }

    void showSelectorDialog(){
        String[] items = new String[] {
                "Cash",
                "Credit card"
        };
        Drawable[] icons = new Drawable[] {
                ContextCompat.getDrawable(this, R.drawable.menu_search),
                ContextCompat.getDrawable(this, R.drawable.menu_search),
        };
        BottomSheet.Builder builder = new BottomSheet.Builder(this);
        builder.setTitle("Select payment option");
        builder.setItems(items, icons, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCallback(new BottomSheetCallback() {
            @Override
            public void onShown() {

            }

            @Override
            public void onDismissed() {

            }
        });
        builder.show();
    }
}