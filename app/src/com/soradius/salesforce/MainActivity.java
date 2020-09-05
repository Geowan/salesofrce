/*
 * Copyright (c) 2012-present, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.soradius.salesforce;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gturedi.views.StatefulLayout;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.mobilesync.app.MobileSyncSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;
import com.soradius.salesforce.adapter.AccountListRecyclerviewAdapter;
import com.soradius.salesforce.models.AccountListModel;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity {

    private RestClient client;
    RecyclerView accountsRecyclerview;
    ArrayList<AccountListModel> accounts = new ArrayList<>();
    AccountListRecyclerviewAdapter adapter;
    StatefulLayout stateful;

    EditText searchAccount;

    String request_soql = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)   {
		super.onCreate(savedInstanceState);

		// Setup theme
		boolean isDarkTheme = MobileSyncSDKManager.getInstance().isDarkTheme();
		setTheme(isDarkTheme ? R.style.SalesforceSDK_Dark : R.style.SalesforceSDK);
		MobileSyncSDKManager.getInstance().setViewNavigationVisibility(this);

		// Setup view
		setContentView(R.layout.main);

	}
	
	@Override 
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);
		stateful = findViewById(R.id.stateful);

		searchAccount = findViewById(R.id.searchAccount);
		searchAccount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				request_soql = "SELECT Name, Balance__c,AccountId FROM Contact WHERE Name LIKE '%"+s.toString()+"%'";
				sendRequest();
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		accountsRecyclerview = findViewById(R.id.accountsRecyclerview);
		adapter = new AccountListRecyclerviewAdapter(accounts, this);
		accountsRecyclerview.setLayoutManager(new LinearLayoutManager(this));
		accountsRecyclerview.setAdapter(adapter);
		//adapter.setHasStableIds(true);

		super.onResume();
	}		
	
	@Override
	public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client; 

		// Show everything
		findViewById(R.id.root).setVisibility(View.VISIBLE);
		request_soql = "SELECT Name, Balance__c, AccountId FROM Contact LIMIT 10";
		//request_soql = "SELECT AccountId,Account_Number__c,Active__c,Name FROM Account";
		sendRequest();
	}

	/**
	 * Called when "Logout" button is clicked. 
	 * 
	 * @param v
	 */
	public void onLogoutClick(View v) {
		SalesforceSDKManager.getInstance().logout(this);
	}

	private void sendRequest() {
		RestRequest restRequest = null;
		stateful.showLoading();
		try {
			restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(this), request_soql);
			client.sendAsync(restRequest, new AsyncRequestCallback() {
				@Override
				public void onSuccess(RestRequest request, final RestResponse result) {
					result.consumeQuietly(); // consume before going back to main thread
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								accounts.clear();
								JSONArray records = result.asJSONObject().getJSONArray("records");
								Log.i("test","we have "+records.length());
								if (records.length() == 0){
									stateful.showEmpty();
								}else {
									for (int i = 0; i < records.length(); i++) {
										AccountListModel accountListModel = new AccountListModel();
										accountListModel.setBalance__c(records.getJSONObject(i).getString("Balance__c"));
										accountListModel.setName(records.getJSONObject(i).getString("Name"));
										accountListModel.setAccountId(records.getJSONObject(i).getString("AccountId"));
										accounts.add(accountListModel);
									}
									adapter.notifyDataSetChanged();
									stateful.showContent();
								}


							} catch (Exception e) {
								onError(e);
							}
						}
					});
				}

				@Override
				public void onError(final Exception exception) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							stateful.showError(exception.toString(), new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									sendRequest();
								}
							});
						}
					});
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}


	}

}
