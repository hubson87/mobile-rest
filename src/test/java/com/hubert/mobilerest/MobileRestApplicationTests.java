package com.hubert.mobilerest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MobileRestApplicationTests {

    @Test
    public void contextLoadsTest() {
    }

    @Test
    public void springBootMainNoExceptionTest()
    {
        MobileRestApplication.main(new String[]{
                "--spring.main.web-environment=false"
        });
    }
}
