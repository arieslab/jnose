package io.github.arieslab.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GitCoreTest {

    @Test
    void testGetStartsDoesNotThrowOnInvalidOrLocalPath() {
        Integer stars = assertDoesNotThrow(() -> GitCore.getStarts("/tmp/nonexistent-path"));
        assertEquals(0, stars);
    }

    @Test
    void testGetNameByGithub() {
        assertEquals("arieslab/jnose", GitCore.getNameByGithub("https://github.com/arieslab/jnose.git"));
        assertEquals("arieslab/jnose", GitCore.getNameByGithub("https://github.com/arieslab/jnose/"));
        assertEquals("arieslab/jnose", GitCore.getNameByGithub("git@github.com:arieslab/jnose.git"));
        assertEquals("", GitCore.getNameByGithub(""));
        assertEquals("", GitCore.getNameByGithub(null));
        assertEquals("", GitCore.getNameByGithub("invalid"));
    }
}
