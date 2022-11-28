package com.gherald.springboot.dto;

public class LineDto {

    private Integer lineNumber;

    private String code;

    private Float riskScore;

    private String riskTokens;

    public LineDto(Integer lineNumber, String code, Float riskScore, String riskTokens) {
        this.lineNumber = lineNumber;
        this.code = code;
        this.riskScore = riskScore;
        this.riskTokens = riskTokens;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Float getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Float riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskTokens() {
        return riskTokens;
    }

    public void setRiskTokens(String riskTokens) {
        this.riskTokens = riskTokens;
    }
}
