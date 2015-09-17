package yasarkay.nsdchat;

import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private NetworkServiceDiscoveryHelper mNetworkServiceDiscoveryHelper;
    private MyNetworkServiceDiscoveryListener mMyNetworkServiceDiscoveryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize our NetworkServiceDiscoveryHelper
        mNetworkServiceDiscoveryHelper = new NetworkServiceDiscoveryHelper(this);

        // Instantiate a NetworkServiceDiscoveryListener and register it to the NetworkServiceDiscoveryHelper
        mMyNetworkServiceDiscoveryListener = new MyNetworkServiceDiscoveryListener();
        mNetworkServiceDiscoveryHelper.addNetworkServiceDiscoveryListener(mMyNetworkServiceDiscoveryListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * NetworkServiceDiscoveryListener implementation
     */
    class MyNetworkServiceDiscoveryListener implements NetworkServiceDiscoveryListener {

        @Override
        public void onNewServiceResolved(NsdServiceInfo nsdServiceInfo) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "onNewServiceResolved : " + nsdServiceInfo.toString());
        }

        @Override
        public void onServiceUpdated(NsdServiceInfo nsdServiceInfo) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "onServiceUpdated : " + nsdServiceInfo.toString());
        }

        @Override
        public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "onServiceLost : " + nsdServiceInfo.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mNetworkServiceDiscoveryHelper.teardown();
        super.onDestroy();
    }
}
