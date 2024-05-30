package org.kbl.billing;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.billing.v1.CloudBillingClient;
import com.google.cloud.billing.v1.CloudBillingSettings;
import com.google.cloud.billing.v1.ProjectBillingInfo;

import java.io.FileInputStream;
import java.io.IOException;

public class BillingDataFetcher {

    public static void main(String[] args) throws IOException {
        // 使用服务账户密钥认证
        CredentialsProvider credentialsProvider = () -> GoogleCredentials.fromStream(
                new FileInputStream("D:\\ADC\\wdtest001.json"));

        // 设置 Cloud Billing 客户端
        CloudBillingSettings settings = CloudBillingSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
        try (CloudBillingClient client = CloudBillingClient.create(settings)) {
            // 指定项目 ID
            String projectId = "wdtest-001";

            // 获取项目的账单信息
            ProjectBillingInfo billingInfo = client.getProjectBillingInfo("projects/" + projectId);

            // 打印账单信息
            System.out.println("Billing Account Name: " + billingInfo.getBillingAccountName());
            System.out.println("Billing Enabled: " + billingInfo.getBillingEnabled());
            System.out.println();
            System.out.println(billingInfo);
        }
    }
}
