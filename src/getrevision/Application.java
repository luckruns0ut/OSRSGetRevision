package getrevision;

import java.io.File;
import java.nio.file.Files;

/**
 * Created by luckruns0ut on 24-Nov-16.
 */
public class Application {
    private static final int SIPUSH_OPCODE = 0x11;

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.out.println("Usage: getrevision myclass.class");
                return;
            }
            System.out.println("Revision of pack: " + getRevision(Files.readAllBytes(new File(args[0]).toPath())));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static int getRevision(byte[] client) {
        int wid = 765;
        int hei = 503;
        byte[] pattern = {
                SIPUSH_OPCODE, (byte)(wid >> 8), (byte)(wid & 0xff),
                SIPUSH_OPCODE, (byte)(hei >> 8), (byte)(hei & 0xff)
        };

        for(int i = 0; i < client.length - pattern.length; i++) {
            boolean found = true;
            for(int j = 0; j < pattern.length; j++) {
                if(client[i+j] != pattern[j]) {
                    found = false;
                    break;
                }
            }
            if(found) {
                // revision should be at the end of the pattern
                int idx = pattern.length + i;
                if(client[idx] == 0x11) { // if the revision is written as a short
                    return (client[idx+1] >> 8) + (client[idx + 2] & 0xff);
                } else if(client[idx] == 0x10){ // or if it is written as a byte
                    return client[idx+1] & 0xff;
                }
            }
        }
        return 0;
    }
}
