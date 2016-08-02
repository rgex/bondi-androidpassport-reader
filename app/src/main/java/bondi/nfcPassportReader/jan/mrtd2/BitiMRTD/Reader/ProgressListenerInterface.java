package bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Reader;

public interface ProgressListenerInterface
{
    public void updateProgress(int progress);

    public boolean isCanceled();

    public void cancel();
}
