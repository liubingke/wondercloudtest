package org.kbl.documentai;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;

public class OcrInvoiceUtil {

    private static final Logger LOGGER = Logger.getLogger(OcrInvoiceUtil.class.getName());

    private static final String PROJECT_ID = "wdtest-001"; // Replace with your project ID
    private static final String LOCATION = "us"; // Replace with your location
    private static final String PROCESSOR_ID = "5313df594ec7470f"; // Replace with your processor ID
    private static final String CREDENTIALS_FILE_PATH = "D:\\ADC\\wdtest001.json"; // Replace with the path to your service account key file

    // Use WeakReference to avoid keeping strong references to the client
    private static WeakReference<DocumentProcessorServiceClient> documentProcessorServiceClientRef = new WeakReference<>(null);

    public static List<Document.Entity> processDocument(byte[] imageFileData) throws IOException {
        // Initialize the client only if it's not already present
        DocumentProcessorServiceClient documentProcessorServiceClient = documentProcessorServiceClientRef.get();
        if (documentProcessorServiceClient == null) {
            documentProcessorServiceClient = initClient();
            // Set the new client to the WeakReference
            documentProcessorServiceClientRef = new WeakReference<>(documentProcessorServiceClient);
        }

        try {
            String name = String.format("projects/%s/locations/%s/processors/%s", PROJECT_ID, LOCATION, PROCESSOR_ID);

            // Convert the image data to a Buffer and base64 encode it.
            ByteString content = ByteString.copyFrom(imageFileData);

            RawDocument document = RawDocument.newBuilder()
                    .setContent(content)
                    .setMimeType("image/jpeg")
                    .build();

            // Configure the process request.
            ProcessRequest request = ProcessRequest.newBuilder()
                    .setName(name)
                    .setRawDocument(document)
                    .build();

            // Process the document using the specified processor
            ProcessResponse result = documentProcessorServiceClient.processDocument(request);
            Document documentResponse = result.getDocument();

            // Extract information from the processed document
            List<Document.Entity> entities = documentResponse.getEntitiesList();
            String text = documentResponse.getText();
            LOGGER.log(Level.INFO, "Original Document Text: {0}", text);
            String convertText = convertCurrencySymbols(text);
            LOGGER.log(Level.INFO, "Converted Document Text: {0}", convertText);

            // 创建新的实体列表并更新内容
            List<Document.Entity> updatedEntities = new ArrayList<>();
            for (Document.Entity entity : entities) {
                String entityText = entity.getMentionText();
                String convertedEntityText = convertCurrencySymbols(entityText);
                entity = entity.toBuilder().setMentionText(convertedEntityText).build();
                updatedEntities.add(entity);
            }

            Document convertDocument = documentResponse.toBuilder().setText(convertText).clearEntities().addAllEntities(updatedEntities).build();
            LOGGER.log(Level.INFO, "Updated Document: {0}", convertDocument);
            for (Document.Entity entity : convertDocument.getEntitiesList()) {
                LOGGER.log(Level.INFO, "Entity Mention Text: {0}", entity.getMentionText());
            }

            return updatedEntities;

        } finally {
            // Ensure the client is closed when you're done using it
            // You can also close the client here instead of in a finally block if
            // you want to manually control when the client is closed
            documentProcessorServiceClient.close();
        }
    }

    private static DocumentProcessorServiceClient initClient() throws IOException {
        Credentials credentials = ServiceAccountCredentials.fromStream(
                new FileInputStream(CREDENTIALS_FILE_PATH)).createScoped(ImmutableList.of("https://www.googleapis.com/auth/cloud-platform"));

        String endpoint = String.format("%s-documentai.googleapis.com:443", LOCATION);
        DocumentProcessorServiceSettings settings = DocumentProcessorServiceSettings.newBuilder()
                .setEndpoint(endpoint)
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
        return DocumentProcessorServiceClient.create(settings);
    }

    private static String convertCurrencySymbols(String text) {
        // 使用正则表达式将 $ 符号替换为 USD
        Pattern pattern = Pattern.compile("\\$");
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll("USD");
    }

    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\liubingke\\Downloads\\test.jpg";
        byte[] imageFileData = Files.readAllBytes(Paths.get(filePath));
        processDocument(imageFileData);
    }
}
