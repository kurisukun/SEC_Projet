package Yubikey;

import com.yubico.client.v2.YubicoClient;

public class Resource {

    public static final int CLIENT_ID = 65519;
    public static final String API_KEY = "45/TnGPsU8BuPu+LsqKlJ+vcFLo=";

    private final YubicoClient client = YubicoClient.getClient(CLIENT_ID, API_KEY);


}
