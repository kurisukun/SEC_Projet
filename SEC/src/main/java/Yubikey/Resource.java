package Yubikey;

import com.yubico.client.v2.VerificationResponse;
import com.yubico.client.v2.YubicoClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Resource {

    public static final int CLIENT_ID = 65519;
    public static final String API_KEY = "45/TnGPsU8BuPu+LsqKlJ+vcFLo=";

    private final YubicoClient client = YubicoClient.getClient(CLIENT_ID, API_KEY);
    private final Logger logger = LogManager.getLogger(Resource.class);

    public boolean login(String otp) throws Exception{

        logger.debug("login function");

        VerificationResponse response = client.verify(otp);
        if(response.isOk()){
            String yubikeyId = YubicoClient.getPublicId(otp);
            String userId = otp.substring(0, 12);
            if (yubikeyId.equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
