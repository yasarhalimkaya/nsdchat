package yasarkay.nsdchat;

import android.net.nsd.NsdServiceInfo;

/**
 * NetworkServiceDiscoveryListener interface
 * Used to be notified with network service discovery events
 */
public interface NetworkServiceDiscoveryListener {

    /**
     * Called when a new service is resolved
     * Handles duplications, clients will only be notified
     * if this is a brand new service resolved
     * @param nsdServiceInfo New resolved service info
     */
    void onNewServiceResolved(NsdServiceInfo nsdServiceInfo);

    /**
     * Called when an already resolved service is resolved again
     * with different attributes, i.e, ip address, port etc.
     * @param nsdServiceInfo Updated service info
     */
    void onServiceUpdated(NsdServiceInfo nsdServiceInfo);

    /**
     * Called when a previously resolved service is no longer available
     * @param nsdServiceInfo Lost service info
     */
    void onServiceLost(NsdServiceInfo nsdServiceInfo);
}
