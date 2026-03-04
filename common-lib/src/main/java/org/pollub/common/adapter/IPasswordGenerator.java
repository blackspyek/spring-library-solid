package org.pollub.common.adapter;

//start L2 Adapter interface
/**
 * Adapter interface for password generation abstraction.
 * Allows switching between different password generation strategies.
 */
public interface IPasswordGenerator {
    /**
     * Generates a random password.
     * @return generated password
     */
    String generate();
}
//end L2 Adapter interface