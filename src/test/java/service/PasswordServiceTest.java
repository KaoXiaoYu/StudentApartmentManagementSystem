package service;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PasswordServiceTest {
    @Test
    public void hashesAndVerifiesPassword() {
        String hash = PasswordService.hash("123456");

        assertNotEquals("123456", hash);
        assertTrue(PasswordService.matches("123456", hash));
        assertFalse(PasswordService.matches("wrong", hash));
    }

    @Test
    public void acceptsLegacyPlaintextForAutomaticMigration() {
        assertTrue(PasswordService.matches("123456", "123456"));
        assertFalse(PasswordService.matches("wrong", "123456"));
    }
}
