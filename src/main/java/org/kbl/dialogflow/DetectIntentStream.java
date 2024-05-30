package org.kbl.dialogflow;

/**
 * @Description
 * @Author liubingke
 * @Date 2024/1/4
 */

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.BidiStream;
import com.google.cloud.dialogflow.cx.v3beta1.AudioEncoding;
import com.google.cloud.dialogflow.cx.v3beta1.AudioInput;
import com.google.cloud.dialogflow.cx.v3beta1.InputAudioConfig;
import com.google.cloud.dialogflow.cx.v3beta1.OutputAudioConfig;
import com.google.cloud.dialogflow.cx.v3beta1.OutputAudioEncoding;
import com.google.cloud.dialogflow.cx.v3beta1.QueryInput;
import com.google.cloud.dialogflow.cx.v3beta1.QueryResult;
import com.google.cloud.dialogflow.cx.v3beta1.SessionName;
import com.google.cloud.dialogflow.cx.v3beta1.SessionsClient;
import com.google.cloud.dialogflow.cx.v3beta1.SessionsSettings;
import com.google.cloud.dialogflow.cx.v3beta1.SsmlVoiceGender;
import com.google.cloud.dialogflow.cx.v3beta1.StreamingDetectIntentRequest;
import com.google.cloud.dialogflow.cx.v3beta1.StreamingDetectIntentResponse;
import com.google.cloud.dialogflow.cx.v3beta1.SynthesizeSpeechConfig;
import com.google.cloud.dialogflow.cx.v3beta1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class DetectIntentStream {

    // DialogFlow API Detect Intent sample with audio files processes as an audio stream.
    public static void detectIntentStream(
            String projectId, String locationId, String agentId, String sessionId, String audioFilePath)
            throws ApiException, IOException {
        SessionsSettings.Builder sessionsSettingsBuilder = SessionsSettings.newBuilder();
        if ("global".equals(locationId)) {
            sessionsSettingsBuilder.setEndpoint("dialogflow.googleapis.com:443");
        } else {
            sessionsSettingsBuilder.setEndpoint(locationId + "-dialogflow.googleapis.com:443");
        }
        SessionsSettings sessionsSettings = sessionsSettingsBuilder.build();

        // Instantiates a client by setting the session name.
        // Format: `projects/<ProjectID>/locations/<LocationID>/agents/<AgentID>/sessions/<SessionID>`
        // Using the same `sessionId` between requests allows continuation of the conversation.

        // Note: close() needs to be called on the SessionsClient object to clean up resources
        // such as threads. In the example below, try-with-resources is used,
        // which automatically calls close().
        try (SessionsClient sessionsClient = SessionsClient.create(sessionsSettings)) {
            SessionName session = SessionName.of(projectId, locationId, agentId, sessionId);

            // Instructs the speech recognizer how to process the audio content.
            // Note: hard coding audioEncoding and sampleRateHertz for simplicity.
            // Audio encoding of the audio content sent in the query request.
            InputAudioConfig inputAudioConfig =
                    InputAudioConfig.newBuilder()
                            .setAudioEncoding(AudioEncoding.AUDIO_ENCODING_LINEAR_16)
                            .setSampleRateHertz(16000) // sampleRateHertz = 16000
                            .build();

            // Build the AudioInput with the InputAudioConfig.
            AudioInput audioInput = AudioInput.newBuilder().setConfig(inputAudioConfig).build();

            // Build the query with the InputAudioConfig.
            QueryInput queryInput =
                    QueryInput.newBuilder()
                            .setAudio(audioInput)
                            .setLanguageCode("zh-CN") // languageCode = "en-US"
                            .build();

            // Create the Bidirectional stream
            BidiStream<StreamingDetectIntentRequest, StreamingDetectIntentResponse> bidiStream =
                    sessionsClient.streamingDetectIntentCallable().call();

            // Specify sssml name and gender
            VoiceSelectionParams voiceSelection =
                    // Voices that are available https://cloud.google.com/text-to-speech/docs/voices
                    VoiceSelectionParams.newBuilder()
                            .setName("en-US-Standard-F")
                            .setSsmlGender(SsmlVoiceGender.SSML_VOICE_GENDER_FEMALE)
                            .build();

            SynthesizeSpeechConfig speechConfig =
                    SynthesizeSpeechConfig.newBuilder().setVoice(voiceSelection).build();

            // Setup audio config
            OutputAudioConfig audioConfig =
                    // Output enconding explanation
                    // https://cloud.google.com/dialogflow/cx/docs/reference/rpc/google.cloud.dialogflow.cx.v3#outputaudioencoding
                    OutputAudioConfig.newBuilder()
                            .setAudioEncoding(OutputAudioEncoding.OUTPUT_AUDIO_ENCODING_UNSPECIFIED)
                            .setAudioEncodingValue(1)
                            .setSynthesizeSpeechConfig(speechConfig)
                            .build();

            // The first request must **only** contain the audio configuration:
            bidiStream.send(
                    StreamingDetectIntentRequest.newBuilder()
                            .setSession(session.toString())
                            .setQueryInput(queryInput)
                            .setOutputAudioConfig(audioConfig)
                            .build());

            try (FileInputStream audioStream = new FileInputStream(audioFilePath)) {
                // Subsequent requests must **only** contain the audio data.
                // Following messages: audio chunks. We just read the file in fixed-size chunks. In reality
                // you would split the user input by time.
                byte[] buffer = new byte[4096];
                int bytes;
                while ((bytes = audioStream.read(buffer)) != -1) {
                    AudioInput subAudioInput =
                            AudioInput.newBuilder().setAudio(ByteString.copyFrom(buffer, 0, bytes)).build();
                    QueryInput subQueryInput =
                            QueryInput.newBuilder()
                                    .setAudio(subAudioInput)
                                    .setLanguageCode("zh-CN") // languageCode = "en-US"
                                    .build();
                    bidiStream.send(
                            StreamingDetectIntentRequest.newBuilder().setQueryInput(subQueryInput).build());
                }
            }

            // Tell the service you are done sending data.
            bidiStream.closeSend();

            for (StreamingDetectIntentResponse response : bidiStream) {
                QueryResult queryResult = response.getDetectIntentResponse().getQueryResult();
                System.out.println("====================");
                System.out.format("Query Text: '%s'\n", queryResult.getTranscript());
                System.out.format(
                        "Detected Intent: %s (confidence: %f)\n",
                        queryResult.getMatch().getIntent().getDisplayName(),
                        queryResult.getMatch().getConfidence());
            }
        }
    }

//    public static void main(String[] args) {
//        try {
//            detectIntentStream("grushome-ai", "global", "dbc02239-2096-4479-b15a-1bbb261df168", "sessionId2","C:\\Users\\liubingke\\Downloads\\Music\\沙河街1-5号.m4a");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
