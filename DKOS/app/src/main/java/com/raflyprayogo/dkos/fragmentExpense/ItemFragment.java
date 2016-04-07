package com.raflyprayogo.dkos.fragmentExpense;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.toolbox.StringRequest;
import com.raflyprayogo.dkos.Handler.AppConfig;
import com.raflyprayogo.dkos.Handler.CommonUtils;
import com.raflyprayogo.dkos.Handler.SQLiteHandler;
import com.raflyprayogo.dkos.Volley.AppController;
import com.raflyprayogo.dkos.R;

public class ItemFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener {

    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private OnFragmentInteractionListener mListener;

    private RelativeLayout loading;
    private RelativeLayout errElement;

    private AbsListView mListView;

    private List<Expense> ItemList = new ArrayList<Expense>();
    private ExpenseAdapter adapter;
    String member_id, group_id, assid_clicked;
    private AdapterView adapterView;
    private static final int    REQUEST_CODE_GET_JSON = 1;
    Bitmap bitmap;

    public ItemFragment() {
    }

    // TODO: Rename and change types of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Change Adapter to display your content
        adapter = new ExpenseAdapter(getActivity(), ItemList);

        //get user logged in
        SQLiteHandler db = new SQLiteHandler(getActivity());
        HashMap<String, String> user = db.getUserDetails();
        member_id = user.get("uid");
        group_id  = user.get("group_id");

        getData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        loading = (RelativeLayout) view.findViewById(R.id.loadingElement);
        errElement = (RelativeLayout) view.findViewById(R.id.errorElement);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(R.id.emptyElement));
        ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view. findViewById(R.id.SwipeList);

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryDark);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading.setVisibility(View.VISIBLE);
                ItemList.clear();
                adapter.notifyDataSetChanged();
                getData();
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        return view;
    }

    public void getData() {

        String tag_string_req = "req_consultation";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GET_EXPENSE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Volley", "Response: " + response.toString());

                try {

                    JSONArray jObj = new JSONArray(response);

                    for (int i = 0; i < jObj.length(); i++) {

                        JSONObject data =  jObj.getJSONObject(i);

                        Expense Mod = new Expense();
                        Mod.setId(data.getString("expense_id"));
                        Mod.setTitle(data.getString("expense_title"));
                        Mod.setCost(data.getString("expense_cost"));
                        ItemList.add(Mod);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if(ItemList.size() == 0){
                    setEmptyText();
                }
                adapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
                errElement.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_LONG).show();
                errElement.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("member_id", member_id);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
        }

//        Intent intent = new Intent(getContext(), JsonFormActivity.class);
////        String json = CommonUtils.loadJSONFromAsset(getContext(), "data.json");
//        String json  = ItemList.get(position).getJson();
//        intent.putExtra("json", json);
//        assid_clicked = ItemList.get(position).getId();
//        startActivityForResult(intent, REQUEST_CODE_GET_JSON);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (null != mListener) {
        }

//        new AlertDialog.Builder(getContext())
//                .setMessage("Open this menu")
//                .setCancelable(false)
//                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Intent intent = new Intent(getContext(), JsonFormActivity.class);
//                        //        String json = CommonUtils.loadJSONFromAsset(getContext(), "data.json");
//                        String json  = ItemList.get(position).getJson();
//                        intent.putExtra("json", json);
//                        assid_clicked = ItemList.get(position).getId();
//                        startActivityForResult(intent, REQUEST_CODE_GET_JSON);
//                    }
//                })
//                .setNegativeButton(R.string.cancel, null)
//                .show();

        return true;
    }

    public void setEmptyText() {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof RelativeLayout) {
            ((RelativeLayout) emptyView).setVisibility(View.VISIBLE);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
