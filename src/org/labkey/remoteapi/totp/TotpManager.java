package org.labkey.remoteapi.totp;

public class TotpManager
{
    // Generate a TOTP one-time password based on the default parameters (30 second time step, 6 digits, SHA1)
    public static String generateCode(String secretKey) throws CodeGenerationException
    {
        return generateCode(secretKey, 30, 6, HashingAlgorithm.SHA1);
    }

    // Generate a TOTP one-time password using custom parameters
    public static String generateCode(String secretKey, int timeStep, int digits, HashingAlgorithm hashingAlgorithm) throws CodeGenerationException
    {
        TimeProvider timeProvider = new TimeProvider();
        long currentBucket = Math.floorDiv(timeProvider.getTime(), timeStep);
        CodeGenerator codeGenerator = new CodeGenerator(hashingAlgorithm, digits);

        return codeGenerator.generate(secretKey, currentBucket);
    }
}
