package DigitalSignature;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.*;

/**
 * Created by Siddharth on 29-06-2015.
 */
public class sign {
    public static String filePath;
    public void signit(String temp) {

        filePath=temp;
        //generate checksum using SHA1
        byte[] checksum=new byte[20];
        try {
             checksum= createSha1(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }


        temp= Base64.encode(checksum);
        System.out.println("Checksum base64 encoded " + temp);
        for(int i=0;i<checksum.length;i++)
            System.out.print(checksum[i]);

        //encrypt using DSA
        byte[]signature=encrypt(checksum);

        temp= Base64.encode(signature);
        System.out.println();
        System.out.println("Signature base64 encoded " + temp);
        for(int i=0;i<signature.length;i++)
            System.out.print(signature[i]);

    }

    private static byte[] encrypt(byte[]checksum) {

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
            keyGen.initialize(1024, new SecureRandom());
            KeyPair kp = keyGen.generateKeyPair();
            PrivateKey prKey = kp.getPrivate();
            PublicKey puKey = kp.getPublic();
            Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
            sig.initSign(prKey);
            sig.update(checksum);
            byte[] signature = sig.sign();

            File t=new File(filePath);
            FileOutputStream f=new FileOutputStream(new File(t.getParent()+"s.sig"));
            f.write(signature);
            f.close();
            f=new FileOutputStream(new File(t.getParent()+"public.sig"));
            byte[]temp=puKey.getEncoded();
            f.write(temp);
            f.close();
            return signature;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] createSha1(File file) throws Exception  {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        InputStream fis = new FileInputStream(file);
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            n = fis.read(buffer);
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return digest.digest();
    }
}
