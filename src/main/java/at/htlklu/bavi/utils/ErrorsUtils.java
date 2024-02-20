package at.htlklu.bavi.utils;

public class ErrorsUtils
{
    public static String getErrorMessage(Exception e)
    {
        return e.getCause().getCause().getLocalizedMessage();
    }
}
