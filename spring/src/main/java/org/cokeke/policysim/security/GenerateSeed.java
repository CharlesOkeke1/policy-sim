package org.cokeke.policysim.security;
import org.springframework.stereotype.Service;

@Service 
public class GenerateSeed {
    public GenerateSeed() {}

    public static int getRandomSeed() {
        int rand = (int) (Math.random()*1000);
        return rand;
    }
}
