declare @featureId int
declare @filterId int

exec usp_Create_Feature 'TestFeature', 1, 50, @featureId OUTPUT

exec usp_Create_Filter @featureId, 'percentage', @filterId OUTPUT

exec usp_Create_Static_Data @filterId, 'percentage', '50'

exec usp_Create_Filter_Dial @filterId, 100, 90, '1', 60, '-1'