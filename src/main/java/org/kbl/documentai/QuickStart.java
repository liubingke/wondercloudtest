package org.kbl.documentai;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class QuickStart {
    public static void main(String[] args)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // TODO(developer): Replace these variables before running the sample.
        String projectId = "wdtest-001";
        String location = "us"; // Format is "us" or "eu".
        String processorId = "887c36e535c8a144";
        String filePath = "C:\\Users\\liubingke\\Downloads\\【云计算销售经理_深圳】吴佳俊 1年.pdf";
        quickStart(projectId, location, processorId, filePath);
    }

    public static void quickStart(
            String projectId, String location, String processorId, String filePath)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // Initialize client that will be used to send requests. This client only needs
        // to be created
        // once, and can be reused for multiple requests. After completing all of your
        // requests, call
        // the "close" method on the client to safely clean up any remaining background
        // resources.
        String endpoint = String.format("%s-documentai.googleapis.com:443", location);
        String keyPath = "D:\\ADC\\wdtest-001.json";
        Credentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(keyPath));
        DocumentProcessorServiceSettings settings =
                DocumentProcessorServiceSettings.newBuilder().setCredentialsProvider(()->credentials).setEndpoint(endpoint).build();
        try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create(settings)) {
            // The full resource name of the processor, e.g.:
            // projects/project-id/locations/location/processor/processor-id
            // You must create new processors in the Cloud Console first
            String name =
                    String.format("projects/%s/locations/%s/processors/%s", projectId, location, processorId);

            // Read the file.
            byte[] imageFileData = Files.readAllBytes(Paths.get(filePath));

            // Convert the image data to a Buffer and base64 encode it.
            ByteString content = ByteString.copyFrom(imageFileData);

            RawDocument document =
                    RawDocument.newBuilder().setContent(content).setMimeType("application/pdf").build();

            // Configure the process request.
            ProcessRequest request =
                    ProcessRequest.newBuilder().setName(name).setRawDocument(document).build();

            // Recognizes text entities in the PDF document
            ProcessResponse result = client.processDocument(request);
            Document documentResponse = result.getDocument();

            // Get all of the document text as one big string
            String text = documentResponse.getText();

            // Read the text recognition output from the processor
            System.out.println("The document contains the following paragraphs:");
            Document.Page firstPage = documentResponse.getPages(0);
            List<Document.Page.Paragraph> paragraphs = firstPage.getParagraphsList();

            for (Document.Page.Paragraph paragraph : paragraphs) {
                String paragraphText = getText(paragraph.getLayout().getTextAnchor(), text);
                System.out.printf("Paragraph text:\n%s\n", paragraphText);
            }
        }
    }

    // Extract shards from the text field
    private static String getText(Document.TextAnchor textAnchor, String text) {
        if (textAnchor.getTextSegmentsList().size() > 0) {
            int startIdx = (int) textAnchor.getTextSegments(0).getStartIndex();
            int endIdx = (int) textAnchor.getTextSegments(0).getEndIndex();
            return text.substring(startIdx, endIdx);
        }
        return "[NO TEXT]";
    }
}