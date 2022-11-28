package com.gherald.springboot.model;

import javax.persistence.*;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer lineNumber;

    @Column(columnDefinition = "LONGTEXT")
    private String code;

    private Float riskScore;

    @Column(columnDefinition = "LONGTEXT")
    private String riskTokens;

    @ManyToOne
    private File file;

    @ManyToOne
    private Change change;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Change getChange() {
        return change;
    }

    public void setChange(Change change) {
        this.change = change;
    }
}
