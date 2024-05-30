package org.kbl.dialogflow;

/**
 * @Description
 * @Author liubingke
 * @Date 2024/1/4
 */

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.dialogflow.cx.v3.AudioEncoding;
import com.google.cloud.dialogflow.cx.v3.AudioInput;
import com.google.cloud.dialogflow.cx.v3.DetectIntentRequest;
import com.google.cloud.dialogflow.cx.v3.DetectIntentResponse;
import com.google.cloud.dialogflow.cx.v3.InputAudioConfig;
import com.google.cloud.dialogflow.cx.v3.QueryInput;
import com.google.cloud.dialogflow.cx.v3.QueryResult;
import com.google.cloud.dialogflow.cx.v3.SessionName;
import com.google.cloud.dialogflow.cx.v3.SessionsClient;
import com.google.cloud.dialogflow.cx.v3.SessionsSettings;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;

public class DetectIntentAudioInput {

    // DialogFlow API Detect Intent sample with Audio input.
//    public static void main(String[] args) throws IOException, ApiException {
//        /** TODO (developer): replace these values with your own values */
//        String projectId = "grushome-ai";
//        String locationId = "global";
//        String agentId = "dbc02239-2096-4479-b15a-1bbb261df168";
//        String audioFileName = "C:\\Users\\liubingke\\Downloads\\Music\\沙河街1-5号.m4a";
//        int sampleRateHertz = 16000;
//        /*
//         * A session ID is a string of at most 36 bytes in size.
//         * Your system is responsible for generating unique session IDs.
//         * They can be random numbers, hashed end-user identifiers,
//         * or any other values that are convenient for you to generate.
//         */
//        String sessionId = "my-UUID";
//        String languageCode = "zh-CN";
//
//        detectIntent(
//                projectId, locationId, agentId, audioFileName, sampleRateHertz, sessionId, languageCode);
//    }

    public static void detectIntent(
            String projectId,
            String locationId,
            String agentId,
            String audioFileName,
            int sampleRateHertz,
            String sessionId,
            String languageCode)
            throws IOException, ApiException {

        SessionsSettings.Builder sessionsSettingsBuilder = SessionsSettings.newBuilder();
        if (locationId.equals("global")) {
            sessionsSettingsBuilder.setEndpoint("dialogflow.googleapis.com:443");
        } else {
            sessionsSettingsBuilder.setEndpoint(locationId + "-dialogflow.googleapis.com:443");
        }
        SessionsSettings sessionsSettings = sessionsSettingsBuilder.build();

        // Instantiates a client by setting the session name.
        // Format:`projects/<ProjectID>/locations/<LocationID>/agents/<AgentID>/sessions/<SessionID>`

        // Note: close() needs to be called on the SessionsClient object to clean up resources
        // such as threads. In the example below, try-with-resources is used,
        // which automatically calls close().
        try (SessionsClient sessionsClient = SessionsClient.create(sessionsSettings)) {
            SessionName session =
                    SessionName.ofProjectLocationAgentSessionName(projectId, locationId, agentId, sessionId);

            // TODO : Uncomment if you want to print session path
            // System.out.println("Session Path: " + session.toString());
            InputAudioConfig inputAudioConfig =
                    InputAudioConfig.newBuilder()
                            .setAudioEncoding(AudioEncoding.AUDIO_ENCODING_LINEAR_16)
                            .setSampleRateHertz(sampleRateHertz)
                            .build();

            try (FileInputStream audioStream = new FileInputStream(audioFileName)) {
                // Subsequent requests must **only** contain the audio data.
                // Following messages: audio chunks. We just read the file in fixed-size chunks. In reality
                // you would split the user input by time.
                byte[] buffer = new byte[4096];
                int bytes = audioStream.read(buffer);
                AudioInput audioInput =
                        AudioInput.newBuilder()
                                .setAudio(ByteString.copyFrom(buffer, 0, bytes))
                                .setConfig(inputAudioConfig)
                                .build();
                QueryInput queryInput =
                        QueryInput.newBuilder()
                                .setAudio(audioInput)
                                .setLanguageCode("zh-CN") // languageCode = "en-US"
                                .build();

                DetectIntentRequest request =
                        DetectIntentRequest.newBuilder()
                                .setSession(session.toString())
                                .setQueryInput(queryInput)
                                .build();

                // Performs the detect intent request.
                DetectIntentResponse response = sessionsClient.detectIntent(request);

                // Display the query result.
                QueryResult queryResult = response.getQueryResult();
                System.out.println(queryResult);
                System.out.println("====================");
                System.out.format(
                        "Detected Intent: %s (confidence: %f)\n",
                        queryResult.getTranscript(), queryResult.getIntentDetectionConfidence());
            }
        }
    }

}