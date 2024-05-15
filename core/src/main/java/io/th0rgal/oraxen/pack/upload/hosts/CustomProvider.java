package io.th0rgal.oraxen.pack.upload.hosts;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.th0rgal.oraxen.config.Settings;
import io.th0rgal.oraxen.utils.logs.Logs;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CustomProvider implements HostingProvider {

    private final String serverAddress;
    private String packUrl;
    private String minecraftPackURL;
    private String sha1;

    public CustomProvider(String serverAddress) {
        this.serverAddress = (serverAddress.startsWith("http://") || serverAddress.startsWith("https://") ? "" : "https://") + serverAddress + (serverAddress.endsWith("/") ? "" : "/");
    }

    @Override
    public boolean uploadPack(File resourcePack) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(serverAddress + "api/upload");

            HttpEntity httpEntity = MultipartEntityBuilder
                    .create()
                    .addTextBody("secret", Settings.CUSTOM_SECRET.toString())
                    .addBinaryBody("file", resourcePack)
                    .build();

            request.setEntity(httpEntity);

            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            JsonObject jsonOutput;
            try {
                jsonOutput = JsonParser.parseString(responseString).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                Logs.logError("Error uploading pack");
                e.printStackTrace();
                return false;
            }
            if (jsonOutput.has("url") && jsonOutput.has("hash")) {
                packUrl = jsonOutput.get("url").getAsString();
                minecraftPackURL = packUrl.replace("https://", "http://");
                sha1 = jsonOutput.get("hash").getAsString();
                return true;
            }

            if (jsonOutput.has("error"))
                Logs.logError("Error: " + jsonOutput.get("error").getAsString());
            Logs.logError("Response: " + jsonOutput);
            Logs.logError("The resource pack has not been uploaded to the server. Usually this is due to an excessive size.");
            return false;
        } catch(IllegalStateException | IOException ex) {
            Logs.logError("The resource pack has not been uploaded to the server. Usually this is due to an excessive size.");
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public String getPackURL() {
        return packUrl;
    }

    @Override
    public String getMinecraftPackURL() {
        return minecraftPackURL;
    }

    @Override
    public byte[] getSHA1() {
        int len = sha1.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(sha1.charAt(i), 16) << 4)
                    + Character.digit(sha1.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public String getOriginalSHA1() {
        return sha1;
    }

    @Override
    public UUID getPackUUID() {
        return null;
    }

}
