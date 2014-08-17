CREATE TABLE PUBLIC.dials_feature(
    feature_id INT IDENTITY PRIMARY KEY,
    feature_name VARCHAR(128) NOT NULL,
    is_enabled BIT NOT NULL
);    


CREATE TABLE PUBLIC.dials_feature_execution(
    execution_id INT IDENTITY  PRIMARY KEY,
    feature_id INT,
    attempts INT,
    executions INT,
    errors INT
);   


CREATE TABLE PUBLIC.dials_feature_filter(
    feature_filter_id INT IDENTITY PRIMARY KEY,
    feature_id INT,
    filter_name VARCHAR(128) NOT NULL
); 


CREATE TABLE PUBLIC.dials_feature_filter_static_data(
    feature_filter_id INT,
    data_key VARCHAR(128),
    data_value VARCHAR(256)
);               


ALTER TABLE PUBLIC.dials_feature_execution ADD CONSTRAINT PUBLIC.FK_Dials_Feature_Execution_Dials_Feature FOREIGN KEY(feature_id) REFERENCES PUBLIC.dials_feature(feature_id);              
ALTER TABLE PUBLIC.dials_feature_filter ADD CONSTRAINT PUBLIC.FK_Dials_Feature_Filter_Dials_Feature FOREIGN KEY(feature_id) REFERENCES PUBLIC.dials_feature(feature_id) NOCHECK;         
ALTER TABLE PUBLIC.dials_feature_filter_static_data ADD CONSTRAINT PUBLIC.FK_Dials_Feature_Filter_Static_Data_Dials_Feature_Filter FOREIGN KEY(feature_filter_id) REFERENCES PUBLIC.dials_feature_filter(feature_filter_id) NOCHECK;            
