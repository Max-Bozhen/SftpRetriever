package com.app.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FileModel {
    private  String file;
    private  LocalDateTime downloadTime;
    private  String src;
    private  String srcName;
    private  LocalDateTime parsedTime;
    private  double srcFileSize;
    private  List<CsvData> csvData;
}
