package com.app.domain;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CsvData {
    @CsvBindByName
    private  String ID;
    @CsvBindByName
    private  String MO_ID;
    @CsvBindByName
    private  String STIME;
    @CsvBindByName
    private  String CSIMMASS;
    @CsvBindByName
    private  String REJCSIMMASS;
    @CsvBindByName
    private  String DISCIMMASS;
    @CsvBindByName
    private  String PSIMMASS;
    @CsvBindByName
    private  String REJPSIMMASS;
    @CsvBindByName
    private  String DISCIMMASSCS;
    @CsvBindByName
    private  String FTP_TIME;
    @CsvBindByName
    private  String NAME;
    @CsvBindByName
    private  String NAME_MO_ID;
}
