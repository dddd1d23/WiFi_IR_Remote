package mikerush.dhy0077.wifi_ir.esptouch;

public interface IEsptouchListener {
    /**
     * when new esptouch result is added, the listener will call
     * onEsptouchResultAdded callback
     *
     * @param result the Esptouch result
     */
    void onEsptouchResultAdded(IEsptouchResult result);
}
