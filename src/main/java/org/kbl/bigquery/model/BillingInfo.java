package org.kbl.bigquery.model;

import com.google.cloud.bigquery.FieldValue;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Description
 * @Author liubingke
 * @Date 2024/5/29
 */
public class BillingInfo {

    private String billingAccountName;

    private String billingAccountId;

    private String projectName;

    private String projectId;

    private String projectAncestors;

    private String labels;


    private String serviceDescription;


    private String serviceId;

    private String skiDescription;

    private String skuId;


    private String creditsType;

    private String costType;


    private String usageStartTime;

    private String usageEndTime;


    private Double usageAmount;

    private String usageAmountUnits;

    private Double cost;


    private Double creditsAmount;

    public String getBillingAccountName() {
        return billingAccountName;
    }

    public void setBillingAccountName(String billingAccountName) {
        this.billingAccountName = billingAccountName;
    }

    public String getBillingAccountId() {
        return billingAccountId;
    }

    public void setBillingAccountId(String billingAccountId) {
        this.billingAccountId = billingAccountId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectAncestors() {
        return projectAncestors;
    }

    public void setProjectAncestors(String projectAncestors) {
        this.projectAncestors = projectAncestors;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSkiDescription() {
        return skiDescription;
    }

    public void setSkiDescription(String skiDescription) {
        this.skiDescription = skiDescription;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getCreditsType() {
        return creditsType;
    }

    public void setCreditsType(String creditsType) {
        this.creditsType = creditsType;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getUsageStartTime() {
        return usageStartTime;
    }

    public void setUsageStartTime(String usageStartTime) {
        this.usageStartTime = usageStartTime;
    }

    public String getUsageEndTime() {
        return usageEndTime;
    }

    public void setUsageEndTime(String usageEndTime) {
        this.usageEndTime = usageEndTime;
    }

    public Double getUsageAmount() {
        return usageAmount;
    }

    public void setUsageAmount(Double usageAmount) {
        this.usageAmount = usageAmount;
    }

    public String getUsageAmountUnits() {
        return usageAmountUnits;
    }

    public void setUsageAmountUnits(String usageAmountUnits) {
        this.usageAmountUnits = usageAmountUnits;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getCreditsAmount() {
        return creditsAmount;
    }

    public void setCreditsAmount(Double creditsAmount) {
        this.creditsAmount = creditsAmount;
    }

    @Override
    public String toString() {
        return "BillingInfo{" +
                "billingAccountName='" + billingAccountName + '\'' +
                ", billingAccountId='" + billingAccountId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectId='" + projectId + '\'' +
                ", projectAncestors='" + projectAncestors + '\'' +
                ", labels='" + labels + '\'' +
                ", serviceDescription='" + serviceDescription + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", skiDescription='" + skiDescription + '\'' +
                ", skuId='" + skuId + '\'' +
                ", creditsType='" + creditsType + '\'' +
                ", costType='" + costType + '\'' +
                ", usageStartTime='" + usageStartTime + '\'' +
                ", usageEndTime='" + usageEndTime + '\'' +
                ", usageAmount=" + usageAmount +
                ", usageAmountUnits='" + usageAmountUnits + '\'' +
                ", cost=" + cost +
                ", creditsAmount=" + creditsAmount +
                '}';
    }
}
