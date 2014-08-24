CREATE TABLE dials_feature(
    feature_id INT IDENTITY PRIMARY KEY,
    feature_name VARCHAR(128) NOT NULL,
    is_enabled BIT NOT NULL,
    killswitch_threshold INT NULL
);    


CREATE TABLE dials_feature_execution(
    feature_execution_id INT IDENTITY  PRIMARY KEY,
    feature_id INT,
    attempts INT DEFAULT 0 not null,
    executions INT DEFAULT 0 not null,
    errors INT DEFAULT 0 not null
);   


CREATE TABLE dials_feature_filter(
    feature_filter_id INT IDENTITY PRIMARY KEY,
    feature_id INT,
    filter_name VARCHAR(128) NOT NULL
); 


CREATE TABLE dials_feature_filter_static_data(
    feature_filter_static_data_id INT IDENTITY PRIMARY KEY,
    feature_filter_id INT,
    data_key VARCHAR(128) not null,
    data_value VARCHAR(256) not null
);               

CREATE TABLE dials_feature_filter_dial(
    feature_filter_dial_id INT IDENTITY PRIMARY KEY,
    feature_filter_id INT NOT NULL,
    frequency INT NOT NULL,
    attempts INT DEFAULT 0 NOT NULL,
    increase_threshold INT,
    increase_pattern VARCHAR(128),
    decrease_threshold INT,
    decrease_pattern VARCHAR(128)
);  

ALTER TABLE dials_feature_execution ADD CONSTRAINT FK_Dials_Feature_Execution_Dials_Feature FOREIGN KEY(feature_id) REFERENCES dials_feature(feature_id);              
ALTER TABLE dials_feature_filter ADD CONSTRAINT FK_Dials_Feature_Filter_Dials_Feature FOREIGN KEY(feature_id) REFERENCES dials_feature(feature_id);    
ALTER TABLE dials_feature_filter_dial ADD CONSTRAINT FK_DIALS_FEATURE_FILTER_DIAL_DIALS_FEATURE_FILTER FOREIGN KEY(feature_filter_id) REFERENCES dials_feature_filter(feature_filter_id);             
ALTER TABLE dials_feature_filter_static_data ADD CONSTRAINT FK_Dials_Feature_Filter_Static_Data_Dials_Feature_Filter FOREIGN KEY(feature_filter_id) REFERENCES dials_feature_filter(feature_filter_id);            