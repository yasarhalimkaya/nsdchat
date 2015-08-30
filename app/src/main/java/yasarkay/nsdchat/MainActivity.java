package yasarkay.nsdchat;

import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private NetworkServiceDiscoveryHelper mNetworkServiceDiscoveryHelper;
    private TextView mResolvedServicesTextView;
    private Button mRefreshButton;
    private TextView mIncomingTextView;
    private EditText mOutgoingEditText;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get an handle for each view in the layout, except the titles
        mResolvedServicesTextView = (TextView)findViewById(R.id.resolvedServicesTextView);

        mRefreshButton = (Button)findViewById(R.id.refresh);
        mRefreshButton.setOnClickListener(this);

        mIncomingTextView = (TextView)findViewById(R.id.incomingTextView);

        mOutgoingEditText = (EditText)findViewById(R.id.outgoingEditText);

        mSendButton = (Button)findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(this);

        // Initialize our NetworkServiceDiscoveryHelper
        mNetworkServiceDiscoveryHelper = new NetworkServiceDiscoveryHelper(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh : {
                if (mNetworkServiceDiscoveryHelper.getResolvedServices().size() == 0) {
                    mResolvedServicesTextView.setText("No service available");
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (NsdServiceInfo nsdServiceInfo : mNetworkServiceDiscoveryHelper.getResolvedServices()) {
                        stringBuilder.append(nsdServiceInfo + "\n");
                    }
                    mResolvedServicesTextView.setText(stringBuilder.toString());
                }
                break;
            }
            case R.id.sendButton : {
                // TODO : Send the message to the first available client
            }
            default :
                break;
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
