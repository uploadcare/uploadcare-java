import com.uploadcare.api.Client;
import com.uploadcare.api.File;

public class FilesListing {

    public static void main(String[] args) {
        Client client = Client.demoClient();

        int index = 0;
        for (File file : client.getFiles().asIterable()) {
            System.out.print((++index) + ": " + file.getFileId());
            System.out.println(file.isStored() ? " (stored)" : "");
        }
    }

}
