package com.bol.mancala;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MancalaEntityGameApplicationTests {
    @Test
    void main() {
        MancalaApplication.main(new String[]{});
        Assertions.assertTrue(true);
    }
}
