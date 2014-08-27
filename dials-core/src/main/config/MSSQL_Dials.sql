CREATE TABLE dials_feature(
    feature_id INT IDENTITY PRIMARY KEY,
    feature_name VARCHAR(128) NOT NULL,
    is_enabled BIT NOT NULL,
    killswitch_threshold INT NULL,
    CONSTRAINT U_Dials_Feature_Feature_Name UNIQUE (feature_name)
);    

GO


CREATE TABLE dials_feature_execution(    
    feature_id INT PRIMARY KEY,
    attempts INT DEFAULT 0 NOT NULL,
    executions INT DEFAULT 0 NOT NULL,
    errors INT DEFAULT 0 NOT NULL,
    CONSTRAINT FK_Dials_Feature_Execution_Dials_Feature FOREIGN KEY(feature_id) REFERENCES dials_feature(feature_id)
);   

GO


CREATE TABLE dials_feature_filter(
    feature_filter_id INT IDENTITY PRIMARY KEY,
    feature_id INT NOT NULL,
    filter_name VARCHAR(128) NOT NULL,
    CONSTRAINT FK_Dials_Feature_Filter_Dials_Feature FOREIGN KEY(feature_id) REFERENCES dials_feature(feature_id)    
); 

GO


CREATE TABLE dials_feature_filter_static_data(
    feature_filter_static_data_id INT IDENTITY PRIMARY KEY,
    feature_filter_id INT NOT NULL,
    data_key VARCHAR(128) NOT NULL,
    data_value VARCHAR(256) NOT NULL,
    CONSTRAINT FK_Dials_Feature_Filter_Static_Data_Dials_Feature_Filter FOREIGN KEY(feature_filter_id) REFERENCES dials_feature_filter(feature_filter_id),
    CONSTRAINT U_Dials_Feature_Filter_Static_Data_Feature_Filter_Id_Data_Key UNIQUE (feature_filter_id, data_key)
);               

GO


CREATE TABLE dials_feature_filter_dial( 
    feature_filter_id INT NOT NULL PRIMARY KEY,
    frequency INT NOT NULL,
    attempts INT DEFAULT 0 NOT NULL,
    increase_threshold INT NOT NULL,
    increase_pattern VARCHAR(128) NOT NULL,
    decrease_threshold INT NOT NULL,
    decrease_pattern VARCHAR(128) NOT NULL,
    CONSTRAINT FK_DIALS_FEATURE_FILTER_DIAL_DIALS_FEATURE_FILTER FOREIGN KEY(feature_filter_id) REFERENCES dials_feature_filter(feature_filter_id)
);  

GO
      

CREATE PROCEDURE [dbo].[usp_Create_Feature]
                @featureName varchar(128),
                @isEnabled bit = 0,
                @killswitchThreshold int = NULL,
                @featureId int OUTPUT
AS
BEGIN
    insert into dials_feature (feature_name, is_enabled, killswitch_threshold) values (@featureName, @isEnabled, @killswitchThreshold)
    set @featureId = SCOPE_IDENTITY()
    insert into dials_feature_execution (feature_id, attempts, executions, errors) values (@featureId, 0, 0, 0) 
END

GO


CREATE PROCEDURE [dbo].[usp_Create_Filter]
                @featureId int, 
                @filterName varchar(128),
                @filterId int OUTPUT                    
AS
BEGIN
    insert into dials_feature_filter (feature_id, filter_name) values (@featureId, @filterName)
    set @filterId = SCOPE_IDENTITY()
END


GO


CREATE PROCEDURE [dbo].[usp_Create_Static_Data]
                @featureFilterId int,
                @dataKey varchar(128),
                @dataValue varchar(256)
AS
BEGIN
    insert into dials_feature_filter_static_data (feature_filter_id, data_key, data_value) values (@featureFilterId, @dataKey, @dataValue)
END

GO


CREATE PROCEDURE [dbo].[usp_Create_Filter_Dial]
                @featureFilterId int,
                @frequency int = 100,
                @increaseThreshold int,
                @increasePattern varchar(128),
                @decreaseThreshold int,
                @decreasePattern varchar(128)
AS
BEGIN
    insert into dials_feature_filter_dial (feature_filter_id, frequency, attempts, increase_threshold, increase_pattern, decrease_threshold, decrease_pattern)
    values (@featureFilterId, @frequency, 0, @increaseThreshold, @increasePattern, @decreaseThreshold, @decreasePattern)
END

GO


