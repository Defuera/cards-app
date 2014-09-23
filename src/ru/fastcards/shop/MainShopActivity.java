package ru.fastcards.shop;

import java.lang.ref.WeakReference;

import ru.fastcards.PurseDialogFragment;
import ru.fastcards.R;
import ru.fastcards.onShopItemClickListener;
import ru.fastcards.common.Article;
import ru.fastcards.editor.EditorActivity;
import ru.fastcards.shop.BottomPanelShopFragment.onBottomButtonClickListener;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainShopActivity extends ActionBarActivity implements onBottomButtonClickListener, onShopItemClickListener {

    private Context context;

    private static final String TAG = "MainShopActivity";

    private SelectionsFragment selection_view_fragment;

    private BundlesShopFragment bundles;
    private PurchasesShopFragment purchases;
    private CatalogFragment catalog;
    private BottomPanelShopFragment bottomPanel;

    private ActionsFragment fragmentNew;
    private ActionsFragment fragmentRecommended;
    private ActionsFragment fragmentBestSellers;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private ShopDialogFragment shopDialog;

    private HandlerCallback callback;

    private BannerPagerFragment bannerPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shop);

        context = this;

        fragmentManager = getSupportFragmentManager();

        initializeUI();

        createBottomPanelFragment();
        createActionsFragment();
        createBannerFragment();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        supportInvalidateOptionsMenu();
        setCallback();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (callback != null) callback.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wealth, menu);
        MenuItem item = menu.findItem(R.id.action_wealth);
        WealthView wealthView = WealthView.getInstanse(this);

        MenuItemCompat.setActionView(item, wealthView);
        wealthView.refreshWealth();

        return true;
    }

    private void initializeUI() {
        //Containers for functional fragments
        selection_view_fragment = new SelectionsFragment();

        purchases = new PurchasesShopFragment();
        catalog = new CatalogFragment();

//		//FunctionalFragments

        bundles = new BundlesShopFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, selection_view_fragment);

        fragmentTransaction.commit();

    }


    private void createBannerFragment() {
        bannerPager = new BannerPagerFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.banner_pager, bannerPager);
        fragmentTransaction.commit();
    }

    private void createBottomPanelFragment() {

        bottomPanel = new BottomPanelShopFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.bottom_panel_fragment_container, bottomPanel);
        fragmentTransaction.commit();
    }

    private void createActionsFragment() {
        fragmentNew = ActionsFragment.newInstance(context, Constants.GET_NEW);
        fragmentRecommended = ActionsFragment.newInstance(context, Constants.GET_RECOMMENDED);
        fragmentBestSellers = ActionsFragment.newInstance(context, Constants.GET_BEST_SELLERS);

        addActionsFragment();

    }

    private void addActionsFragment() {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_novel, fragmentNew);
        fragmentTransaction.add(R.id.fragment_actions, fragmentRecommended);
        fragmentTransaction.add(R.id.fragment_leaders, fragmentBestSellers);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void removeActionsFragment() {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragmentNew);
        fragmentTransaction.remove(fragmentRecommended);
        fragmentTransaction.remove(fragmentBestSellers);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBottomButtonClick(int ID) {
        // TODO Auto-generated method stub
        if (selection_view_fragment.isVisible()) removeActionsFragment();
        switch (ID) {
            case 0:
                replaceFragments(selection_view_fragment,
                        R.string.title_main_shop_activity, R.id.selection);
                break;
            case 1:
                replaceFragments(catalog, R.string.str_catalog, R.id.catalog);
                break;
            case 2:
                replaceFragments(purchases, R.string.str_purchases, R.id.purchase);
                break;
            case 3:
                replaceFragments(bundles, R.string.str_bundles, R.id.bundle);
                break;
        }
    }

    private void replaceFragments(Fragment fragment, int title, int text_container) {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        fragmentTransaction = fragmentManager.beginTransaction();
//		fragmentTransaction.show(fragment);
//		
        fragmentTransaction.replace(R.id.fragment_container, fragment);

        fragmentTransaction.commit();

        if (fragment == selection_view_fragment) {
            createBannerFragment();
            addActionsFragment();
        }
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(title));
        bottomPanel.changeColorText(text_container);

    }


    @Override
    public void buyItem(final Article a, final String purchaseType) {
        // TODO Auto- generated method stub
        if (!Utils.isNetworkAvailable(context)) {
            Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_LONG).show();
            return;
        }
        shopDialog = ShopDialogFragment.newInstance(context, a, purchaseType);
        shopDialog.show(getSupportFragmentManager(), TAG);
        shopDialog.setOnBuyClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Purchaser(context, callback, a, purchaseType, shopDialog).buyArticle();
            }
        });

        shopDialog.setOnChooseClickListener(onChooseClickListener);
    }


    private static class HandlerCallback extends Handler {

        WeakReference<Activity> wrActivity;

        public HandlerCallback(Activity activity) {
            wrActivity = new WeakReference<Activity>(activity);
        }
    }

    private void setCallback() {
        callback = new HandlerCallback(this) {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == Constants.TRANSACTION_SUCCED)
                    refreshFragments();
                else if (msg.what == Constants.NOT_ENOUGH_STARS)
                    new PurseDialogFragment().show(getSupportFragmentManager(), TAG);
            }

            ;
        };
    }

    private void refreshFragments() {
        fragmentNew.refresh();
        fragmentRecommended.refresh();
        fragmentBestSellers.refresh();
        purchases.refresh();
        bundles.refresh();
        catalog.refresh();
//			ca.refresh();
    }

    private OnClickListener onChooseClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            shopDialog.dismiss();
            Intent intent = new Intent(context, EditorActivity.class);
            intent.putExtra(Constants.EXTRA_ID, shopDialog.getChooseTheme().getId());
            intent.putExtra(Constants.EXTRA_PURCHASE_ID, shopDialog.getChooseTheme().getPurchaseId());
            intent.putExtra(Constants.EXTRA_POSTCARD_SELECTED, shopDialog.getSelectedPostcard());
            startActivity(intent);
        }
    };
}
