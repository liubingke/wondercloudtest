package org.kbl.bigquery;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.*;
import org.kbl.bigquery.model.BillingInfo;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.UUID;

public class SimpleApp {
    public static void main(String... args) throws Exception {
        //BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
        // 指定服务账号密钥文件路径
        String serviceAccountKeyFile = "D:\\ADC\\wdtest001.json";

        // 使用服务账号密钥文件进行认证
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFile));

        // 创建 BigQuery 实例
        BigQuery bigquery = BigQueryOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(
                                "SELECT billing_account_name,billing_acount_id,project_name,project_id,project_ancestors,labels,service_description,service_id,ski_description,sku_id,credits_type,cost_type,usage_start_time,usage_end_time,usage_amount,usage_amount_units,cost,credits_amount FROM `wdtest-001.testbq.testtable` LIMIT 1000")
                        // Use standard SQL syntax for queries.
                        // See: https://cloud.google.com/bigquery/sql-reference/
                        .setUseLegacySql(false)
                        .build();

        // Create a job ID so that we can safely retry.
        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

        // Wait for the query to complete.
        queryJob = queryJob.waitFor();

        // Check for errors
        if (queryJob == null) {
            throw new RuntimeException("Job no longer exists");
        } else if (queryJob.getStatus().getError() != null) {
            // You can also look at queryJob.getStatus().getExecutionErrors() for all
            // errors, not just the latest one.
            throw new RuntimeException(queryJob.getStatus().getError().toString());
        }

        // Get the results.
        TableResult result = queryJob.getQueryResults();

        ArrayList<BillingInfo> list = new ArrayList<>();

        // Print each row
        for (FieldValueList row : result.iterateAll()) {
            BillingInfo info = new BillingInfo();
            String billingAccountName = row.get("billing_account_name").getStringValue();
            String billingAccountId = row.get("billing_acount_id").getStringValue();
            String projectName = (String) row.get("project_name").getValue();
            String projectId = (String) row.get("project_id").getValue();

            String projectAncestors = row.get("project_ancestors").getStringValue();
            String labels = (String) row.get("labels").getValue();
            String serviceDescription = (String) row.get("service_description").getValue();
            String serviceId = (String) row.get("service_id").getValue();
            String skiDescription = (String) row.get("ski_description").getValue();
            String skuId = (String) row.get("sku_id").getValue();
            String creditsType = (String) row.get("credits_type").getValue();
            String costType = (String) row.get("cost_type").getValue();
            String usageStartTime = (String) row.get("usage_start_time").getValue();
            String usageEndTime = (String) row.get("usage_end_time").getValue();
            double usageAmount = row.get("usage_amount").getDoubleValue();
            String usageAmountUnits = row.get("usage_amount_units").getStringValue();
            double cost = row.get("cost").getDoubleValue();
            double creditsAmount = row.get("credits_amount").getDoubleValue();

            info.setBillingAccountName(billingAccountName);
            info.setBillingAccountId(billingAccountId);
            info.setProjectName(projectName);
            info.setProjectId(projectId);
            info.setProjectAncestors(projectAncestors);
            info.setLabels(labels);
            info.setServiceDescription(serviceDescription);
            info.setServiceId(serviceId);
            info.setSkiDescription(skiDescription);
            info.setSkuId(skuId);
            info.setCreditsType(creditsType);
            info.setCostType(costType);
            info.setUsageStartTime(usageStartTime);
            info.setUsageEndTime(usageEndTime);
            info.setUsageAmount(usageAmount);
            info.setUsageAmountUnits(usageAmountUnits);
            info.setCost(cost);
            info.setCreditsAmount(creditsAmount);
            list.add(info);
            info=null;

        }
        for (BillingInfo info : list) {
            System.out.println(info);
        }
    }
}
