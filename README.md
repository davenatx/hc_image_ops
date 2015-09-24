# hc_image_ops
The purpose of this project is to identify cropped images (image width is greater than image length) and overlay the image onto a standard size page.  This is accomplished by reading the "HC" image repository and inserting a database record representing each TIFF file.  This step is necessary because we need to use SQL to investigate this data to determine how to complete this project.  For example, we need to determine which images are "cropped".  This is most likely going to be images where the length is less than the width.  Next we need to determine what the default length of the template image should be we overlay the cropped image onto.  

All of the images presently in the "HC" image repository are read and stored in the accompanying H2 database.  

##Database 

This database has one table, IMAGE_RECORDS.  This table has the following fields:

* FNAME - File Name
* FPATH - File Path
* FILEDATE - File Date created by the image repository path. e.g. .../YYYY/MM/DD
* COMPRESSION - TIFF compression
* IMG_WIDTH - Pixel Width of Image
* IMG_LENGTH - Pixel Length of Image
* X_RESOLUTION - X Resolution in DPI
* Y_RESOLUTION - Y Resolution in DPI

##SQL

Below is some helpful SQL for investigating this database:

This SQL creates an index on the IMG_LENGTH field and orders it in descending order.  It is necessary because it helps H2 perform order by operations:

````
CREATE INDEX IDX_IMG_LENTH_DESC ON IMAGE_RECORDS(IMG_LENGTH DESC);  
````

Query all the records and order by IMG_LENGTH in descending order:

````
SELECT * FROM IMAGE_RECORDS ORDER BY IMG_LENGTH DESC;
````
