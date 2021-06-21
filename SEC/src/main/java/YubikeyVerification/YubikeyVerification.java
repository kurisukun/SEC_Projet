package YubikeyVerification;

import com.yubico.client.v2.VerificationResponse;
import com.yubico.client.v2.YubicoClient;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class YubikeyVerification {

    private static final Dotenv dotenv = Dotenv.load();
    public static final int CLIENT_ID = Integer.parseInt(dotenv.get("CLIENT_ID"));
    public static final String API_KEY = dotenv.get("SECRET_KEY");

    private final YubicoClient client = YubicoClient.getClient(CLIENT_ID, API_KEY);
    private final Logger logger = LogManager.getLogger(YubikeyVerification.class);

    public boolean verify(String otp) throws Exception{
        logger.trace("verify");

        VerificationResponse response = client.verify(otp);
        if(response.isOk()){
            String yubikeyId = YubicoClient.getPublicId(otp);
            String userId = otp.substring(0, 12);
            if (yubikeyId.equals(userId)) {
                logger.info("User " + userId + " successfully authentified using YubiKey");
                return true;
            }
        }
        logger.warn("User tried to authentify but failed");
        return false;
    }
}
