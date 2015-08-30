package yasarkay.nsdchat;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by qbeart on 28/08/15.
 */
public class NetworkServiceDiscoveryHelper {

    private final String TAG = "NsdHelper";

    private String mServiceName = Build.MODEL;
    private final String mServiceType = "_http._tcp.";
    private ServerSocket mServerSocket = null;
    private RegistrationListener mRegistrationListener;
    private DiscoveryListener mDiscoveryListener;
    private ResolveListener mResolveListener;
    private NsdManager mNsdManager;
    private ArrayList<NsdServiceInfo> mResolvedServices;

    public NetworkServiceDiscoveryHelper(Context context) {

        // Get an instance of NsdManager
        mNsdManager = (NsdManager)context.getSystemService(Context.NSD_SERVICE);

        // Initialize the registration listener
        mRegistrationListener = new RegistrationListener();

        // Initialize the discovery listener
        mDiscoveryListener = new DiscoveryListener();

        // Initialize the resolve listener
        mResolveListener = new ResolveListener();

        mResolvedServices = new ArrayList<>();

        // Start advertising and start discovering
        registerService();
        discoverServices();
    }

    public boolean registerService() {
        NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();

        nsdServiceInfo.setServiceName(mServiceName);
        nsdServiceInfo.setServiceType(mServiceType);

        // Try to get the next available port
        try {
            if (mServerSocket == null)
                mServerSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        nsdServiceInfo.setPort(mServerSocket.getLocalPort());

        try {
            mNsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean discoverServices() {
        try {
            mNsdManager.discoverServices(mServiceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    // RegistrationListener implementation
    class RegistrationListener implements NsdManager.RegistrationListener {

        @Override
        public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) { }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) { }

        @Override
        public void onServiceRegistered(NsdServiceInfo serviceInfo) {
            // The service name provided earlier may have been changed due to a conflict.
            mServiceName = serviceInfo.getServiceName();

            if (BuildConfig.DEBUG)
                Log.d(TAG, "onServiceRegistered : " + serviceInfo);
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo serviceInfo) { }
    }

    // DiscoveryListener implementation
    class DiscoveryListener implements NsdManager.DiscoveryListener {

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            mNsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            mNsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onDiscoveryStarted(String serviceType) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Service discovery started");
        }

        @Override
        public void onDiscoveryStopped(String serviceType) { }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Service found : " + serviceInfo);

            if (!serviceInfo.getServiceType().equals(mServiceType)) {
                Log.d(TAG, "Unknown service type : " + serviceInfo.getServiceType());
            } else if (serviceInfo.getServiceName().equals(mServiceName)) {
                Log.d(TAG, "Same machine : " + serviceInfo);
            } else {
                mNsdManager.resolveService(serviceInfo, mResolveListener);
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Service lost : " + serviceInfo);

            // The service is no longer available
            int index = -1;
            for (int i = 0; i < mResolvedServices.size(); i++) {
                if (mResolvedServices.get(i).getServiceName().equals(serviceInfo.getServiceName())) {
                    index = i;
                    break;
                }
            }

            if (index != -1)
                mResolvedServices.remove(index);
        }
    }

    // ResolveListener implementation
    class ResolveListener implements NsdManager.ResolveListener {

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Resolve failed : " + errorCode);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Resolve successful : " + serviceInfo);

            // Assume the same machine is not resolved via resolveService in the first place

            // Store the resolved service information, someone may want to do something with it
            mResolvedServices.add(serviceInfo);
        }
    }

    public ArrayList<NsdServiceInfo> getResolvedServices() {
        return mResolvedServices;
    }

    public void teardown() {
        mNsdManager.unregisterService(mRegistrationListener);
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);

        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
