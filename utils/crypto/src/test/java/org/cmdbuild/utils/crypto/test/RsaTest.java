/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto.test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import static org.cmdbuild.utils.crypto.CmRsaUtils.parsePrivateKey;
import static org.cmdbuild.utils.crypto.CmRsaUtils.parsePublicKey;
import static org.cmdbuild.utils.crypto.CmRsaUtils.verifySignedChallenge;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.crypto.CmRsaUtils.createChallengeResponse;
import static org.cmdbuild.utils.crypto.CmRsaUtils.createToken;
import static org.cmdbuild.utils.crypto.CmRsaUtils.verifyToken;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeBytes;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class RsaTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String RSA_PRIVATE_KEY_DATA = "-----BEGIN RSA PRIVATE KEY-----\n"
            + "MIIEpAIBAAKCAQEAxvOu7j/mQWfpxTh4D0dWunY4vrgH4+Qk/k/N6Qpj5IiXl2T3\n"
            + "uDH075q3fK9FfPp7IgUGaUwB9KOMMuBvw4eJhl47q+bIdS2NkDqOAm8UEEk07wgg\n"
            + "EZ15A2QDV8SzOc0AwnFDeRcuN6ftP1pUIaBJyPX7BcyFIRFCPzWkeEdDm6Limt71\n"
            + "/pLj43QCyQa1oBm7uQqLElcBLvYt1dXlO9ucVi+VuxP6LZoE+Xn7AyToch2jJxPZ\n"
            + "Or3ruYMNntVeZPsfzKrYBt5ehH1iDZ6L0+FBqIzzULGoxOgS2KcKc1wbuKR9npVM\n"
            + "56d9MT1GyyV4B5hhIoUxQCiqIeMstksuvuOo3wIDAQABAoIBAQCSCtdidO2lHY5x\n"
            + "A56+OHPltFq7RYQlOZgeRp60brgdTldY/vkI8UMHj98ZW9/6ejRhKKcoLG9TEasc\n"
            + "vbvNIHVHeWz8JxEHU0UTlqggCUIFIngE70X7KElov8Xka7PUvjPMiDArd2Sp3k89\n"
            + "riUL+gmvx0FZZGRlfbHPjKUK1eU/HOi8PRz2wmkMXZqG13I9rHjf8GGjxieOsLxz\n"
            + "qAdSiD+ZvUkCBeT+kg+CQQNjAgCqwuPVQbIpwglME5R8twWN60PdA6n6c1QCXpSM\n"
            + "FWDAnpNMiEf9gF7yYMen4V+bOv8R29mtv2phqJUDA9SK0zaXCzcjU/JIYwcSpemj\n"
            + "ufvciIcBAoGBAP82/DBHjb9G9Aifhj9QP0QqdLWrQ+qyGyx+iInKv//yNvK+Rspq\n"
            + "PKmvzKds64jSq+HMbr9FAoGElUFX4+GoDwg//XT9wYsbhSahbxCIryGUWChN3ghq\n"
            + "scOdaUFLnI0f4F1lyjyery7padELEPkaIw0av0j4EQ2KHpPjKNRWMoA/AoGBAMeQ\n"
            + "YkQF/g5U/O26owCuwC/t5RImhFR3Z6SIzz8TdEZSqVVyxXGjnKCMiCdFOcImKYqQ\n"
            + "iBI3A3MoHELV4K5T+wvR24SMhZ9LU5hRwo8/tIqo7gmUrC6cEh0JbY2S+HDVzBbO\n"
            + "IES/eLTmWwIMwGO2d+nYVSvMGPoR0D0TZZ4Kaq9hAoGAMb6f7TElOdE0o7GkUxbr\n"
            + "HfhAg2B8fnR0w3luV51DSzoE58+asL2AUXrIoyGhzytxATP9qh5jXKdhCDl/W26M\n"
            + "b4k5d+I2JU0Z6OuyYECkEiF+BW0YB//z4jL+XxHD0+YQr2O1xZcQgk5Qp8RGbKdh\n"
            + "iZ5bBngDquXvC9hNDwY2FbUCgYBviM6i3gaD1BQYebsoYoKfRwJEBqrm2mVem+fY\n"
            + "hPLzarehPigDv0GGqYRfDl2dmN6WxviF1aFj9wL0h7yvMdiZXoylpDP6N4tdEjT/\n"
            + "AsfQx2FikWk6E5g8CkzkV0PrLFKIXGPEiI7Z6/TpTF8qW4zhcTsI42UCPnp36CNf\n"
            + "FMtTgQKBgQDTyGM7CiH/5R0DXRBnfZDbWUXObCBbyZaXAsQcekc5qLMLb/dgPLSO\n"
            + "YQC1E4+6EgQWPPccwHsVi7P3vL08s8iqrOIg1OGDCaY83yDfyw9bC4Y/GH7mTmfC\n"
            + "fwOygNv2fh4tgs6gBKdf2HBOpXGY9RaWT8ZyEyX6VSgLykSCQBxSXg==\n"
            + "-----END RSA PRIVATE KEY-----",
            RSA_PUBLIC_KEY_DATA_1 = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDG867uP+ZBZ+nFOHgPR1a6dji+uAfj5CT+T83pCmPkiJeXZPe4MfTvmrd8r0V8+nsiBQZpTAH0o4wy4G/Dh4mGXjur5sh1LY2QOo4CbxQQSTTvCCARnXkDZANXxLM5zQDCcUN5Fy43p+0/WlQhoEnI9fsFzIUhEUI/NaR4R0ObouKa3vX+kuPjdALJBrWgGbu5CosSVwEu9i3V1eU725xWL5W7E/otmgT5efsDJOhyHaMnE9k6veu5gw2e1V5k+x/MqtgG3l6EfWINnovT4UGojPNQsajE6BLYpwpzXBu4pH2elUznp30xPUbLJXgHmGEihTFAKKoh4yy2Sy6+46jf davide@phil",
            RSA_PUBLIC_KEY_DATA_2 = "AAAAB3NzaC1yc2EAAAADAQABAAABAQDG867uP+ZBZ+nFOHgPR1a6dji+uAfj5CT+T83pCmPkiJeXZPe4MfTvmrd8r0V8+nsiBQZpTAH0o4wy4G/Dh4mGXjur5sh1LY2QOo4CbxQQSTTvCCARnXkDZANXxLM5zQDCcUN5Fy43p+0/WlQhoEnI9fsFzIUhEUI/NaR4R0ObouKa3vX+kuPjdALJBrWgGbu5CosSVwEu9i3V1eU725xWL5W7E/otmgT5efsDJOhyHaMnE9k6veu5gw2e1V5k+x/MqtgG3l6EfWINnovT4UGojPNQsajE6BLYpwpzXBu4pH2elUznp30xPUbLJXgHmGEihTFAKKoh4yy2Sy6+46jf",
            RSA_INVALID_PUBLIC_KEY_DATA = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC6V9nUuUdixTUtKDBcaATknmM3d2JI+j2jaMiirgdrnRjAAfMnAD+dbnokmKfFOKrUhgVhMn/YGyXh2IyEsxb+mFj5S9qqKCqfRNp1fomzP9r5HoGnHut3ZwHQPz0ZVqkbGGulO4vJzUxhdNp7usRcbZyuuoRkvlLW5B1/IzTW2QBRdtevd3ySOzkR6yDBsN0sB+U4rW0wuBYxq2Dvs9F01QnjZIsyB32Z9sHGOf49Qj2SVUS4/Sx86rd1V6Yly5Gn4jlum6dzMHSXa66SX7QtVEOR7aKbHSV1wmZSW9FYJyR3enDJg0YtBwBbprFacoE/KeTyOQBBe4nOLhFjkMbDgUasdCGRtDDP+00os6DZB8YRNg+lURkSdz01vSQtMH5yLQOSSIjkCTSL2IemCM0KpE2J2K/gn4QdctYitcbMJeWnoYG1SRo8sH1mdHSiuUK7GB+DrXorAYRiWqHMP98t4NWaiFFgPwRyihXkoHIvcLCDYzFQm8WqT2DWvQVkRaCcpWgd39V7cqHLWrUY53/vbSZEC+ndlpQI64x+DfxLQ3b9S2Hy/YZON00aEE/VxAVo0iMQN4GcWrrkoTwi3f4XIfCcZb8ySTXkrliQHIymU3NimSW+696/BtLKT/nVsUDPLsfWlaGALBn84N6Hvt9Oh93QXbpqyJPhRo2U9uRx+w== d.jensen@tecnoteca.com";

    @Test
    public void testRsaPrivateKeyLoading() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);
        logger.debug("private key = {}", privateKey);
    }

    @Test
    public void testRsaPublicKeyLoading1() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        logger.debug("public key = {}", publicKey);
    }

    @Test
    public void testRsaPublicKeyLoading2() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_2);
        logger.debug("public key = {}", publicKey);
    }

    @Test
    public void testRsaPublicKeyVerification1() {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String challenge = encodeBytes("asfsevc5et4wg14t2v5g346ygve".getBytes());
        logger.debug("challenge = {}", challenge);
        String response = createChallengeResponse(challenge, privateKey);
        logger.debug("response = {}", response);
        assertTrue(verifySignedChallenge(challenge, response, publicKey));
    }

    @Test
    public void testRsaPublicKeyVerification2() {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String challenge = randomId();
        logger.debug("challenge = {}", challenge);
        String response = createChallengeResponse(challenge, privateKey);
        logger.debug("response = {}", response);
        assertTrue(verifySignedChallenge(challenge, response, publicKey));
    }

    @Test
    public void testRsaPublicKeyVerification3() {
        PublicKey publicKey = parsePublicKey(RSA_INVALID_PUBLIC_KEY_DATA);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String challenge = randomId();
        logger.debug("challenge = {}", challenge);
        String response = createChallengeResponse(challenge, privateKey);
        logger.debug("response = {}", response);
        assertFalse(verifySignedChallenge(challenge, response, publicKey));
    }

    @Test
    public void testTokenVerification1() {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String token = createToken(privateKey);
        logger.debug("token = {}", token);
        assertTrue(verifyToken(token, publicKey));
    }

    @Test
    public void testTokenVerification2() {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_2);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String token = createToken(privateKey);
        logger.debug("token = {}", token);
        assertTrue(verifyToken(token, publicKey));
    }

    @Test(expected = Exception.class)
    public void testTokenVerificationFailWithDifferentKeySize() {
        PublicKey publicKey = parsePublicKey(RSA_INVALID_PUBLIC_KEY_DATA);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String token = createToken(privateKey);
        logger.debug("token = {}", token);
        assertFalse(verifyToken(token, publicKey));
        verifyToken(token, publicKey);
        fail();
    }

    @Test
    public void rsaSignatureTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, DecoderException {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update("test".getBytes());
        String sig = Hex.encodeHexString(signature.sign());

        signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update("test".getBytes());
        assertTrue(signature.verify(Hex.decodeHex(sig)));

    }

}
