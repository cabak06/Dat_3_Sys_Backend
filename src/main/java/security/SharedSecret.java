package security;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/* This generates a secure random per execution of the server
 * A server restart, will generate a new key, making all existing tokens invalid
 * For production (and if a load-balancer is used) come up with a persistent key strategy */
public class SharedSecret {

    private static byte[] secret;

    public static byte[] getSharedKey() {
        /*
        System.out.println("******************* IMPORTANT ******************'");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("**** REMOVE FIXED SECRET BEFORE PRODUCTION *******");
        System.out.println("****      See security.SharedSecret        *******");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
         */
        //REMOVE BEFORE PRODUCTION
//        if (true) {
//            return "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes();
//        }
        try {

            InputStream input = SharedSecret.class.getResourceAsStream("/SharedSecret.properties");
            Properties pros = new Properties();

            pros.load(input);

            String sharedSecret = pros.getProperty("sharedSecret");

            if (sharedSecret.equals("InitialSetup_Group3_SharedSecret")) {
                System.out.println("******************* IMPORTANT ******************'");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("**** CHANGE THE SHARED SECRET BEFORE USE ! *******");
                System.out.println("****      See security.SharedSecret        *******");
                System.out.println("And change SharedSecret.properties under resources");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }

            return sharedSecret.getBytes();

        } catch (Exception ex) {
            Logger.getLogger(SharedSecret.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR LOADING THE SHAREDSECRET FILE");
        }

        if (secret == null) {  //Or better read as an environment variable set on production server
            secret = new byte[32];
            new SecureRandom().nextBytes(secret);
        }
        return secret;
    }
}
